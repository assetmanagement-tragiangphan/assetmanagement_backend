package com.nashtech.rookies.assetmanagement.service.impl;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.entity.Role;
import com.nashtech.rookies.assetmanagement.specifications.AssignmentSpecification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.AssignmentGetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.CreateAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.EditAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentDetailResponse;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Assignment;
import com.nashtech.rookies.assetmanagement.entity.AuditMetadata;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.AssignmentMapper;
import com.nashtech.rookies.assetmanagement.repository.AssetRepository;
import com.nashtech.rookies.assetmanagement.repository.AssignmentRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.junit.jupiter.api.Assertions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AssignmentServiceImplTest {

    @Mock
    private AssignmentRepository repository;

    @Mock
    private AssignmentMapper mapper;

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
    private AuditMetadata auditMetadata;

    @BeforeEach
    void setUp() {
        userDetails = UserDetailsDto.builder()
                .id(1)
                .username("test")
                .roleName(RoleConstant.ADMIN)
                .status(StatusConstant.ACTIVE)
                .build();

        auditMetadata = new AuditMetadata();
        auditMetadata.setCreatedBy(User.builder()
                .id(1)
                .username("test")
                .role(Role.builder().name(RoleConstant.ADMIN).build())
                .status(StatusConstant.ACTIVE)
                .build());

        Category category = Category.builder()
                .name("Electronics")
                .prefix("EL")
                .build();

        asset = Asset.builder()
                .assetCode("LA000001")
                .category(category)
                .status(StatusConstant.AVAILABLE)
                .build();

        assignee = User.builder()
                .staffCode("SD0009")
                .username("test")
                .build();

        assignment = Assignment.builder()
                .id(1)
                .asset(asset)
                .assignee(assignee)
                .assignedDate(LocalDate.now())
                .auditMetadata(auditMetadata)
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
                .assignedDate(LocalDate.now())
                .note("Test assignment")
                .build();

        editRequest = EditAssignmentRequest.builder()
                .assetCode("LA000001")
                .username(assignee.getUsername())
                .note("Updated assignment")
                .build();

    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testOwnAssignmentDetails_Unsorted() {
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        List<Assignment> assignments = List.of(assignment);
        Page<Assignment> assignmentPage = new PageImpl<>(assignments, pageable, assignments.size());

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(assignmentPage);
        when(mapper.entityToDetailDto(any(Assignment.class), any(User.class), any(Asset.class)))
                .thenReturn(mapToAssignmentDetail(assignment, userDetails));

        ResponseDto<PageableDto<List<AssignmentDetailResponse>>> response = assignmentService.getOwnAssignmentDetails(userDetails, pageable);

        assertEquals("Successfully retrieved your own assignment details.", response.getMessage());
        assertEquals(1, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPage());
        assertEquals(0, response.getData().getCurrentPage());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testGetOwnAssignmentDetails_SortingByAssetCodeAscending() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "assetCode"));
        List<Assignment> assignments = List.of(assignment);
        Page<Assignment> assignmentPage = new PageImpl<>(assignments, pageable, assignments.size());

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(assignmentPage);
        when(mapper.entityToDetailDto(any(Assignment.class), any(User.class), any(Asset.class)))
                .thenReturn(mapToAssignmentDetail(assignment, userDetails));

        ResponseDto<PageableDto<List<AssignmentDetailResponse>>> response = assignmentService.getOwnAssignmentDetails(userDetails, pageable);

        assertEquals("Successfully retrieved your own assignment details.", response.getMessage());
        assertEquals(1, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPage());
        assertEquals(0, response.getData().getCurrentPage());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testGetOwnAssignmentDetails_SortingByAssetNameDescending() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "assetName"));
        List<Assignment> assignments = List.of(assignment);
        Page<Assignment> assignmentPage = new PageImpl<>(assignments, pageable, assignments.size());

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(assignmentPage);
        when(mapper.entityToDetailDto(any(Assignment.class), any(User.class), any(Asset.class)))
                .thenReturn(mapToAssignmentDetail(assignment, userDetails));

        ResponseDto<PageableDto<List<AssignmentDetailResponse>>> response = assignmentService.getOwnAssignmentDetails(userDetails, pageable);

        assertEquals("Successfully retrieved your own assignment details.", response.getMessage());
        assertEquals(1, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPage());
        assertEquals(0, response.getData().getCurrentPage());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testGetAssignmentDetails_SortingByAssetCodeAscending() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "assetCode"));
        List<Assignment> assignments = List.of(assignment);
        Page<Assignment> assignmentPage = new PageImpl<>(assignments, pageable, assignments.size());
        AssignmentGetRequest request = new AssignmentGetRequest();

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(assignmentPage);
        when(mapper.entityToDetailDto(any(Assignment.class), any(User.class), any(Asset.class)))
                .thenReturn(mapToAssignmentDetail(assignment, userDetails));
        ResponseDto<PageableDto<List<AssignmentDetailResponse>>> response = assignmentService.getAssignmentDetails(request, pageable);

        assertEquals("Successfully retrieved assignment details.", response.getMessage());
        assertEquals(1, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPage());
        assertEquals(0, response.getData().getCurrentPage());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testGetAssignmentDetails_SortingByAssetNameDescending() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "assetName"));
        List<Assignment> assignments = List.of(assignment);
        Page<Assignment> assignmentPage = new PageImpl<>(assignments, pageable, assignments.size());
        AssignmentGetRequest request = new AssignmentGetRequest();

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(assignmentPage);
        when(mapper.entityToDetailDto(any(Assignment.class), any(User.class), any(Asset.class)))
                .thenReturn(mapToAssignmentDetail(assignment, userDetails));

        ResponseDto<PageableDto<List<AssignmentDetailResponse>>> response = assignmentService.getAssignmentDetails(request, pageable);

        assertEquals("Successfully retrieved assignment details.", response.getMessage());
        assertEquals(1, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPage());
        assertEquals(0, response.getData().getCurrentPage());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testGetAssignmentDetails_Unsorted() {
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        List<Assignment> assignments = List.of(assignment);
        Page<Assignment> assignmentPage = new PageImpl<>(assignments, pageable, assignments.size());
        AssignmentGetRequest request = new AssignmentGetRequest();

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(assignmentPage);
        when(mapper.entityToDetailDto(any(Assignment.class), any(User.class), any(Asset.class)))
                .thenReturn(mapToAssignmentDetail(assignment, userDetails));

        ResponseDto<PageableDto<List<AssignmentDetailResponse>>> response = assignmentService.getAssignmentDetails(request, pageable);

        assertEquals("Successfully retrieved assignment details.", response.getMessage());
        assertEquals(1, response.getData().getTotalElements());
        assertEquals(1, response.getData().getTotalPage());
        assertEquals(0, response.getData().getCurrentPage());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testSaveAssignment_WhenValidInput_ThenReturnAssignmentAndMessageSuccess() {
        when(assetRepository.existsByAssetCode(anyString())).thenReturn(true);
        when(assetRepository.findByAssetCodeAndStatus(anyString(), any(StatusConstant.class))).thenReturn(Optional.of(asset));
        when(userRepository.findByStaffCode(createRequest.getStaffCode())).thenReturn(Optional.of(assignee));
        when(repository.saveAndFlush(any(Assignment.class))).thenReturn(assignment);
        when(mapper.entityToDto(any(Assignment.class), any(UserDetailsDto.class))).thenReturn(mapToAssignmentResponse(assignment, userDetails));

        ResponseDto<AssignmentResponse> response = assignmentService.saveAssignment(createRequest, userDetails);

        assertEquals("Assignment create successfully.", response.getMessage());
        assertEquals("LA000001", response.getData().getAssetCode());
        assertEquals("Test assignment", response.getData().getNote());
        assertEquals(StatusConstant.WAITING_FOR_ACCEPTANCE, response.getData().getStatus());

        verify(assetRepository, times(1)).saveAndFlush(any(Asset.class));
        verify(repository, times(1)).saveAndFlush(any(Assignment.class));
    }

    @Test
    void testSaveAssignment_WhenAssetNotFound_ThenThrowResourceNotFoundException() {
        when(assetRepository.existsByAssetCode(anyString())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> assignmentService.saveAssignment(createRequest, userDetails));
    }

    @Test
    void testSaveAssignment_WhenAssetNotAvailable_ThenThrowResourceAlreadyExistException() {
        when(assetRepository.existsByAssetCode(anyString())).thenReturn(true);
        when(assetRepository.findByAssetCodeAndStatus(anyString(), any(StatusConstant.class))).thenReturn(Optional.empty());

        assertThrows(ResourceAlreadyExistException.class, () -> assignmentService.saveAssignment(createRequest, userDetails));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testEditAssignment_WhenSameAssetAndValidInput_ThenReturnAssignmentAndMessageSuccess() {
        when(repository.existsById(anyInt())).thenReturn(true);
        when(repository.findByIdAndStatusEquals(anyInt(), eq(StatusConstant.WAITING_FOR_ACCEPTANCE))).thenReturn(Optional.of(updateAssignment));
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.of(asset));
        when(userRepository.findByUsernameAndStatus(editRequest.getUsername(), StatusConstant.ACTIVE)).thenReturn(Optional.of(assignee));
        when(repository.saveAndFlush(any(Assignment.class))).thenReturn(assignment);
        when(mapper.entityToDto(any(Assignment.class), any(UserDetailsDto.class))).thenReturn(mapToAssignmentResponse(updateAssignment, userDetails));

        ResponseDto<AssignmentResponse> response = assignmentService.editAssignment(1, editRequest, userDetails);

        assertEquals("Assignment update successfully.", response.getMessage());
        assertEquals("LA000001", response.getData().getAssetCode());
        assertEquals("Updated assignment", response.getData().getNote());
        assertEquals(StatusConstant.WAITING_FOR_ACCEPTANCE, response.getData().getStatus());

        verify(assetRepository, times(1)).saveAndFlush(any(Asset.class));
        verify(repository, times(1)).saveAndFlush(any(Assignment.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testEditAssignment_WhenNewAssetAndValidInput_ThenReturnAssignmentAndMessageSuccess() {
        Asset newAsset = new Asset();
        newAsset.setAssetCode("LA000002");
        newAsset.setStatus(StatusConstant.AVAILABLE);

        editRequest = EditAssignmentRequest.builder()
                .assetCode(newAsset.getAssetCode())
                .username(assignee.getUsername())
                .note("Updated assignment")
                .build();

        when(repository.existsById(anyInt())).thenReturn(true);
        when(repository.findByIdAndStatusEquals(anyInt(), eq(StatusConstant.WAITING_FOR_ACCEPTANCE))).thenReturn(Optional.of(updateAssignment));
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.of(newAsset));
        when(userRepository.findByUsernameAndStatus(editRequest.getUsername(), StatusConstant.ACTIVE)).thenReturn(Optional.of(assignee));
        when(repository.saveAndFlush(any(Assignment.class))).thenReturn(assignment);
        when(mapper.entityToDto(any(Assignment.class), any(UserDetailsDto.class))).thenReturn(mapToAssignmentResponse(updateAssignment, userDetails));

        ResponseDto<AssignmentResponse> response = assignmentService.editAssignment(1, editRequest, userDetails);

        assertEquals("Assignment update successfully.", response.getMessage());
        assertEquals("LA000001", response.getData().getAssetCode());
        assertEquals("Updated assignment", response.getData().getNote());
        assertEquals(StatusConstant.WAITING_FOR_ACCEPTANCE, response.getData().getStatus());

        verify(assetRepository, times(1)).saveAndFlush(any(Asset.class));
        verify(repository, times(1)).saveAndFlush(any(Assignment.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testEditAssignment_WhenAssetNotFound_ThenThrowResourceNotFoundException() {
        when(repository.existsById(anyInt())).thenReturn(true);
        when(repository.findByIdAndStatusEquals(anyInt(), eq(StatusConstant.WAITING_FOR_ACCEPTANCE))).thenReturn(Optional.of(new Assignment()));
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> assignmentService.editAssignment(1, editRequest, userDetails));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testEditAssignment_WhenAssignmentNotFound_ThenThrowResourceNotFoundException() {
        when(repository.existsById(anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> assignmentService.editAssignment(1, editRequest, userDetails));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testEditAssignment_WhenAssetAlreadyAssigned_ThenThrowResourceAlreadyExistException() {
        Asset assignedAsset = Asset.builder()
                .assetCode("LA000003")
                .status(StatusConstant.ASSIGNED)
                .build();

        editRequest = EditAssignmentRequest.builder()
                .assetCode(assignedAsset.getAssetCode())
                .username("user")
                .note("Updated assignment")
                .build();

        when(repository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findByUsernameAndStatus(editRequest.getUsername(), StatusConstant.ACTIVE)).thenReturn(Optional.of(assignee));
        when(repository.findByIdAndStatusEquals(anyInt(), eq(StatusConstant.WAITING_FOR_ACCEPTANCE))).thenReturn(Optional.of(assignment));
        when(assetRepository.findAssetByAssetCode(anyString())).thenReturn(Optional.of(assignedAsset));
        when(assetRepository.existsByAssetCodeAndStatus(assignedAsset.getAssetCode(), StatusConstant.ASSIGNED)).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class, () -> assignmentService.editAssignment(1, editRequest, userDetails));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testDeleteAssignment_WhenNotFound_ThenThrowResourceNotFoundException() {
        when(repository.existsById(anyInt())).thenReturn(Boolean.FALSE);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> assignmentService.deleteAssignment(1));
        assertEquals("Assignment does not exist", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testDeleteAssignment_WhenStatusIsNOTWaitingForAcceptanceOrDeclined_ThenThrowBadRequestException() {
        Assignment notDeleteableAssignment = Assignment.builder()
                .id(1)
                .asset(asset)
                .assignee(assignee)
                .assignedDate(LocalDate.now())
                .note("Test assignment")
                .status(StatusConstant.ACTIVE)
                .build();

        when(repository.existsById(anyInt())).thenReturn(Boolean.TRUE);
        when(repository.getReferenceById(anyInt())).thenReturn(notDeleteableAssignment);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> assignmentService.deleteAssignment(1));
        assertEquals("Cannot delete assignment", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testDeleteAssignment_WhenStatusIsWaitingForAcceptanceOrDeclined_ThenSucces() {
        Assignment deletedAssignment = Assignment.builder()
                .id(1)
                .asset(asset)
                .assignee(assignee)
                .assignedDate(LocalDate.now())
                .note("Test assignment")
                .status(StatusConstant.INACTIVE)
                .build();
        when(repository.existsById(anyInt())).thenReturn(Boolean.TRUE);
        when(repository.getReferenceById(anyInt())).thenReturn(assignment);
        when(repository.save(any(Assignment.class))).thenReturn(deletedAssignment);

        ResponseDto response = assignmentService.deleteAssignment(1);
        Assertions.assertNull(response.getData());
        Assertions.assertNotNull(response.getMessage());
        assertEquals(response.getMessage(), "Delete assignment successfully");
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testResponseAssignment_WhenAccepted_ThenReturnAssignmentAndMessageSuccess() {
        Assignment acceptedAssignment = Assignment.builder()
                .id(1)
                .asset(asset)
                .assignee(assignee)
                .assignedDate(LocalDate.now())
                .note("Test assignment")
                .status(StatusConstant.ACCEPTED)
                .build();
        when(repository.findById(anyInt())).thenReturn(Optional.of(assignment));
        when(repository.saveAndFlush(any(Assignment.class))).thenReturn(assignment);
        when(mapper.entityToDto(any(Assignment.class), any(UserDetailsDto.class))).thenReturn(mapToAssignmentResponse(acceptedAssignment, userDetails));

        ResponseDto<AssignmentResponse> response = assignmentService.responseAssignment(1, StatusConstant.ACCEPTED, userDetails);

        assertEquals("Assignment accepted successfully.", response.getMessage());
        assertEquals(StatusConstant.ACCEPTED, response.getData().getStatus());

        verify(repository, times(1)).saveAndFlush(any(Assignment.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testResponseAssignment_WhenDeclined_ThenReturnAssignmentAndMessageSuccess() {
        Assignment declinedAssignment = Assignment.builder()
                .id(1)
                .asset(asset)
                .assignee(assignee)
                .assignedDate(LocalDate.now())
                .note("Test assignment")
                .status(StatusConstant.DECLINED)
                .build();
        when(repository.findById(anyInt())).thenReturn(Optional.of(assignment));
        when(repository.saveAndFlush(any(Assignment.class))).thenReturn(assignment);
        when(assetRepository.saveAndFlush(any(Asset.class))).thenReturn(asset);
        when(mapper.entityToDto(any(Assignment.class), any(UserDetailsDto.class))).thenReturn(mapToAssignmentResponse(declinedAssignment, userDetails));

        ResponseDto<AssignmentResponse> response = assignmentService.responseAssignment(1, StatusConstant.DECLINED, userDetails);

        assertEquals("Assignment declined successfully.", response.getMessage());
        assertEquals(StatusConstant.DECLINED, response.getData().getStatus());
        assertEquals(StatusConstant.AVAILABLE, assignment.getAsset().getStatus());

        verify(repository, times(1)).saveAndFlush(any(Assignment.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testResponseAssignment_WhenWaitingForAcceptance_ThenReturnAssignmentAndMessageSuccess() {
        Assignment waitingAssignment = Assignment.builder()
                .id(1)
                .asset(asset)
                .assignee(assignee)
                .assignedDate(LocalDate.now())
                .note("Test assignment")
                .status(StatusConstant.WAITING_FOR_ACCEPTANCE)
                .build();
        when(repository.findById(anyInt())).thenReturn(Optional.of(assignment));
        when(repository.saveAndFlush(any(Assignment.class))).thenReturn(assignment);
        when(mapper.entityToDto(any(Assignment.class), any(UserDetailsDto.class))).thenReturn(mapToAssignmentResponse(waitingAssignment, userDetails));

        ResponseDto<AssignmentResponse> response = assignmentService.responseAssignment(1, StatusConstant.WAITING_FOR_ACCEPTANCE, userDetails);

        assertEquals("Assignment is waiting for returning.", response.getMessage());
        assertEquals(StatusConstant.WAITING_FOR_ACCEPTANCE, response.getData().getStatus());

        verify(repository, times(1)).saveAndFlush(any(Assignment.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testResponseAssignment_WhenWaitingForReturning_ThenReturnAssignmentAndMessageSuccess() {
        Assignment returningAssignment = Assignment.builder()
                .id(1)
                .asset(asset)
                .assignee(assignee)
                .assignedDate(LocalDate.now())
                .note("Test assignment")
                .status(StatusConstant.WAITING_FOR_RETURNING)
                .build();
        when(repository.findById(anyInt())).thenReturn(Optional.of(assignment));
        when(repository.saveAndFlush(any(Assignment.class))).thenReturn(assignment);
        when(mapper.entityToDto(any(Assignment.class), any(UserDetailsDto.class))).thenReturn(mapToAssignmentResponse(returningAssignment, userDetails));

        assignment.setStatus(StatusConstant.WAITING_FOR_RETURNING); // Ensure the assignment status is set correctly before verification

        ResponseDto<AssignmentResponse> response = assignmentService.responseAssignment(1, StatusConstant.WAITING_FOR_RETURNING, userDetails);

        assertEquals("Assignment is waiting for returning.", response.getMessage());
        assertEquals(StatusConstant.WAITING_FOR_RETURNING, response.getData().getStatus());

        verify(repository, times(1)).saveAndFlush(any(Assignment.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testResponseAssignment_WhenAssignmentNotFound_ThenThrowResourceNotFoundException() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> assignmentService.responseAssignment(1, StatusConstant.ACCEPTED, userDetails));
    }

    private AssignmentDetailResponse mapToAssignmentDetail(Assignment assignment, UserDetailsDto assignedBy) {
        return AssignmentDetailResponse.builder()
                .id(assignment.getId())
                .assetCode(assignment.getAsset().getAssetCode())
                .assetName(assignment.getAsset().getName())
                .category(assignment.getAsset().getCategory().getName())
                .specification(assignment.getAsset().getSpecification())
                .assignedTo(assignment.getAssignee().getUsername())
                .assignedBy(assignedBy.getUsername())
                .assignedDate(assignment.getAssignedDate())
                .status(assignment.getStatus())
                .note(assignment.getNote())
                .build();
    }

    private AssignmentResponse mapToAssignmentResponse(Assignment assignment, UserDetailsDto assignedBy) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .assetCode(assignment.getAsset().getAssetCode())
                .assetName(assignment.getAsset().getName())
                .assignTo(assignment.getAssignee().getUsername())
                .assignBy(assignedBy.getUsername())
                .assignedDate(assignment.getAssignedDate())
                .status(assignment.getStatus())
                .note(assignment.getNote())
                .build();
    }
}
