/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CreateAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.EditAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.AssetRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.response.AssetResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.AssetMapper;
import com.nashtech.rookies.assetmanagement.mapper.UserMapper;
import com.nashtech.rookies.assetmanagement.repository.AssetRepository;
import com.nashtech.rookies.assetmanagement.repository.CategoryRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.service.AssetService;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.nashtech.rookies.assetmanagement.specifications.AssestSpecification.filterSpecs;

/**
 *
 * @author HP
 * @author Tamina
 */
@Service
@AllArgsConstructor
public class AssetServiceImpl implements AssetService {

    private CategoryRepository categoryRepository;
    private AssetRepository repository;
    private AssetMapper mapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public ResponseDto getAll(AssetRequestDTO requestParams, Pageable pageable, UserDetailsDto requestUser) {
        Specification<Asset> specs = filterSpecs(requestParams.getCategories(), requestParams.getSearch());
        
        Page<AssetResponseDto> result = repository.findAll(specs, pageable).map(mapper::entityToDto);
        
        PageableDto page = PageableDto.builder()
                .content(result.getContent())
                .currentPage(result.getNumber())
                .totalPage(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .build();
        return new ResponseDto(page, "Get All Assets Successfully");
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



    @Override
    public ResponseDto<AssetResponseDto> editAsset(EditAssetRequest request, UserDetailsDto requestUser) {
        Asset asset = repository.findAssetByAssetCode(request.getAssetCode())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not exists!"));
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsernameAndStatus(username, StatusConstant.ACTIVE)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return userMapper.entityToUserDetailsDto(user);
    }
}
