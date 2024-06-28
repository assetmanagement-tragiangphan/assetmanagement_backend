package com.nashtech.rookies.assetmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CategoryRequest;
import com.nashtech.rookies.assetmanagement.dto.response.CategoryResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.CategoryService;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private UserDetailsDto userDetailsDto;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        categoryResponse = new CategoryResponse(1, "CN", "Category Name");

        userDetailsDto = UserDetailsDto.builder()
                .roleName(RoleConstant.ADMIN)
                .location(LocationConstant.HCM)
                .build();
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testCreateCategory_WhenValidInput_ThenReturnCategoryAndMessageSuccess() throws Exception {
        CategoryRequest request = new CategoryRequest("Category Name");

        ResponseDto<CategoryResponse> responseDto = ResponseDto.<CategoryResponse>builder()
                .data(categoryResponse)
                .message("Create Category successfully.")
                .build();

        when(categoryService.saveCategory(any(CategoryRequest.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.prefix").value(categoryResponse.getPrefix()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(categoryResponse.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Create Category successfully."));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testGetCategories_WhenCategoryExists_ThenReturnCategoryList() throws Exception {
        ResponseDto<List<CategoryResponse>> responseDto = ResponseDto.<List<CategoryResponse>>builder()
                .data(Collections.singletonList(categoryResponse))
                .build();

        when(categoryService.getAllCategory()).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories")
                        .with(user(userDetailsDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].prefix").value(categoryResponse.getPrefix()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value(categoryResponse.getName()));
    }
}
