package com.nashtech.rookies.assetmanagement.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.AssignmentGetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.CreateAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.EditAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentDetailResponse;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.service.AssignmentService;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;

@SpringBootTest
@AutoConfigureMockMvc
class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssignmentService assignmentService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private SecurityContext securityContext;

    @Autowired
    private ObjectMapper objectMapper;
    private UserDetailsDto userDetailsDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        userDetailsDto = UserDetailsDto.builder()
                .roleName(RoleConstant.ADMIN)
                .location(LocationConstant.HCM)
                .build();
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testGetOwnAssignmentDetails_WhenSuccess_ThenReturnListAssignment() throws Exception {
        List<AssignmentDetailResponse> assignmentList = Collections.singletonList(
                new AssignmentDetailResponse(1, "assetCode", "assetName", "category", "specification", "assignedTo", "assignedBy", LocalDate.now(), null, "note")
        );
        Sort sort = Sort.by(Sort.Direction.ASC, "asset.assetCode");
        Pageable pageable = PageRequest.of(0, 20, sort);
        Page<AssignmentDetailResponse> assignmentPage = new PageImpl<>(assignmentList, pageable, 20);
        PageableDto<List<AssignmentDetailResponse>> page = PageableDto.<List<AssignmentDetailResponse>>builder()
                .content(assignmentPage.getContent())
                .currentPage(assignmentPage.getNumber())
                .totalPage(assignmentPage.getTotalPages())
                .totalElements(assignmentPage.getTotalElements())
                .build();
        ResponseDto<PageableDto<List<AssignmentDetailResponse>>> responseDto = ResponseDto.<PageableDto<List<AssignmentDetailResponse>>>builder()
                .data(page)
                .message("Successfully retrieved your own assignment details.")
                .build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(assignmentService.getOwnAssignmentDetails(any(UserDetailsDto.class), any(Pageable.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/assignments/own")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Successfully retrieved your own assignment details.")));

        // Verify service method invocation
        verify(assignmentService, times(1))
                .getOwnAssignmentDetails(any(UserDetailsDto.class), any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testGetAssignmentDetails_WhenSuccess_ThenReturnListAssignment() throws Exception {
        AssignmentGetRequest request = new AssignmentGetRequest("assetName", Collections.singletonList("status"), LocalDate.now());
        Sort sort = Sort.by(Sort.Direction.ASC, "asset.assetCode");
        Pageable pageable = PageRequest.of(0, 20, sort);

        List<AssignmentDetailResponse> assignmentList = Collections.singletonList(
                new AssignmentDetailResponse(1, "assetCode", "assetName", "category", "specification", "assignedTo", "assignedBy", LocalDate.now(), null, "note")
        );
        Page<AssignmentDetailResponse> assignmentPage = new PageImpl<>(assignmentList, pageable, 20);
        PageableDto<List<AssignmentDetailResponse>> page = PageableDto.<List<AssignmentDetailResponse>>builder()
                .content(assignmentPage.getContent())
                .currentPage(assignmentPage.getNumber())
                .totalPage(assignmentPage.getTotalPages())
                .totalElements(assignmentPage.getTotalElements())
                .build();
        ResponseDto<PageableDto<List<AssignmentDetailResponse>>> responseDto = ResponseDto.<PageableDto<List<AssignmentDetailResponse>>>builder()
                .data(page)
                .message("Successfully retrieved assignment details.")
                .build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(assignmentService.getAssignmentDetails(any(AssignmentGetRequest.class), any(Pageable.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Successfully retrieved assignment details.")));

        // Verify service method invocation
        verify(assignmentService, times(1))
                .getAssignmentDetails(any(AssignmentGetRequest.class), any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testCreateAssignment_WhenInputValid_ThenReturnAssignmentAndMessageSuccess() throws Exception {
        CreateAssignmentRequest request = CreateAssignmentRequest.builder()
                .staffCode("SD0001")
                .assetCode("EL000001")
                .assignedDate(LocalDate.now())
                .note("Test note")
                .build();

        AssignmentResponse assignmentResponse = AssignmentResponse.builder()
                .id(1)
                .assetCode("EL000001")
                .assetName("Asset 1")
                .assignBy(userDetailsDto.getUsername())
                .assignTo("SD0001")
                .assignedDate(LocalDate.now())
                .note("Test note")
                .status(StatusConstant.WAITING_FOR_ACCEPTANCE)
                .build();
        ResponseDto<AssignmentResponse> responseDto = new ResponseDto<>(assignmentResponse, "Assignment created successfully");

        when(assignmentService.saveAssignment(any(CreateAssignmentRequest.class), any(UserDetailsDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testEditAssignment_WhenInputValid_ThenReturnAssignmentAndMessageSuccess() throws Exception {
        EditAssignmentRequest request = EditAssignmentRequest.builder()
                .staffCode("SD0001")
                .assetCode("EL000001")
                .note("Updated note")
                .build();

        AssignmentResponse assignmentResponse = AssignmentResponse.builder()
                .id(1)
                .assetCode("EL000001")
                .assetName("Asset 1")
                .assignBy(userDetailsDto.getUsername())
                .assignTo("SD0001")
                .assignedDate(LocalDate.now())
                .note("Updated note")
                .status(StatusConstant.WAITING_FOR_ACCEPTANCE)
                .build();
        ResponseDto<AssignmentResponse> responseDto = new ResponseDto<>(assignmentResponse, "Assignment updated successfully");

        when(assignmentService.editAssignment(eq(1), any(EditAssignmentRequest.class), any(UserDetailsDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/assignments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testResponseAssignment_WhenAccepted_ThenReturnMessageSuccess() throws Exception {

        AssignmentResponse assignmentResponse = AssignmentResponse.builder()
                .id(1)
                .assetCode("EL000001")
                .assetName("Asset 1")
                .assignBy(userDetailsDto.getUsername())
                .assignTo("SD0001")
                .assignedDate(LocalDate.now())
                .note("Updated note")
                .status(StatusConstant.ACCEPTED)
                .build();
        ResponseDto<AssignmentResponse> responseDto = ResponseDto.<AssignmentResponse>builder()
                .data(assignmentResponse)
                .message("Assignment response successfully.")
                .build();

        when(assignmentService.responseAssignment(eq(1), eq(StatusConstant.ACCEPTED), any(UserDetailsDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/assignments/response/1")
                        .param("status", StatusConstant.ACCEPTED.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Assignment response successfully.")));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    void testResponseAssignment_WhenDeclined_ThenReturnMessageSuccess() throws Exception {

        AssignmentResponse assignmentResponse = AssignmentResponse.builder()
                .id(1)
                .assetCode("EL000001")
                .assetName("Asset 1")
                .assignBy(userDetailsDto.getUsername())
                .assignTo("SD0001")
                .assignedDate(LocalDate.now())
                .note("Updated note")
                .status(StatusConstant.DECLINED)
                .build();
        ResponseDto<AssignmentResponse> responseDto = ResponseDto.<AssignmentResponse>builder()
                .data(assignmentResponse)
                .message("Assignment response successfully.")
                .build();

        when(assignmentService.responseAssignment(eq(1), eq(StatusConstant.DECLINED), any(UserDetailsDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/assignments/response/1")
                        .param("status", StatusConstant.DECLINED.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Assignment response successfully.")));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testDeleteAssignment_WhenValidInput_ThenSuccess() throws Exception {
        Integer id = 1;
        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .message("Delete assignment successfully")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(assignmentService.deleteAssignment(anyInt())).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/assignments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Delete assignment successfully")));
        // Verify service method invocation
        verify(assignmentService, times(1))
                .deleteAssignment(anyInt());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testDeleteAssignment_WhenNotFound_ThenThrowResourceNotFoundException() throws Exception {
        Integer id = 1;
        ResourceNotFoundException rnfe = new ResourceNotFoundException("Assignment does not exist");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(assignmentService.deleteAssignment(anyInt())).thenThrow(rnfe);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/assignments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Assignment does not exist")));
        // Verify service method invocation
        verify(assignmentService, times(1))
                .deleteAssignment(anyInt());
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testDeleteAssignment_WhenStatusIsNOTWaitingForAcceptaceOrDeclined_ThenThrowBadRequestException() throws Exception {
        Integer id = 1;
        BadRequestException bre = new BadRequestException("Cannot delete assignment");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user(userDetailsDto));
        when(assignmentService.deleteAssignment(anyInt())).thenThrow(bre);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/assignments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetailsDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Cannot delete assignment")));
        // Verify service method invocation
        verify(assignmentService, times(1))
                .deleteAssignment(anyInt());
    }
}
