package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CreateAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.EditAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssetResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.AssetMapper;
import com.nashtech.rookies.assetmanagement.mapper.UserMapper;
import com.nashtech.rookies.assetmanagement.repository.AssetRepository;
import com.nashtech.rookies.assetmanagement.repository.CategoryRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AssetServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetMapper assetMapper;

    @InjectMocks
    private AssetServiceImpl assetService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testSaveAsset_WhenRequestValid_ThenReturnSuccessMessage() {
        CreateAssetRequest request = CreateAssetRequest.builder()
                .categoryName("Laptop")
                .assetName("Asset Laptop")
                .specification("Specification")
                .assetState(StatusConstant.NOT_AVAILABLE)
                .installDate(LocalDate.now())
                .build();

        UserDetailsDto requestUser = UserDetailsDto.builder()
                .roleName(RoleConstant.valueOf("ADMIN"))
                .location(LocationConstant.HCM)
                .build();

        Category category = new Category();
        category.setPrefix("LA");

        when(categoryRepository.findCategoryByName(anyString())).thenReturn(category);
        when(assetRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.empty());

        Asset asset = new Asset();
        asset.setId(1);
        asset.setAssetCode("LA000001");
        asset.setName("Asset Laptop");
        asset.setSpecification("Specification");
        asset.setStatus(StatusConstant.NOT_AVAILABLE);
        asset.setInstalledDate(LocalDate.now());
        when(assetRepository.saveAndFlush(any(Asset.class))).thenReturn(asset);

        AssetResponseDto responseDto = new AssetResponseDto("LA000001", "Asset Laptop", category.getName(), "NOT_AVAILABLE");

        when(assetMapper.entityToDto(any(Asset.class))).thenReturn(responseDto);

        ResponseDto<AssetResponseDto> response = assetService.saveAsset(request, requestUser);

        assertNotNull(response);
        assertEquals("Create Asset successfully.", response.getMessage());
        verify(assetRepository, times(1)).saveAndFlush(any(Asset.class));
    }

    @Test
    public void testEditAsset_WhenRequestValid_ThenReturnSuccessMessage() {
        Asset asset = Asset.builder()
                .id(1)
                .assetCode("LA000001")
                .name("Laptop")
                .build();

        EditAssetRequest request = EditAssetRequest.builder()
                .assetCode("LA000001")
                .assetName("Personal Computer")
                .specification("Specification")
                .assetState(StatusConstant.NOT_AVAILABLE)
                .installDate(LocalDate.now())
                .build();

        UserDetailsDto requestUser = UserDetailsDto.builder()
                .roleName(RoleConstant.valueOf("ADMIN"))
                .location(LocationConstant.HCM)
                .build();

        Category category = new Category();
        category.setPrefix("LA");

        when(categoryRepository.findCategoryByName(anyString())).thenReturn(category);
        when(assetRepository.findAssetByAssetCode(request.getAssetCode())).thenReturn(Optional.of(asset));
        when(assetRepository.saveAndFlush(any(Asset.class))).thenReturn(asset);

        AssetResponseDto responseDto = new AssetResponseDto("LA000001", "Edited Asset", category.getName(), "NOT_AVAILABLE");

        when(assetMapper.entityToDto(any(Asset.class))).thenReturn(responseDto);

        ResponseDto<AssetResponseDto> response = assetService.editAsset(request, requestUser);

        assertNotNull(response);
        assertEquals("Update Asset successfully.", response.getMessage());
        verify(assetRepository, times(1)).findAssetByAssetCode(anyString());
        verify(assetRepository, times(1)).saveAndFlush(any(Asset.class));
    }

    @Test
    public void testSaveAsset_WhenAssetNotExist_ThenThrowResourceNotFoundException() {
        EditAssetRequest request = new EditAssetRequest();
        request.setAssetCode("LA00001");

        UserDetailsDto requestUser = UserDetailsDto.builder()
                .roleName(RoleConstant.valueOf("ADMIN"))
                .location(LocationConstant.HCM)
                .build();

        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            assetService.editAsset(request, requestUser);
        });

        verify(assetRepository, times(1)).findAssetByAssetCode(anyString());
        verify(assetRepository, times(0)).saveAndFlush(any(Asset.class));
    }
}
