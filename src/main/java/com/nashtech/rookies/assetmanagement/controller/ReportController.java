/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.Report.ReportResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.ReportService;

import lombok.AllArgsConstructor;

/**
 *
 * @author HP
 * @author Tamina
 */
@RestController
@RequestMapping("/api/v1/reports")
@AllArgsConstructor
public class ReportController {

    private final ReportService service;

    @GetMapping("/view")
    public ResponseEntity<ResponseDto<PageableDto<List<ReportResponse>>>> getReportWithPagination(Pageable pageable, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(service.getReportWithPagination(pageable, requestUser));
    }

    @GetMapping("/export")
    public ResponseEntity<ResponseDto<List<ReportResponse>>> getReport(Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(service.getReport(requestUser));
    }

}
