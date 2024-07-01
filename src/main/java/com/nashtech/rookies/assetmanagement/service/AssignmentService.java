package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.CreateAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.EditAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;

public interface AssignmentService {
    ResponseDto<AssignmentResponse> saveAssignment(CreateAssignmentRequest request, UserDetailsDto requestUser);
    ResponseDto<AssignmentResponse> editAssignment(Integer id, EditAssignmentRequest request, UserDetailsDto requestUser);
    ResponseDto deleteAssignment(Integer id);
}
