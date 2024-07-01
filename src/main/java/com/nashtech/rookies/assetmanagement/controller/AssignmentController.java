package com.nashtech.rookies.assetmanagement.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.AssignmentGetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.CreateAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.EditAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentDetailResponse;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.AssignmentService;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/assignments")
@AllArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @GetMapping()
    public ResponseEntity<ResponseDto<PageableDto<List<AssignmentDetailResponse>>>> getAssignmentsDetails(AssignmentGetRequest request, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(assignmentService.getAssignmentDetails(request, pageable));
    }

    @GetMapping("/own")
    public ResponseEntity<ResponseDto<PageableDto<List<AssignmentDetailResponse>>>> getOwnAssignmentsDetails(Pageable pageable, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(assignmentService.getOwnAssignmentDetails(requestUser, pageable));
    }
    
    @PostMapping()
    public ResponseEntity<ResponseDto<AssignmentResponse>> createAssignment(@RequestBody CreateAssignmentRequest request, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.saveAssignment(request, requestUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto<AssignmentResponse>> editAssignment(@PathVariable("id") Integer id, EditAssignmentRequest request, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto)authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(assignmentService.editAssignment(id, request, requestUser));
    }

    @PatchMapping("/response/{id}")
    public ResponseEntity<ResponseDto<AssignmentResponse>> responseOwnAssignment(@PathVariable("id") Integer id, StatusConstant status, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto)authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(assignmentService.responseAssignment(id, status, requestUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAssigment(@PathVariable("id") Integer id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(assignmentService.deleteAssignment(id));
    }
}
