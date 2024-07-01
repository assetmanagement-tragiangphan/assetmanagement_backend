/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CreateAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.AssetRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.EditAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssetHistoryDTO;
import com.nashtech.rookies.assetmanagement.dto.response.AssetResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.AssetWithHistoryResponseDTO;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Asset_;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.AssetMapper;
import com.nashtech.rookies.assetmanagement.repository.AssetRepository;
import com.nashtech.rookies.assetmanagement.repository.AssignmentRepository;
import com.nashtech.rookies.assetmanagement.repository.CategoryRepository;
import com.nashtech.rookies.assetmanagement.service.AssetService;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static com.nashtech.rookies.assetmanagement.specifications.AssetSpecification.filterSpecs;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @author HP
 * @author Tamina
 */
@Service
@AllArgsConstructor
public class AssetServiceImpl implements AssetService {

    private CategoryRepository categoryRepository;
    private AssetRepository repository;
    private AssetMapper mapper;
    private AssignmentRepository assignmentRepository;

    @Override
    public ResponseDto getAll(AssetRequestDTO requestParams, Pageable pageable, UserDetailsDto requestUser) {
        Specification<Asset> specs = filterSpecs(requestUser.getLocation(), requestParams.getCategories(), requestParams.getSearch(), requestParams.getStates());

        PageRequest pageRequest = (PageRequest) pageable;
        if (pageRequest.getSort().equals(Sort.unsorted())) {
            pageRequest = pageRequest.withSort(Sort.Direction.ASC, Asset_.ASSET_CODE);
        }

        Page<AssetResponseDto> result = repository.findAll(specs, pageRequest).map(mapper::entityToDto);

        PageableDto page = PageableDto.builder()
                .content(result.getContent())
                .currentPage(result.getNumber())
                .totalPage(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .build();
        return new ResponseDto(page, "Get All Assets Successfully");
    }

    @Override
    public ResponseDto getOne(String requestParams) {
        Asset result = repository.findAssetByAssetCode(requestParams).orElseThrow(() -> new ResourceNotFoundException("Asset does not exists!"));
        return new ResponseDto(mapper.entityToDto(result), "Get All Assets Successfully");
    }

    @Override
    public ResponseDto getOneWithHistory(String requestParams, int page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        Asset asset = repository.findAssetByAssetCode(requestParams).orElseThrow(() -> new ResourceNotFoundException("Asset does not exists!"));
        Page<AssetHistoryDTO> historyList = repository.findAssetHistory(requestParams, pageRequest);
        AssetResponseDto assetResponseDto = mapper.entityToDto(asset);
        AssetWithHistoryResponseDTO response = new AssetWithHistoryResponseDTO(assetResponseDto, historyList);
        return new ResponseDto(response, "Get All Assets History Successfully");
    }

    @Override
    public ResponseDto getHistoryOfOneAsset(String requestParams, Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<AssetHistoryDTO> historyList = repository.findAssetHistory(requestParams, pageRequest);
        return new ResponseDto(historyList, "Get History of Asset Successfully");

    }

    @Override
    public ResponseDto<AssetResponseDto> saveAsset(CreateAssetRequest request, UserDetailsDto requestUser) {
        Asset asset = new Asset();
        Category category = categoryRepository.findCategoryByName(request.getCategoryName());
        String assetCode = genAssetCode(request);
        asset.setAssetCode(assetCode);
        asset.setName(request.getAssetName());
        asset.setCategory(category);
        asset.setSpecification(request.getSpecification());
        asset.setStatus(request.getAssetState());
        asset.setInstalledDate(request.getInstallDate());
        asset.setLocation(requestUser.getLocation());
        asset = repository.saveAndFlush(asset);
        return ResponseDto.<AssetResponseDto>builder()
                .data(mapper.entityToDto(asset))
                .message("Create Asset successfully.")
                .build();
    }

    private String genAssetCode(CreateAssetRequest request) {
        String categoryPrefix = categoryRepository.findCategoryByName(request.getCategoryName()).getPrefix();

        Optional<Asset> optionalAsset = repository.findFirstByOrderByIdDesc();
        int newAssetNumber = optionalAsset.map(asset -> {
            String lastAssetNumberStr = asset.getAssetCode().substring(asset.getAssetCode().length() - 6);
            return Integer.parseInt(lastAssetNumberStr) + 1;
        }).orElse(1);

        return categoryPrefix + String.format("%06d", newAssetNumber);
    }

    public ResponseDto<AssetResponseDto> editAsset(String assetCode, EditAssetRequest request) {
        Asset asset = repository.findAssetByAssetCode(assetCode)
                .orElseThrow(() -> new ResourceNotFoundException("Asset does not exists!"));
        if (Objects.requireNonNull(asset.getStatus()) == StatusConstant.ASSIGNED) {
            throw new ResourceAlreadyExistException("Asset is not available to edit!");
        }
        asset.setName(request.getAssetName());
        asset.setSpecification(request.getSpecification());
        asset.setStatus(request.getAssetState());
        asset.setInstalledDate(request.getInstallDate());
        asset = repository.saveAndFlush(asset);
        return ResponseDto.<AssetResponseDto>builder()
                .data(mapper.entityToDto(asset))
                .message("Update Asset successfully.")
                .build();

    }

    @Override
    public ResponseDto deleteAsset(String assetCode) {
        Asset asset = repository.findAssetByAssetCode(assetCode).orElseThrow(() -> new ResourceNotFoundException("Asset does not exists!"));
        int assetId = asset.getId();
        if (asset.getStatus().equals(StatusConstant.ASSIGNED)) {
            throw new BadRequestException("Cannot delete the asset because it's being assigned");
        }
        if (assignmentRepository.existsByAssetId(assetId)) {
            throw new BadRequestException("Cannot delete the asset because it belongs to one or more historical assignments");
        }
        repository.delete(asset);
        return ResponseDto.builder()
                .data(null)
                .message("Delete Asset Successfully.")
                .build();
    }

}
