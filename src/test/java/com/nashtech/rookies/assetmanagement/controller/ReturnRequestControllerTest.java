package com.nashtech.rookies.assetmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.ReturnRequest.ReturnRequestRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ReturnRequestResponseDTO;
import com.nashtech.rookies.assetmanagement.entity.ReturnRequest;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.service.ReturnRequestService;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author HP
 * @author Tamina
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ReturnRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReturnRequestService returnRequestService;

    @MockBean
    private Authentication authentication;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecurityContext securityContext;

    private UserDetailsDto userDetailsDto;

    private ReturnRequest returnRequest1;
    private ReturnRequest returnRequest2;
    private ReturnRequest returnRequest3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        userDetailsDto = UserDetailsDto.builder()
                .roleName(RoleConstant.ADMIN)
                .location(LocationConstant.HCM)
                .build();

    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testGetAll_WhenPagination_ThenReturnListReturnRequestAndStatusOK() throws Exception {

        ReturnRequestRequestDTO request = new ReturnRequestRequestDTO("user1", List.of(StatusConstant.ACTIVE), LocalDate.of(2021, 1, 1));

        ReturnRequestResponseDTO returnRequestResponseDTO1 = new ReturnRequestResponseDTO(1, "Pc000001", "Personal Computer", "user1", "2020-01-01", "user1", "2021-01-01", StatusConstant.ACTIVE);
        ReturnRequestResponseDTO returnRequestResponseDTO2 = new ReturnRequestResponseDTO(2, "Pc000001", "Personal Computer", "user1", "2020-01-01", "user1", "2021-01-01", StatusConstant.ACTIVE);
        ReturnRequestResponseDTO returnRequestResponseDTO3 = new ReturnRequestResponseDTO(3, "Pc000001", "Personal Computer", "user1", "2020-01-01", "user1", "2021-01-01", StatusConstant.ACTIVE);

        List<ReturnRequestResponseDTO> returnRequestList = Arrays.asList(returnRequestResponseDTO1, returnRequestResponseDTO2, returnRequestResponseDTO3);

        Sort sort = Sort.by(Sort.Direction.ASC, "auditMetadata.createdOn");

        Pageable pageable = PageRequest.of(0, 20, sort);

        Page<ReturnRequest> returnRequestPage = new PageImpl(returnRequestList, pageable, 20);

        PageableDto page = PageableDto.builder()
                .content(returnRequestPage.getContent())
                .currentPage(returnRequestPage.getNumber())
                .totalPage(returnRequestPage.getTotalPages())
                .totalElements(returnRequestPage.getTotalElements())
                .build();

        ResponseDto responseDto = ResponseDto.builder()
                .data(page)
                .message("Get All Return Request Succesfully")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(returnRequestService.getAll(any(ReturnRequestRequestDTO.class), any(Pageable.class), any(UserDetailsDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/return-request")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDetailsDto))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Get All Return Request Succesfully")));

        // Verify service method invocation
        verify(returnRequestService, times(1))
                .getAll(any(ReturnRequestRequestDTO.class), any(Pageable.class), any(UserDetailsDto.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testDeleteReturnRequest_WhenValidInput_ThenSuccess() throws Exception {
        ResponseDto response = ResponseDto.builder().data(null).message("Cancel Return Request Succesfully").build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(returnRequestService.cancelOne(1, userDetailsDto)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/return-request/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDetailsDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Cancel Return Request Succesfully")));
        // Verify service method invocation
        verify(returnRequestService, times(1))
                .cancelOne(1, userDetailsDto);
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testDeleteReturnRequest_WhenNotFound_ThenThrowResourceNotFoundException() throws Exception {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(returnRequestService.cancelOne(1, userDetailsDto)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/return-request/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDetailsDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // Verify service method invocation
        verify(returnRequestService, times(1))
                .cancelOne(1, userDetailsDto);
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testDeleteReturnRequest_WhenStatusIsWaitingForReturning_ThenThrowBadRequestException() throws Exception {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(returnRequestService.cancelOne(1, userDetailsDto)).thenThrow(BadRequestException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/return-request/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDetailsDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Verify service method invocation
        verify(returnRequestService, times(1))
                .cancelOne(1, userDetailsDto);
    }
}
