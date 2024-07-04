/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.ReturnRequest.ReturnRequestRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ReturnRequestResponseDTO;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Assignment;
import com.nashtech.rookies.assetmanagement.entity.AuditMetadata;
import com.nashtech.rookies.assetmanagement.entity.ReturnRequest;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.repository.ReturnRequestRepository;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 *
 * @author HP
 * @author Tamina
 */
@ExtendWith(MockitoExtension.class)
public class ReturnRequestServiceImplTest {

    @Mock
    private ReturnRequestRepository returnRequestRepository;

    @InjectMocks
    private ReturnRequestServiceImpl returnRequestService;

    private UserDetailsDto userDetails;

    private ReturnRequest returnRequest1;
    private ReturnRequest returnRequest2;
    private ReturnRequest returnRequest3;
    private ReturnRequestRequestDTO returnRequestRequestDTO;
    private User user;
    private User assignee;
    private Assignment assignment;
    private Asset asset;
    List<ReturnRequest> returnRequestList;
    List<ReturnRequestResponseDTO> returnRequestReponseList;
    ReturnRequestResponseDTO returnRequestResponseDTO1;
    ReturnRequestResponseDTO returnRequestResponseDTO2;
    ReturnRequestResponseDTO returnRequestResponseDTO3;
    AuditMetadata auditMetadata;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("user1");
        asset = Asset.builder()
                .assetCode("LA000001")
                .status(StatusConstant.AVAILABLE)
                .build();

        assignee = User.builder()
                .staffCode("SD0009")
                .build();
        assignment = Assignment.builder()
                .id(1)
                .asset(asset)
                .assignee(assignee)
                .assignedDate(LocalDate.now())
                .note("Test assignment")
                .status(StatusConstant.WAITING_FOR_ACCEPTANCE)
                .build();
        auditMetadata = new AuditMetadata();
        auditMetadata.setCreatedBy(user);
        auditMetadata.setCreatedOn(LocalDateTime.now());
        auditMetadata.setUpdatedBy(user);
        auditMetadata.setUpdatedOn(LocalDateTime.now());

        returnRequest1 = new ReturnRequest(LocalDate.of(2021, 1, 1), user, user, assignment, auditMetadata);
        returnRequest1.setStatus(StatusConstant.WAITING_FOR_RETURNING);
        
        returnRequest2 = new ReturnRequest(LocalDate.of(2021, 1, 1), user, user, assignment, auditMetadata);
        returnRequest2.setStatus(StatusConstant.COMPLETED);
        
        returnRequest3 = new ReturnRequest(LocalDate.of(2021, 1, 1), user, user, assignment, auditMetadata);

        returnRequestRequestDTO = new ReturnRequestRequestDTO("", List.of(StatusConstant.ACTIVE), LocalDate.now());

        returnRequestResponseDTO1 = new ReturnRequestResponseDTO(1, "Pc000001", "Personal Computer", "user1", "2020-01-01", "user1", "2021-01-01", StatusConstant.ACTIVE);
        returnRequestResponseDTO2 = new ReturnRequestResponseDTO(2, "Pc000001", "Personal Computer", "user1", "2020-01-01", "user1", "2021-01-01", StatusConstant.ACTIVE);
        returnRequestResponseDTO3 = new ReturnRequestResponseDTO(3, "Pc000001", "Personal Computer", "user1", "2020-01-01", "user1", "2021-01-01", StatusConstant.ACTIVE);

        returnRequestReponseList = new ArrayList();
        returnRequestReponseList.add(returnRequestResponseDTO1);
        returnRequestReponseList.add(returnRequestResponseDTO2);
        returnRequestReponseList.add(returnRequestResponseDTO3);

        returnRequestList = new ArrayList();
        returnRequestList.add(returnRequest1);
        returnRequestList.add(returnRequest2);
        returnRequestList.add(returnRequest3);

        userDetails = UserDetailsDto.builder()
                .id(1)
                .username("test")
                .roleName(RoleConstant.ADMIN)
                .status(StatusConstant.ACTIVE)
                .build();
    }

    @Test
    void testGetAllReturnRequest_whenInputParameters_thenReturnPage() {

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.unsorted());

        Page<ReturnRequest> page = new PageImpl<>(returnRequestList, pageRequest, returnRequestList.size());

        PageableDto PageResult = new PageableDto(returnRequestReponseList, page.getNumber(), page.getTotalPages(), page.getTotalElements());

        when(returnRequestRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        // Act
        ResponseDto<PageableDto> result = returnRequestService.getAll(returnRequestRequestDTO, pageRequest, userDetails);

        // Assert
        assertEquals(result.getData().getCurrentPage(), 0);
        assertEquals(result.getData().getTotalElements(), 3);
        assertEquals(result.getData().getTotalPage(), 1);
        assertEquals(result.getMessage(), "Get All Return Request Succesfully");

        verify(returnRequestRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
    }

    @Test
    void testCancelReturnRequest_WhenStatusIsWaitingForReturning_ThenSuccess() {
        when(returnRequestRepository.findById(anyInt())).thenReturn(Optional.of(returnRequest1));
        doNothing().when(returnRequestRepository).delete(any(ReturnRequest.class));

        ResponseDto response = returnRequestService.cancelOne(1, userDetails);
        Assertions.assertNotNull(response);
        assertEquals("Cancel Return Request Succesfully", response.getMessage());
        verify(returnRequestRepository, times(1)).delete(any(ReturnRequest.class));
    }

    @Test
    void testCancelReturnRequest_WhenNotFound_ThenThrowResourceNotFoundException() {
        when(returnRequestRepository.findById(anyInt())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> returnRequestService.cancelOne(1, userDetails));
        assertEquals("Return Request Does Not Exist", exception.getMessage());
    }
//

    @Test
    void testCancelReturnRequest_WhenWaitingForReturnedStatus_ThenThrowBadRequestException() {
        when(returnRequestRepository.findById(anyInt())).thenReturn(Optional.of(returnRequest2));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            returnRequestService.cancelOne(1, userDetails);
        });
        assertEquals("Cannot Cancel Return Request Because It Is Completed", exception.getMessage());
    }

    @Test
    void testCompleteReturnRequest_WhenStatusIsWaitingForReturning_ThenSuccess() {
        when(returnRequestRepository.findById(anyInt())).thenReturn(Optional.of(returnRequest1));
        when(returnRequestRepository.save(any(ReturnRequest.class))).thenReturn(returnRequest1);

        ResponseDto response = returnRequestService.completeOne(1, userDetails);

        Assertions.assertNotNull(response);
        assertEquals("Copmlete Return Request Succesfully", response.getMessage());
        verify(returnRequestRepository, times(1)).save(any(ReturnRequest.class));
    }

    @Test
    void testCompleteReturnRequest_WhenNotFound_ThenThrowResourceNotFoundException() {
        when(returnRequestRepository.findById(anyInt())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> returnRequestService.completeOne(1, userDetails));
        assertEquals("Return Request Does Not Exist", exception.getMessage());
    }
//

    @Test
    void testCompleteReturnRequest_WhenCopmletedStatus_ThenThrowBadRequestException() {
        when(returnRequestRepository.findById(anyInt())).thenReturn(Optional.of(returnRequest2));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            returnRequestService.completeOne(1, userDetails);
        });
        assertEquals("Cannot Completed Return Request Because It Is Completed", exception.getMessage());
    }
}
