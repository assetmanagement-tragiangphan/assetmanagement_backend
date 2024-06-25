package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.AssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Assignment;
import com.nashtech.rookies.assetmanagement.entity.AuditMetadata;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.AssignmentMapper;
import com.nashtech.rookies.assetmanagement.repository.AssetRepository;
import com.nashtech.rookies.assetmanagement.repository.AssignmentRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.service.AssignmentService;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {
    private AssignmentRepository repository;
    private AssignmentMapper mapper;
    private AssetRepository assetRepository;
    private UserRepository userRepository;

    @Override
    public ResponseDto<AssignmentResponse> saveAssignment(AssignmentRequest request, UserDetailsDto requestUser) {
        Asset asset = assetRepository.findAssetByAssetCodeAndStatus(request.getAssetCode(), StatusConstant.AVAILABLE).orElseThrow(() -> new ResourceNotFoundException("Asset Not Found Or Not Available"));
        User assignee = userRepository.findByStaffCode(request.getStaffCode()).orElseThrow(() -> new ResourceNotFoundException("Assignee Not Found"));
        Assignment assignment = new Assignment();
        assignment.setAssignedDate(request.getAssignDate());
        assignment.setNote(request.getNote());
        assignment.setAsset(asset);
        assignment.setAssignee(assignee);
        assignment.setStatus(StatusConstant.WAITING_FOR_ACCEPTANCE);
        assignment = repository.saveAndFlush(assignment);
        asset.setStatus(StatusConstant.ASSIGNED);
        assetRepository.saveAndFlush(asset);
        return ResponseDto.<AssignmentResponse>builder()
                .data(mapper.entityToDto(assignment, requestUser))
                .message("Assignment create successfully.")
                .build();
    }

    @Override
    public ResponseDto<AssignmentResponse> editAssignment(AssignmentRequest request, UserDetailsDto requestUser) {
        return null;
    }
}
