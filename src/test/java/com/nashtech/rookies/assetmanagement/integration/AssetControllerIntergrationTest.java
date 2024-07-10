/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.AssetRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CreateAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.EditAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.AuthenticationRequest;
import com.nashtech.rookies.assetmanagement.dto.response.LoginResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.AuthenticationService;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 *
 * @author HP
 * @author Tamina
 */
@ActiveProfiles("test")
@SpringBootTest()
@AutoConfigureMockMvc
public class AssetControllerIntergrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    AuthenticationService authenticationService;
    
    private CreateAssetRequest createAssetRequest;
    private EditAssetRequest editAssetRequest;
    private AssetRequestDTO assetRequestDTO;

    private String jwtToken = "";

    Cookie cookie;

    @BeforeEach
    void setUp() {

        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("nhatt")
                .password("Nhat@123")
                .build();
        ResponseDto<LoginResponse> token = authenticationService.authenticate(request, "");
        jwtToken = token.getData().getToken();

        cookie = new Cookie("asset-management-server", jwtToken);

        assetRequestDTO = AssetRequestDTO.builder()
                .search("")
                .states(List.of("AVAILABLE", "NOT_AVAILABLE"))
                .categories(List.of(1L))
                .build();

        createAssetRequest = CreateAssetRequest.builder()
                .assetName("Test Intergration Create")
                .categoryName("iPad")
                .specification("Specs")
                .installDate(LocalDate.now())
                .assetState(StatusConstant.AVAILABLE)
                .build();

        editAssetRequest = EditAssetRequest.builder()
                .assetName("Test Intergration Edit")
                .specification("Specs Updated")
                .installDate(LocalDate.now())
                .assetState(StatusConstant.AVAILABLE)
                .build();
    }

    @Test
    public void testGetAll_WhenPagination_ThenReturnListAssetAndStatusOK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(assetRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Get All Assets Successfully")));
    }

    @Test
    public void testGetOne_WhenAssetCodeExists_ThenReturnListAssetAndStatusOK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/assets/{assetCode}", "PC000001")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(assetRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Get All Assets Successfully")));

    }

    @Test
    public void testCreateAsset_WhenValidInput_ThenReturnAssetAndMessageSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(createAssetRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Create Asset successfully.")));
    }

    @Test
    public void testEditAsset_WhenValidInput_ThenReturnAssetAndMessageSuccess() throws Exception {
        String assetCode = "PC000005";
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/assets/{assetCode}", assetCode)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(editAssetRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Update Asset successfully.")));
    }

    @Test
    public void testDeleteAsset_WhenValidInput_ThenSuccess() throws Exception {
        String assetCode = "MB000066";

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/assets/{assetCode}", assetCode)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Delete Asset Successfully.")));

    }

    @Test
    public void testDeleteAsset_WhenAssignedOrHasHistory_ThenThrowBadRequestException() throws Exception {
        String assetCode = "PC000003";

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/assets/{assetCode}", assetCode)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

}
