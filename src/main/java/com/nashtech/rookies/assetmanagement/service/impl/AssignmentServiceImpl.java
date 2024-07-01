package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.CreateAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.EditAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Assignment;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
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
    public ResponseDto<AssignmentResponse> saveAssignment(CreateAssignmentRequest request, UserDetailsDto requestUser) {
        if (assetRepository.existsByAssetCode(request.getAssetCode())) {
            Asset asset = assetRepository.findByAssetCodeAndStatus(request.getAssetCode(), StatusConstant.AVAILABLE).orElseThrow(() -> new ResourceAlreadyExistException("Asset Not Available to assign"));
            User assignee = userRepository.findByStaffCode(request.getStaffCode()).orElseThrow(() -> new ResourceNotFoundException("Assignee Not Found"));
            Assignment assignment = new Assignment();
            assignment.setAssignedDate(request.getAssignedDate());
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
        } else {
            throw new ResourceNotFoundException("Asset not found!");
        }
    }

    @Override
    public ResponseDto<AssignmentResponse> editAssignment(Integer id, EditAssignmentRequest request, UserDetailsDto requestUser) {
        if (repository.existsById(id)) {
            Assignment assignment = repository.findByIdAndStatusEquals(id, StatusConstant.WAITING_FOR_ACCEPTANCE).orElseThrow(() -> new ResourceAlreadyExistException("Assignment is unavailable to edit!"));
            Asset asset = assetRepository.findAssetByAssetCode(request.getAssetCode()).orElseThrow(() -> new ResourceNotFoundException("Asset Not Found!"));
            User assignee = userRepository.findByStaffCode(request.getStaffCode()).orElseThrow(() -> new ResourceNotFoundException("Assignee Not Found"));
            if (asset.getAssetCode().equals(assignment.getAsset().getAssetCode())) {
                assignment.setNote(request.getNote());
                assignment.setAssignee(assignee);
                assignment.setStatus(StatusConstant.WAITING_FOR_ACCEPTANCE);
                assignment = repository.saveAndFlush(assignment);
                asset.setStatus(StatusConstant.ASSIGNED);
                assetRepository.saveAndFlush(asset);
                return ResponseDto.<AssignmentResponse>builder()
                        .data(mapper.entityToDto(assignment, requestUser))
                        .message("Assignment update successfully.")
                        .build();
            } else {
                if (assetRepository.existsByAssetCodeAndStatus(request.getAssetCode(), StatusConstant.ASSIGNED)) {
                    throw new ResourceAlreadyExistException("Asset not available to assign.");
                } else {
                    assignment.setNote(request.getNote());
                    assignment.setAsset(asset);
                    assignment.setAssignee(assignee);
                    assignment.setStatus(StatusConstant.WAITING_FOR_ACCEPTANCE);
                    assignment = repository.saveAndFlush(assignment);
                    asset.setStatus(StatusConstant.ASSIGNED);
                    assetRepository.saveAndFlush(asset);
                    return ResponseDto.<AssignmentResponse>builder()
                            .data(mapper.entityToDto(assignment, requestUser))
                            .message("Assignment update successfully.")
                            .build();
                }
            }
        } else {
            throw new ResourceNotFoundException("Assignment is not exist");
        }
    }

    @Override
    public ResponseDto deleteAssignment(Integer id) {
        if (repository.existsById(id)) {
            Assignment assignment = repository.getReferenceById(id);
            if (assignment.getStatus().equals(StatusConstant.WAITING_FOR_ACCEPTANCE) || assignment.getStatus().equals(StatusConstant.DECLINED)) {
                assignment.setStatus(StatusConstant.INACTIVE);
                assignment.getAsset().setStatus(StatusConstant.AVAILABLE);
                repository.save(assignment);
                return ResponseDto.builder()
                        .data(null)
                        .message("Delete assignment successfully")
                        .build();
            } else {
                throw new BadRequestException("Cannot delete assignment");
            }
        } else {
            throw new ResourceNotFoundException("Assignment does not exist");
        }
    }
}
