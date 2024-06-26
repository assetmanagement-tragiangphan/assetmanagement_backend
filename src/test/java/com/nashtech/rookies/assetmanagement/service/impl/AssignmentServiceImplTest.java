package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.CreateAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.EditAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Assignment;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.AssignmentMapper;
import com.nashtech.rookies.assetmanagement.repository.AssetRepository;
import com.nashtech.rookies.assetmanagement.repository.AssignmentRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssignmentServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private AssignmentMapper assignmentMapper;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    private CreateAssignmentRequest createRequest;
    private EditAssignmentRequest editRequest;
    private UserDetailsDto userDetails;
    private Asset asset;
    private User assignee;
    private Assignment assignment;
    private Assignment updateAssignment;

    @BeforeEach
    public void setUp() {
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

        updateAssignment = Assignment.builder()
                .id(1)
                .asset(asset)
                .assignee(assignee)
                .assignedDate(LocalDate.now())
                .note("Updated assignment")
                .status(StatusConstant.WAITING_FOR_ACCEPTANCE)
                .build();


        createRequest = CreateAssignmentRequest.builder()
                .assetCode("LA000001")
                .staffCode(assignee.getStaffCode())
                .assignDate(LocalDate.now())
                .note("Test assignment")
                .build();

        editRequest = EditAssignmentRequest.builder()
                .assetCode("LA000001")
                .staffCode(assignee.getStaffCode())
                .note("Updated assignment")
                .build();

        userDetails = UserDetailsDto.builder()
                .id(1)
                .username("test")
                .roleName(RoleConstant.ADMIN)
                .status(StatusConstant.ACTIVE)
                .build();
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testSaveAssignment_WhenValidInput_ThenReturnAssignmentAndMessageSuccess() {
        when(assetRepository.existsByAssetCode(anyString())).thenReturn(true);
        when(assetRepository.findByAssetCodeAndStatus(anyString(), any(StatusConstant.class))).thenReturn(Optional.of(asset));
        when(userRepository.findByStaffCode(createRequest.getStaffCode())).thenReturn(Optional.of(assignee));
        when(assignmentRepository.saveAndFlush(any(Assignment.class))).thenReturn(assignment);
        when(assignmentMapper.entityToDto(any(Assignment.class), any(UserDetailsDto.class))).thenReturn(mapToAssignmentResponse(assignment, userDetails));

        ResponseDto<AssignmentResponse> response = assignmentService.saveAssignment(createRequest, userDetails);

        assertEquals("Assignment create successfully.", response.getMessage());
        assertEquals("LA000001", response.getData().getAssetCode());
        assertEquals("Test assignment", response.getData().getNote());
        assertEquals(StatusConstant.WAITING_FOR_ACCEPTANCE, response.getData().getStatus());

        verify(assetRepository, times(1)).saveAndFlush(any(Asset.class));
        verify(assignmentRepository, times(1)).saveAndFlush(any(Assignment.class));
    }

    @Test
    public void testSaveAssignment_WhenAssetNotFound_ThenThrowResourceNotFoundException() {
        when(assetRepository.existsByAssetCode(anyString())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> assignmentService.saveAssignment(createRequest, userDetails));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testEditAssignment_WhenSameAssetAndValidInput_ThenReturnAssignmentAndMessageSuccess() {
        when(assignmentRepository.existsById(anyInt())).thenReturn(true);
        when(assignmentRepository.findByIdAndStatusEquals(anyInt(), eq(StatusConstant.WAITING_FOR_ACCEPTANCE))).thenReturn(Optional.of(updateAssignment));
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.of(asset));
        when(userRepository.findByStaffCode(anyString())).thenReturn(Optional.of(assignee));
        when(assignmentRepository.saveAndFlush(any(Assignment.class))).thenReturn(assignment);
        when(assignmentMapper.entityToDto(any(Assignment.class), any(UserDetailsDto.class))).thenReturn(mapToAssignmentResponse(updateAssignment, userDetails));

        ResponseDto<AssignmentResponse> response = assignmentService.editAssignment(1, editRequest, userDetails);

        assertEquals("Assignment update successfully.", response.getMessage());
        assertEquals("LA000001", response.getData().getAssetCode());
        assertEquals("Updated assignment", response.getData().getNote());
        assertEquals(StatusConstant.WAITING_FOR_ACCEPTANCE, response.getData().getStatus());

        verify(assetRepository, times(1)).saveAndFlush(any(Asset.class));
        verify(assignmentRepository, times(1)).saveAndFlush(any(Assignment.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testEditAssignment_WhenNewAssetAndValidInput_ThenReturnAssignmentAndMessageSuccess() {
        Asset newAsset = new Asset();
        newAsset.setAssetCode("LA000002");
        newAsset.setStatus(StatusConstant.AVAILABLE);

        editRequest = EditAssignmentRequest.builder()
                .assetCode(newAsset.getAssetCode())
                .staffCode(assignee.getStaffCode())
                .note("Updated assignment")
                .build();

        when(assignmentRepository.existsById(anyInt())).thenReturn(true);
        when(assignmentRepository.findByIdAndStatusEquals(anyInt(), eq(StatusConstant.WAITING_FOR_ACCEPTANCE))).thenReturn(Optional.of(updateAssignment));
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.of(newAsset));
        when(userRepository.findByStaffCode(anyString())).thenReturn(Optional.of(assignee));
        when(assignmentRepository.saveAndFlush(any(Assignment.class))).thenReturn(assignment);
        when(assignmentMapper.entityToDto(any(Assignment.class), any(UserDetailsDto.class))).thenReturn(mapToAssignmentResponse(updateAssignment, userDetails));

        ResponseDto<AssignmentResponse> response = assignmentService.editAssignment(1, editRequest, userDetails);

        assertEquals("Assignment update successfully.", response.getMessage());
        assertEquals("LA000001", response.getData().getAssetCode());
        assertEquals("Updated assignment", response.getData().getNote());
        assertEquals(StatusConstant.WAITING_FOR_ACCEPTANCE, response.getData().getStatus());

        verify(assetRepository, times(1)).saveAndFlush(any(Asset.class));
        verify(assignmentRepository, times(1)).saveAndFlush(any(Assignment.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testEditAssignment_WhenAssetNotFound_ThenThrowResourceNotFoundException() {
        when(assignmentRepository.existsById(anyInt())).thenReturn(true);
        when(assignmentRepository.findByIdAndStatusEquals(anyInt(), eq(StatusConstant.WAITING_FOR_ACCEPTANCE))).thenReturn(Optional.of(new Assignment()));
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> assignmentService.editAssignment(1, editRequest, userDetails));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testEditAssignment_WhenAssignmentNotFound_ThenThrowResourceNotFoundException() {
        when(assignmentRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> assignmentService.editAssignment(1, editRequest, userDetails));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testEditAssignment_WhenAssetAlreadyAssigned_ThenThrowResourceAlreadyExistException() {
        Asset assignedAsset = Asset.builder()
                .assetCode("LA000003")
                .status(StatusConstant.ASSIGNED)
                .build();

        editRequest = EditAssignmentRequest.builder()
                .assetCode(assignedAsset.getAssetCode())
                .staffCode("SD0009")
                .note("Updated assignment")
                .build();

        when(assignmentRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findByStaffCode(editRequest.getStaffCode())).thenReturn(Optional.of(assignee));
        when(assignmentRepository.findByIdAndStatusEquals(anyInt(), eq(StatusConstant.WAITING_FOR_ACCEPTANCE))).thenReturn(Optional.of(assignment));
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.of(assignedAsset));
        when(assetRepository.existsByAssetCodeAndStatus(assignedAsset.getAssetCode(), StatusConstant.ASSIGNED)).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class, () -> assignmentService.editAssignment(1, editRequest, userDetails));
    }

    private AssignmentResponse mapToAssignmentResponse(Assignment assignment, UserDetailsDto userDetails) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .assetCode(assignment.getAsset().getAssetCode())
                .assetName(assignment.getAsset().getName())
                .assignBy(userDetails.getUsername())
                .assignTo(assignment.getAssignee().getUsername())
                .assignedDate(assignment.getAssignedDate())
                .note(assignment.getNote())
                .status(assignment.getStatus())
                .build();
    }
}
