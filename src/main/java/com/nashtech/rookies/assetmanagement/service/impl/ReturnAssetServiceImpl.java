package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.ReturnAsset.ReturnAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ReturnAssetRequestDTO;
import com.nashtech.rookies.assetmanagement.entity.ReturnRequest;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.ReturnAssetMapper;
import com.nashtech.rookies.assetmanagement.repository.AssignmentRepository;
import com.nashtech.rookies.assetmanagement.repository.ReturnRequestRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.service.ReturnAssetService;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReturnAssetServiceImpl implements ReturnAssetService {
    private final ReturnRequestRepository returnRequestRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final ReturnAssetMapper returnAssetMapper;

    @Override
    public ResponseDto<ReturnAssetRequestDTO> createReturnRequest(ReturnAssetRequest request, UserDetailsDto requestUser) {
        var assignment = assignmentRepository.findById(request.getAssignmentId()).orElseThrow(() -> new ResourceNotFoundException("Assignment is not exist"));
        var requestedUser = userRepository.findById(requestUser.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        var isRequested = returnRequestRepository.findByAssignmentId(request.getAssignmentId());
        if (isRequested.isPresent()) throw new ResourceAlreadyExistException("Assignment is requested");
        var returnRequest = ReturnRequest.builder()
                .requestedBy(requestedUser)
                .assignment(assignment)
                .status(StatusConstant.WAITING_FOR_ACCEPTANCE)
                .build();
        returnRequest = returnRequestRepository.save(returnRequest);
        return ResponseDto.<ReturnAssetRequestDTO>builder()
                .data(returnAssetMapper.entityToDto(returnRequest))
                .message("Create request successfully.")
                .build();
    }
}
