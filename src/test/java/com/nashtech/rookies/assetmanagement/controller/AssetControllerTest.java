package com.nashtech.rookies.assetmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CreateAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.EditAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssetResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.AssetService;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssetController.class)
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetService assetService;

    @MockBean
    private Authentication authentication;

    private UserDetailsDto userDetailsDto;

    @BeforeEach
    void setUp() {
        userDetailsDto = new UserDetailsDto();
        userDetailsDto.setLocation(LocationConstant.HCM);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(userDetailsDto);
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void createAsset_ShouldReturnCreatedStatus() throws Exception {
        // Arrange
        CreateAssetRequest request = new CreateAssetRequest();
        request.setCategoryName("Electronics");
        request.setAssetName("Laptop");
        request.setSpecification("Intel i5");
        request.setAssetState(StatusConstant.AVAILABLE);
        request.setInstallDate(LocalDate.parse("2024-06-24"));

        AssetResponseDto assetResponseDto = new AssetResponseDto();
        ResponseDto<AssetResponseDto> responseDto = ResponseDto.<AssetResponseDto>builder()
                .data(assetResponseDto)
                .message("Create Asset successfully.")
                .build();

        when(assetService.saveAsset(request, userDetailsDto)).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto))
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Create Asset successfully."));
    }

    @Test
    void editAsset_ShouldReturnOkStatus() throws Exception {
        // Arrange
        EditAssetRequest request = new EditAssetRequest();
        request.setAssetCode("EL000001");
        request.setAssetName("Updated Laptop");
        request.setSpecification("Intel i7");
        request.setAssetState(StatusConstant.ASSIGNED);
        request.setInstallDate(LocalDate.parse("2024-06-24"));

        AssetResponseDto assetResponseDto = new AssetResponseDto();
        ResponseDto<AssetResponseDto> responseDto = ResponseDto.<AssetResponseDto>builder()
                .data(assetResponseDto)
                .message("Update Asset successfully.")
                .build();

        when(assetService.editAsset(request)).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update Asset successfully."));
    }
}
