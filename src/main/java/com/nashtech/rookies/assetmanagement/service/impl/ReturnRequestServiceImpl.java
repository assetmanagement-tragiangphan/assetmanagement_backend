/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.ReturnAsset.ReturnAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.ReturnRequest.ReturnRequestRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ReturnAssetRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.response.ReturnRequestResponseDTO;
import com.nashtech.rookies.assetmanagement.entity.ReturnRequest;
import com.nashtech.rookies.assetmanagement.entity.ReturnRequest_;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.ReturnAssetMapper;
import com.nashtech.rookies.assetmanagement.repository.AssignmentRepository;
import com.nashtech.rookies.assetmanagement.repository.ReturnRequestRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.service.AssignmentService;
import com.nashtech.rookies.assetmanagement.service.ReturnRequestService;
import com.nashtech.rookies.assetmanagement.specifications.ReturnRequestSpecification;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author HP
 * @author Tamina
 */
@Service
@AllArgsConstructor
public class ReturnRequestServiceImpl implements ReturnRequestService {

    public ReturnRequestRepository repository;
    public AssignmentService assignmentService;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final ReturnAssetMapper returnAssetMapper;

    @Override
    public ResponseDto getAll(ReturnRequestRequestDTO requestParams, Pageable pageable, UserDetailsDto requestUser) {
        Specification<ReturnRequest> specs = ReturnRequestSpecification.filterSpecs(requestUser.getLocation(), requestParams.getSearch(), requestParams.getReturnedDate(), requestParams.getState());
        PageRequest pageRequest = (PageRequest) pageable;
        if (pageRequest.getSort().equals(Sort.unsorted())) {
            pageRequest = pageRequest.withSort(Sort.Direction.ASC, ReturnRequest_.ID);
        }
        Page<ReturnRequest> returnRequest = repository.findAll(specs, pageRequest);
        List<ReturnRequestResponseDTO> list = new ArrayList();
        for (ReturnRequest rr : returnRequest) {
            ReturnRequestResponseDTO response = new ReturnRequestResponseDTO(
                    rr.getId(),
                    rr.getAssignment().getAsset().getAssetCode(),
                    rr.getAssignment().getAsset().getName(),
                    rr.getAuditMetadata().getCreatedBy().getUsername(),
                    rr.getAuditMetadata().getCreatedOn().toLocalDate().toString(),
                    rr.getAcceptedBy().getUsername(),
                    rr.getReturnedDate().toString(),
                    rr.getStatus());
            list.add(response);
        }
        PageableDto page = new PageableDto(list, returnRequest.getNumber(), returnRequest.getTotalPages(), returnRequest.getTotalElements());
        return ResponseDto.builder().data(page).message("Get All Return Request Succesfully").build();
    }

    @Override
    public ResponseDto cancelOne(Integer id, UserDetailsDto requestUser) {
        ReturnRequest returnRequest = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Return Request Does Not Exist"));
        if (!returnRequest.getStatus().equals(StatusConstant.WAITING_FOR_RETURNING)) {
            throw new BadRequestException("Cannot Cancel Return Request Because It Is Completed");
        }
        repository.delete(returnRequest);
        return ResponseDto.builder().data(null).message("Cancel Return Request Succesfully").build();
    }

    @Override
    public ResponseDto completeOne(Integer id, UserDetailsDto requestUser) {
        ReturnRequest returnRequest = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Return Request Does Not Exist"));
        if (!returnRequest.getStatus().equals(StatusConstant.WAITING_FOR_RETURNING)) {
            throw new BadRequestException("Cannot Completed Return Request Because It Is Completed");
        }
        returnRequest.setStatus(StatusConstant.COMPLETED);
        returnRequest.setReturnedDate(LocalDate.now());
        returnRequest.getAssignment().setStatus(StatusConstant.INACTIVE);
        returnRequest.getAssignment().getAsset().setStatus(StatusConstant.AVAILABLE);
        repository.save(returnRequest);
        return ResponseDto.builder().data(null).message("Copmlete Return Request Succesfully").build();
    }

    @Override
    public ResponseDto createReturnRequest(ReturnAssetRequest request, UserDetailsDto requestUser) {
        var assignment = assignmentService.updateAssignmentStatus(request.getAssignmentId(), StatusConstant.WAITING_FOR_RETURNING);
        var requestedUser = userRepository.findById(requestUser.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        var isRequested = repository.findByAssignmentId(request.getAssignmentId());
        if (isRequested.isPresent()) throw new ResourceAlreadyExistException("Assignment is requested");
        var returnRequest = ReturnRequest.builder()
                .requestedBy(requestedUser)
                .assignment(assignment)
                .status(StatusConstant.WAITING_FOR_ACCEPTANCE)
                .build();
        returnRequest = repository.save(returnRequest);
        return ResponseDto.<ReturnAssetRequestDTO>builder()
                .data(returnAssetMapper.entityToDto(returnRequest))
                .message("Create request successfully.")
                .build();
    }

}
