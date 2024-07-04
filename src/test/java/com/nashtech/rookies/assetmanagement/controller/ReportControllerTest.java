package com.nashtech.rookies.assetmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentDetailResponse;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.Report.ReportResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.AuditMetadata;
import com.nashtech.rookies.assetmanagement.entity.Role;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.service.AssignmentService;
import com.nashtech.rookies.assetmanagement.service.ReportService;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService service;

    @MockBean
    private Authentication authentication;

    @MockBean
    private SecurityContext securityContext;

    @Autowired
    private ObjectMapper objectMapper;
    private UserDetailsDto userDetailsDto;
    private List<ReportResponse> reports;
    List<Asset> assets;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        AuditMetadata auditMetadata = new AuditMetadata();
        auditMetadata.setCreatedBy(User.builder().auditMetadata(auditMetadata)
                .id(1)
                .username("test")
                .role(Role.builder().name(RoleConstant.ADMIN).build())
                .status(StatusConstant.ACTIVE)
                .location(LocationConstant.HCM)
                .build());

        userDetailsDto = UserDetailsDto.builder()
                .id(1)
                .username("test")
                .roleName(RoleConstant.ADMIN)
                .status(StatusConstant.ACTIVE)
                .location(LocationConstant.HCM)
                .build();

        assets = List.of(
                Asset.builder().auditMetadata(auditMetadata).status(StatusConstant.ASSIGNED).build(),
                Asset.builder().auditMetadata(auditMetadata).status(StatusConstant.ASSIGNED).build(),
                Asset.builder().auditMetadata(auditMetadata).status(StatusConstant.AVAILABLE).build(),
                Asset.builder().auditMetadata(auditMetadata).status(StatusConstant.AVAILABLE).build(),
                Asset.builder().auditMetadata(auditMetadata).status(StatusConstant.NOT_AVAILABLE).build(),
                Asset.builder().auditMetadata(auditMetadata).status(StatusConstant.WAITING_FOR_RECYCLING).build(),
                Asset.builder().auditMetadata(auditMetadata).status(StatusConstant.WAITING_FOR_RECYCLING).build(),
                Asset.builder().auditMetadata(auditMetadata).status(StatusConstant.WAITING_FOR_RECYCLING).build(),
                Asset.builder().auditMetadata(auditMetadata).status(StatusConstant.RECYCLED).build()
        );

        reports = List.of(
                ReportResponse.builder()
                        .category("Laptop").assigned(2L).available(2L)
                        .notAvailable(1L).waitingForRecycling(3L).recycled(1L)
                        .build()
        );

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void getReport() throws Exception {
        ResponseDto<List<ReportResponse>> responseDto = ResponseDto.<List<ReportResponse>>builder()
                .data(reports)
                .message("Successfully exported report statistic.")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(service.getReport(any(UserDetailsDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reports/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Successfully exported report statistic.")));

        // Verify service method invocation
        verify(service, times(1))
                .getReport(any(UserDetailsDto.class));
    }

    @Test
    void getReportWithPagination() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        Page<ReportResponse> reportPage = new PageImpl<>(reports, pageable, reports.size());

        PageableDto<List<ReportResponse>> page = PageableDto.<List<ReportResponse>>builder()
                .content(reportPage.getContent())
                .currentPage(reportPage.getNumber())
                .totalPage(reportPage.getTotalPages())
                .totalElements(reportPage.getTotalElements())
                .build();

        ResponseDto<PageableDto<List<ReportResponse>>> responseDto = ResponseDto.<PageableDto<List<ReportResponse>>>builder()
                .data(page)
                .message("Successfully retrieved report statistic.")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(service.getReportWithPagination(any(Pageable.class), any(UserDetailsDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reports/view")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Successfully retrieved report statistic.")));

        // Verify service method invocation
        verify(service, times(1))
                .getReportWithPagination(any(Pageable.class), any(UserDetailsDto.class));
    }
}