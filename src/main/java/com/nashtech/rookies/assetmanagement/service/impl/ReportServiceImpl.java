package com.nashtech.rookies.assetmanagement.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.Report.ReportResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.repository.ReportRepository;
import com.nashtech.rookies.assetmanagement.service.ReportService;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository repository;

    @Override
    public ResponseDto<PageableDto<List<ReportResponse>>> getReportWithPagination(Pageable pageable, UserDetailsDto requestUser) {
        PageRequest pageRequest = (PageRequest) pageable;
        if (pageRequest.getSort().equals(Sort.unsorted())) {
            pageRequest = pageRequest.withSort(Sort.Direction.ASC, "category");
        }

        Page<ReportResponse> reports = repository.reportStatistic(requestUser.getId(), pageRequest);
        PageableDto<List<ReportResponse>> pages = PageableDto.<List<ReportResponse>>builder()
                .content(reports.getContent())
                .currentPage(reports.getNumber())
                .totalPage(reports.getTotalPages())
                .totalElements(reports.getTotalElements())
                .build();

        return ResponseDto.<PageableDto<List<ReportResponse>>>builder()
                .data(pages)
                .message("Successfully retrieved report statistic.")
                .build();
    }

    @Override
    public ResponseDto<List<ReportResponse>> getReport(UserDetailsDto requestUser) {
        List<ReportResponse> reports = repository.exportReportStatistic(requestUser.getId());
        return ResponseDto.<List<ReportResponse>>builder()
                .data(reports)
                .message("Successfully exported report statistic.")
                .build();
    }
}
