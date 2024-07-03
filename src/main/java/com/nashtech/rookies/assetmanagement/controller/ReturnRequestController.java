/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.controller;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.ReturnRequest.ReturnRequestRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.ReturnRequestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author HP
 * @author Tamina
 */
@RestController
@RequestMapping("/api/v1/return-request")
@AllArgsConstructor
public class ReturnRequestController {

    private final ReturnRequestService service;

    @GetMapping
    public ResponseEntity getAll(@Valid ReturnRequestRequestDTO requestParams, Pageable pageable, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto) authentication.getPrincipal();
        ResponseDto returnRequests = service.getAll(requestParams, pageable, requestUser);
        return ResponseEntity.ok().body(returnRequests);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity cancelOne(@PathVariable("id") Integer id, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto) authentication.getPrincipal();
        ResponseDto returnRequests = service.cancelOne(id, requestUser);
        return ResponseEntity.status(HttpStatus.OK).body(returnRequests);
    }
    
}
