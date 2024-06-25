package com.nashtech.rookies.assetmanagement.controller;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.AssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.AssignmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assignments")
@AllArgsConstructor
public class AssignmentController {
    private AssignmentService assignmentService;

    @PostMapping()
    public ResponseEntity<ResponseDto<AssignmentResponse>> createAssigment(AssignmentRequest request, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto)authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.saveAssignment(request, requestUser));
    }
}
