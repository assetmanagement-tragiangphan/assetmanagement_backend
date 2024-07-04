package com.nashtech.rookies.assetmanagement.service;

import java.util.List;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;

import org.springframework.data.domain.Pageable;

import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.Report.ReportResponse;

public interface ReportService {
    public ResponseDto<List<ReportResponse>> getReport(UserDetailsDto requestUser);
    public ResponseDto<PageableDto<List<ReportResponse>>> getReportWithPagination(Pageable pageable, UserDetailsDto requestUser);
}
