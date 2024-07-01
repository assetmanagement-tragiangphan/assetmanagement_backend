package com.nashtech.rookies.assetmanagement.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.AssignmentGetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.CreateAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.EditAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentDetailResponse;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Asset_;
import com.nashtech.rookies.assetmanagement.entity.Assignment;
import com.nashtech.rookies.assetmanagement.entity.Assignment_;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.AssignmentMapper;
import com.nashtech.rookies.assetmanagement.repository.AssetRepository;
import com.nashtech.rookies.assetmanagement.repository.AssignmentRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.service.AssignmentService;
import com.nashtech.rookies.assetmanagement.specifications.AssignmentSpecification;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository repository;
    private final AssignmentMapper mapper;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseDto<PageableDto<List<AssignmentDetailResponse>>> getAssignmentDetails(AssignmentGetRequest request, Pageable pageable) {
        Specification<Assignment> specs = AssignmentSpecification.filterSpecs(request);

        PageRequest pageRequest = (PageRequest) pageable;
        String sortField = pageRequest.getSort().toString().split(":")[0].trim().toLowerCase();
        String sortType = pageRequest.getSort().toString().split(":")[1].trim();
        log.info("field " + sortField);
        log.info("type " + sortType);

        if (pageRequest.getSort().equals(Sort.unsorted())) {
            log.info("1 " + sortField);
            pageRequest = pageRequest.withSort(Direction.ASC, Assignment_.ASSET + "." + Asset_.ASSET_CODE);
        }
        if (sortType.equals("ASC")) {
            if (sortField.equals("assetcode")) {
                log.info("2 " + sortField);
                pageRequest = pageRequest.withSort(Direction.ASC, Assignment_.ASSET + "." + Asset_.ASSET_CODE);
            }
            if (sortField.equals("assetname")) {
                log.info("3 " + sortField);
                pageRequest = pageRequest.withSort(Direction.ASC, Assignment_.ASSET + "." + Asset_.NAME);
            }
        } else {
            if (sortField.equals("assetcode")) {
                log.info("2 " + sortField);
                pageRequest = pageRequest.withSort(Direction.DESC, Assignment_.ASSET + "." + Asset_.ASSET_CODE);
            }
            if (sortField.equals("assetname")) {
                log.info("3 " + sortField);
                pageRequest = pageRequest.withSort(Direction.DESC, Assignment_.ASSET + "." + Asset_.NAME);
            }
        }

        Page<AssignmentDetailResponse> assignmentDetails = repository.findAll(specs, pageRequest)
                .map(res -> mapper.entityToDetailDto(res, res.getAuditMetadata().getCreatedBy(), res.getAsset()));

        PageableDto<List<AssignmentDetailResponse>> pages = PageableDto.<List<AssignmentDetailResponse>>builder()
                .content(assignmentDetails.getContent())
                .currentPage(assignmentDetails.getNumber())
                .totalPage(assignmentDetails.getTotalPages())
                .totalElements(assignmentDetails.getTotalElements())
                .build();

        return ResponseDto.<PageableDto<List<AssignmentDetailResponse>>>builder()
                .data(pages)
                .message("Successfully retrieved assignment details.")
                .build();
    }

    @Override
    public ResponseDto<PageableDto<List<AssignmentDetailResponse>>> getOwnAssignmentDetails(UserDetailsDto requestUser, Pageable pageable) {
        Specification<Assignment> specs = AssignmentSpecification.ownSpecs(requestUser);

        PageRequest pageRequest = (PageRequest) pageable;
        String sortField = pageRequest.getSort().toString().split(":")[0].trim().toLowerCase();
        String sortType = pageRequest.getSort().toString().split(":")[1].trim();

        if (sortType.equals("ASC")) {
            if (sortField.equals("assetcode")) {
                log.info("2 " + sortField);
                pageRequest = pageRequest.withSort(Direction.ASC, Assignment_.ASSET + "." + Asset_.ASSET_CODE);
            }
            if (sortField.equals("assetname")) {
                log.info("3 " + sortField);
                pageRequest = pageRequest.withSort(Direction.ASC, Assignment_.ASSET + "." + Asset_.NAME);
            }
        } else {
            if (sortField.equals("assetcode")) {
                log.info("2 " + sortField);
                pageRequest = pageRequest.withSort(Direction.DESC, Assignment_.ASSET + "." + Asset_.ASSET_CODE);
            }
            if (sortField.equals("assetname")) {
                log.info("3 " + sortField);
                pageRequest = pageRequest.withSort(Direction.DESC, Assignment_.ASSET + "." + Asset_.NAME);
            }
        }

        Page<AssignmentDetailResponse> assignmentDetails = repository.findAll(specs, pageRequest)
                .map(res -> mapper.entityToDetailDto(res, res.getAuditMetadata().getCreatedBy(), res.getAsset()));

        PageableDto<List<AssignmentDetailResponse>> pages = PageableDto.<List<AssignmentDetailResponse>>builder()
                .content(assignmentDetails.getContent())
                .currentPage(assignmentDetails.getNumber())
                .totalPage(assignmentDetails.getTotalPages())
                .totalElements(assignmentDetails.getTotalElements())
                .build();

        return ResponseDto.<PageableDto<List<AssignmentDetailResponse>>>builder()
                .data(pages)
                .message("Successfully retrieved assignment details.")
                .build();
    }

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
    public ResponseDto<AssignmentResponse> responseAssignment(Integer id, StatusConstant status,
            UserDetailsDto requestUser) {
        Assignment assignment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment does not exist."));
        switch (status) {
            case ACCEPTED -> {
                assignment.setStatus(StatusConstant.ACCEPTED);
                assignment = repository.saveAndFlush(assignment);
                return ResponseDto.<AssignmentResponse>builder()
                        .data(mapper.entityToDto(assignment, requestUser))
                        .message("Assignment accepted successfully.")
                        .build();
            }
            case DECLINED -> {
                assignment.setStatus(StatusConstant.DECLINED);
                Asset asset = assignment.getAsset();
                asset.setStatus(StatusConstant.AVAILABLE);
                assignment = repository.saveAndFlush(assignment);
                return ResponseDto.<AssignmentResponse>builder()
                        .data(mapper.entityToDto(assignment, requestUser))
                        .message("Assignment declined successfully.")
                        .build();
            }
            case WAITING_FOR_ACCEPTANCE -> {
                assignment.setStatus(StatusConstant.WAITING_FOR_ACCEPTANCE);
                assignment = repository.saveAndFlush(assignment);
                return ResponseDto.<AssignmentResponse>builder()
                        .data(mapper.entityToDto(assignment, requestUser))
                        .message("Assignment is waiting for returning.")
                        .build();
            }
            case WAITING_FOR_RETURNING -> {
                assignment.setStatus(StatusConstant.WAITING_FOR_RETURNING);
                assignment = repository.saveAndFlush(assignment);
                return ResponseDto.<AssignmentResponse>builder()
                        .data(mapper.entityToDto(assignment, requestUser))
                        .message("Assignment is waiting for returning.")
                        .build();
            }
            default -> {
                return null;
            }
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
