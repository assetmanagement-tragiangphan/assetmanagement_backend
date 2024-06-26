package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.AssetRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CreateAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.EditAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssetResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.AssetMapper;
import com.nashtech.rookies.assetmanagement.repository.AssetRepository;
import com.nashtech.rookies.assetmanagement.repository.AssignmentRepository;
import com.nashtech.rookies.assetmanagement.repository.CategoryRepository;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private AssetMapper assetMapper;

    @InjectMocks
    private AssetServiceImpl assetService;

    private Category category;
    private Asset asset;
    private Asset assignedAsset;
    private UserDetailsDto userDetailsDto;
    private CreateAssetRequest createAssetRequest;
    private EditAssetRequest editAssetRequest;
    private AssetRequestDTO assetRequestDTO;

    private List<Asset> assets;
    private List<AssetResponseDto> assetResponseDtos;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Electronics");
        category.setPrefix("EL");

        asset = new Asset();
        asset.setId(1);
        asset.setAssetCode("EL000001");
        asset.setName("Laptop");
        asset.setCategory(category);
        asset.setSpecification("Specs");
        asset.setStatus(StatusConstant.AVAILABLE);
        asset.setInstalledDate(LocalDate.now());

        Asset asset2 = new Asset();
        asset2.setAssetCode("EL000002");
        asset2.setName("Desktop");
        asset2.setCategory(category);
        asset2.setSpecification("Specs");
        asset2.setStatus(StatusConstant.AVAILABLE);
        asset2.setInstalledDate(LocalDate.now());

        assignedAsset = new Asset();
        assignedAsset.setId(1);
        assignedAsset.setAssetCode("EL000001");
        assignedAsset.setName("Laptop");
        assignedAsset.setCategory(category);
        assignedAsset.setSpecification("Specs");
        assignedAsset.setStatus(StatusConstant.ASSIGNED);
        assignedAsset.setInstalledDate(LocalDate.now());

        assets = List.of(asset, asset2, assignedAsset);

        assetRequestDTO = new AssetRequestDTO();
        assetRequestDTO.setSearch("Laptop");

        userDetailsDto = UserDetailsDto.builder()
                .location(LocationConstant.HCM)
                .build();

        createAssetRequest = CreateAssetRequest.builder()
                .assetName("Laptop")
                .categoryName("Electronics")
                .specification("Specs")
                .installDate(LocalDate.now())
                .assetState(StatusConstant.AVAILABLE)
                .build();

        editAssetRequest = EditAssetRequest.builder()
                .assetName("Desktop")
                .specification("Specs Updated")
                .installDate(LocalDate.now())
                .assetState(StatusConstant.AVAILABLE)
                .build();

        AssetResponseDto assetResponseDto = new AssetResponseDto();
        assetResponseDto.setAssetCode("EL000001");
        assetResponseDto.setName("Laptop");
        assetResponseDto.setCategory("Electronics");
        assetResponseDto.setSpecification("Specs");
        
        AssetResponseDto assetResponseDto2 = new AssetResponseDto();
        assetResponseDto2.setAssetCode("EL000002");
        assetResponseDto2.setName("Desktop");

        AssetResponseDto assetResponseDto3 = new AssetResponseDto();
        assetResponseDto3.setAssetCode("EL000003");
        assetResponseDto3.setName("Mouse");

        assetResponseDtos = List.of(assetResponseDto, assetResponseDto2, assetResponseDto3);
    }

    @Test
    void testGetAll_whenInputPageable_thenReturnResponseDtoPageAssetResponseDto(){
        // Arrange
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        Page<Asset> page = new PageImpl<>(assets, pageable, assets.size());
        
        when(assetRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(assetMapper.entityToDto(any(Asset.class))).thenReturn(new AssetResponseDto());

        // Act
        ResponseDto<PageableDto<List<AssetResponseDto>>> result = assetService.getAll(assetRequestDTO, pageable, userDetailsDto);

        // Assert
        assertEquals(result.getData().getTotalElements(), 3);
        assertEquals(result.getData().getContent().size(), 3);
        assertEquals(result.getMessage(), "Get All Assets Successfully");

        verify(assetRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetOneAsset_whenExistAsset_thenReturnResponseDtoAsset() {
        // Arrange
        String assetCode = "EL000001";
        when(assetRepository.findAssetByAssetCode(any(String.class))).thenReturn(Optional.of(asset));
        when(assetMapper.entityToDto(any(Asset.class))).thenReturn(new AssetResponseDto());
        // Act
        ResponseDto<AssetResponseDto> result = assetService.getOne(assetCode);

        // Assert
        assertEquals(result.getMessage(), "Get All Assets Successfully");

        verify(assetRepository, times(1)).findAssetByAssetCode(any(String.class));
    }

    @Test
    void testGetUserEntityById_whenNotExistUser_thenThrowNotFoundException() {
        // Arrange
        String assetCode = "EL000001";
        when(assetRepository.findAssetByAssetCode(any(String.class))).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> assetService.getOne(assetCode));
        verify(assetRepository, times(1)).findAssetByAssetCode(any(String.class));
    }

    @Test
    void testSaveAsset_WhenInputValid_ThenReturnResponseAndMessageSuccess() {
        when(categoryRepository.findCategoryByName(anyString())).thenReturn(category);
        when(assetRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(asset));
        when(assetRepository.saveAndFlush(any(Asset.class))).thenReturn(asset);
        when(assetMapper.entityToDto(any(Asset.class))).thenReturn(new AssetResponseDto());

        ResponseDto<AssetResponseDto> response = assetService.saveAsset(createAssetRequest, userDetailsDto);

        assertNotNull(response);
        assertEquals("Create Asset successfully.", response.getMessage());
        verify(assetRepository, times(1)).saveAndFlush(any(Asset.class));
    }

    @Test
    void testEditAsset_WhenInputValid_ThenReturnResponseAndMessageSuccess() {
        when(assetRepository.findAssetByAssetCode(asset.getAssetCode())).thenReturn(Optional.of(asset));
        when(assetRepository.saveAndFlush(any(Asset.class))).thenReturn(asset);
        when(assetMapper.entityToDto(any(Asset.class))).thenReturn(new AssetResponseDto());

        ResponseDto<AssetResponseDto> response = assetService.editAsset("EL000001", editAssetRequest);

        assertNotNull(response);
        assertEquals("Update Asset successfully.", response.getMessage());
        verify(assetRepository, times(1)).saveAndFlush(any(Asset.class));
    }

    @Test
    void testEditAsset_WhenAssetNotFound_ThenThrowResourceNotFoundException() {
        when(assetRepository.findAssetByAssetCode(asset.getAssetCode())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> assetService.editAsset("EL000001", editAssetRequest));

        assertEquals("Asset does not exists!", exception.getMessage());
    }

    @Test
    void testEditAsset_WhenAssignedStatus_ThenThrowResourceAlreadyExistException() {
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.of(assignedAsset));

        ResourceAlreadyExistException exception = assertThrows(ResourceAlreadyExistException.class,
                () -> assetService.editAsset("EL000001", editAssetRequest));

        assertEquals("Asset is not available to edit!", exception.getMessage());
    }

    @Test
    void testDeleteAsset_WhenNotAssignedAndDoesNotHasHistory_ThenSuccess() {
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.of(asset));
        when(assignmentRepository.existsByAssetId(anyInt())).thenReturn(Boolean.FALSE);

        ResponseDto<AssetResponseDto> response = assetService.deleteAsset("EL000001");

        assertNotNull(response);
        assertEquals("Delete Asset Successfully.", response.getMessage());
        verify(assetRepository, times(1)).delete(any(Asset.class));
    }

    @Test
    void testDeleteAsset_WhenNotFound_ThenThrowResourceNotFoundException() {
        when(assetRepository.findAssetByAssetCode(asset.getAssetCode())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> assetService.deleteAsset("EL000001"));
        assertEquals("Asset does not exists!", exception.getMessage());
    }

    @Test
    void testDeleteAsset_WhenAssignedStatus_ThenThrowBadRequestException() {
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.of(assignedAsset));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            assetService.deleteAsset("EL000001");
        });
        assertEquals("Cannot delete the asset because it's being assigned", exception.getMessage());
    }

    @Test
    void testDeleteAsset_WhenHasHistory_ThenThrowBadRequestException() {
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.of(asset));
        when(assignmentRepository.existsByAssetId(anyInt())).thenReturn(Boolean.TRUE);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            assetService.deleteAsset("EL000001");
        });
        assertEquals("Cannot delete the asset because it belongs to one or more historical assignments", exception.getMessage());
    }

}
