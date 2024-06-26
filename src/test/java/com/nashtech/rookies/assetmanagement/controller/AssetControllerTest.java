package com.nashtech.rookies.assetmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CreateAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.EditAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssetResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.service.AssetService;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
public class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetService assetService;

    @MockBean
    private Authentication authentication;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecurityContext securityContext;

    private UserDetailsDto userDetailsDto;
    private CreateAssetRequest createAssetRequest;
    private EditAssetRequest editAssetRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Category category = new Category();
        category.setName("Electronics");
        category.setPrefix("EL");

        Asset asset = new Asset();
        asset.setAssetCode("EL000001");
        asset.setName("Laptop");
        asset.setCategory(category);
        asset.setSpecification("Specs");
        asset.setStatus(StatusConstant.AVAILABLE);
        asset.setInstalledDate(LocalDate.now());

        userDetailsDto = UserDetailsDto.builder()
                .roleName(RoleConstant.ADMIN)
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
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testCreateAsset_WhenValidInput_ThenReturnAssetAndMessageSuccess() throws Exception {
        AssetResponseDto assetResponseDto = new AssetResponseDto();
        ResponseDto<AssetResponseDto> responseDto = ResponseDto.<AssetResponseDto>builder()
                .data(assetResponseDto)
                .message("Create Asset successfully.")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(assetService.saveAsset(createAssetRequest, userDetailsDto)).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDetailsDto))
                .content(objectMapper.writeValueAsString(createAssetRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Create Asset successfully.")));

        // Verify service method invocation
        verify(assetService, times(1))
                .saveAsset(any(CreateAssetRequest.class), any(UserDetailsDto.class));
    }

    @Test
    @WithMockUser
    public void testEditAsset_WhenValidInput_ThenReturnAssetAndMessageSuccess() throws Exception {
        String assetCode = "EL000001";
        EditAssetRequest request = new EditAssetRequest();
        AssetResponseDto assetResponseDto = new AssetResponseDto();
        ResponseDto<AssetResponseDto> responseDto = ResponseDto.<AssetResponseDto>builder()
                .data(assetResponseDto)
                .message("Update Asset successfully.")
                .build();

        when(assetService.editAsset(anyString(), any(EditAssetRequest.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/assets/{assetCode}", assetCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editAssetRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Update Asset successfully.")));

        // Verify service method invocation
        verify(assetService, times(1)).editAsset(anyString(), any(EditAssetRequest.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testDeleteAsset_WhenValidInput_ThenSuccess() throws Exception {
        String assetCode = "EL000001";
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .message("Delete Asset Successfully.")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(assetService.deleteAsset(anyString())).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/assets/{assetCode}", assetCode)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDetailsDto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Delete Asset Successfully.")));
        // Verify service method invocation
        verify(assetService, times(1))
                .deleteAsset(anyString());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testDeleteAsset_WhenAssignedOrHasHistory_ThenThrowBadRequestException() throws Exception {
        String assetCode = "EL000001";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(assetService.deleteAsset(anyString())).thenThrow(BadRequestException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/assets/{assetCode}", assetCode)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDetailsDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        
        // Verify service method invocation
        verify(assetService, times(1))
                .deleteAsset(anyString());
    }
}
