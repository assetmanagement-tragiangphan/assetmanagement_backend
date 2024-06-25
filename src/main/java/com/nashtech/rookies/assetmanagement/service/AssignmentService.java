package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.AssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;

public interface AssignmentService {
    ResponseDto<AssignmentResponse> saveAssignment(AssignmentRequest request, UserDetailsDto requestUser);
    ResponseDto<AssignmentResponse> editAssignment(AssignmentRequest request, UserDetailsDto requestUser);
}
