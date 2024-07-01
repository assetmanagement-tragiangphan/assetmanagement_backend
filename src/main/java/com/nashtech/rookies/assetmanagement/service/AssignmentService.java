package com.nashtech.rookies.assetmanagement.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.AssignmentGetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.CreateAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.EditAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentDetailResponse;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;


public interface AssignmentService {
    ResponseDto<PageableDto<List<AssignmentDetailResponse>>> getAssignmentDetails(AssignmentGetRequest request, Pageable pageable);
    ResponseDto<PageableDto<List<AssignmentDetailResponse>>> getOwnAssignmentDetails(UserDetailsDto requestUser, Pageable pageable);
    ResponseDto<AssignmentResponse> saveAssignment(CreateAssignmentRequest request, UserDetailsDto requestUser);
    ResponseDto<AssignmentResponse> editAssignment(Integer id, EditAssignmentRequest request, UserDetailsDto requestUser);
    ResponseDto<AssignmentResponse> responseAssignment(Integer id, StatusConstant status, UserDetailsDto requestUser);
    ResponseDto deleteAssignment(Integer id);
}
