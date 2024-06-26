package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CreateAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.EditAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssetResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.AssetMapper;
import com.nashtech.rookies.assetmanagement.repository.AssetRepository;
import com.nashtech.rookies.assetmanagement.repository.CategoryRepository;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
    private AssetMapper assetMapper;

    @InjectMocks
    private AssetServiceImpl assetService;

    private Category category;
    private Asset asset;
    private Asset assignedAsset;
    private UserDetailsDto userDetailsDto;
    private CreateAssetRequest createAssetRequest;
    private EditAssetRequest editAssetRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Electronics");
        category.setPrefix("EL");

        asset = new Asset();
        asset.setAssetCode("EL000001");
        asset.setName("Laptop");
        asset.setCategory(category);
        asset.setSpecification("Specs");
        asset.setStatus(StatusConstant.AVAILABLE);
        asset.setInstalledDate(LocalDate.now());

        assignedAsset = new Asset();
        assignedAsset.setAssetCode("EL000001");
        assignedAsset.setName("Laptop");
        assignedAsset.setCategory(category);
        assignedAsset.setSpecification("Specs");
        assignedAsset.setStatus(StatusConstant.ASSIGNED);
        assignedAsset.setInstalledDate(LocalDate.now());

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
}
