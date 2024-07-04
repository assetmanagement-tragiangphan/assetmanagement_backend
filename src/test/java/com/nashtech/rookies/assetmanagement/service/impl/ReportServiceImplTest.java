package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.Report.ReportResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.AuditMetadata;
import com.nashtech.rookies.assetmanagement.entity.Role;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.repository.ReportRepository;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository repository;
    @InjectMocks
    private ReportServiceImpl service;

//    private ReportResponse report;
    private List<ReportResponse> reports;
    List<Asset> assets;
    private UserDetailsDto userDetails;
    

    @BeforeEach
    void setUp() {
        userDetails = UserDetailsDto.builder()
                .id(1)
                .username("test")
                .roleName(RoleConstant.ADMIN)
                .status(StatusConstant.ACTIVE)
                .location(LocationConstant.HCM)
                .build();

        AuditMetadata auditMetadata = new AuditMetadata();
        auditMetadata.setCreatedBy(User.builder().auditMetadata(auditMetadata)
                .id(1)
                .username("test")
                .role(Role.builder().name(RoleConstant.ADMIN).build())
                .status(StatusConstant.ACTIVE)
                .location(LocationConstant.HCM)
                .build());
        
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
    void getReportWithPagination_WhenValidInput_ThenReturnPageReportAndSuccessMessage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        Page<ReportResponse> reportResponsePage = new PageImpl<>(reports, pageable, reports.size());

        when(repository.reportStatistic(anyInt(), any(Pageable.class))).thenReturn(reportResponsePage);

        ResponseDto<PageableDto<List<ReportResponse>>> response = service.getReportWithPagination(pageable, userDetails);
        assertEquals("Successfully retrieved report statistic.", response.getMessage());
        assertEquals(1, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPage());
        assertEquals(0, response.getData().getCurrentPage());
    }

    @Test
    void getReport_WhenValidInput_ThenReturnReportsAndSuccessMessage() {
        when(repository.exportReportStatistic(anyInt())).thenReturn(reports);

        ResponseDto<List<ReportResponse>> response = service.getReport(userDetails);
        assertEquals("Successfully exported report statistic.", response.getMessage());
        assertEquals(reports, response.getData());
    }
}