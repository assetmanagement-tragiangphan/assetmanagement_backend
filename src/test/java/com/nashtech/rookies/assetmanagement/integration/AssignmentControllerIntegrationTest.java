package com.nashtech.rookies.assetmanagement.integration;

import java.time.LocalDate;
import java.util.List;

import com.nashtech.rookies.assetmanagement.dto.request.AuthenticationRequest;
import com.nashtech.rookies.assetmanagement.dto.response.LoginResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.AssignmentGetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.CreateAssignmentRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.EditAssignmentRequest;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AssignmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationService authenticationService;
    private AssignmentGetRequest requestGet;
    private CreateAssignmentRequest createRequest;
    private EditAssignmentRequest editRequest;
    private Cookie cookie;

    @BeforeEach
    void setUp() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("nhatt")
                .password("Nhat@123")
                .build();
        ResponseDto<LoginResponse> token = authenticationService.authenticate(request, "");
        String jwtToken = token.getData().getToken();

        cookie = new Cookie("asset-management-server", jwtToken);

        requestGet = AssignmentGetRequest.builder()
                .search("")
                .status(List.of("ASSIGNED", "WAITING_FOR_ACCEPTANCE"))
                .assignedDate(LocalDate.parse("2023-01-20"))
                .build();

        createRequest = CreateAssignmentRequest.builder()
                .staffCode("SD0001")
                .assetCode("PC000009")
                .assignedDate(LocalDate.now())
                .note("abc")
                .build();

        editRequest = EditAssignmentRequest.builder()
                .username("nhatt")
                .assetCode("PC000010")
                .note("abcxyz")
                .build();
    }

//    @Test
//    void testGetOwnAssignmentDetails_WhenSuccess_ThenReturnListAssignment() throws Exception {
//
////        mockMvc.perform(get("/api/v1/assignments/own?page=0&size=20&sort=id,asc")
//        mockMvc.perform(get("/api/v1/assignments/own")
//                        .param("page", "0")
//                        .param("size", "20")
//                        .param("sort", "id,asc")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .cookie(cookie))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message", Matchers.is("Successfully retrieved your own assignment details.")));
//    }
//
//    @Test
//    void testGetAssignmentDetails_WhenSuccess_ThenReturnListAssignment() throws Exception {
//
////        mockMvc.perform(get("/api/v1/assignments?search=PC000004&status=ASSIGNED,WAITING_FOR_ACCEPTANCE&assignedDate=2024-06-27&page=0&size=20&sort=id,asc")
//        mockMvc.perform(get("/api/v1/assignments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("search", requestGet.getSearch())
//                        .param("status", String.join(",", requestGet.getStatus()))
//                        .param("assignedDate", requestGet.getAssignedDate().toString())
//                        .param("page", "0")
//                        .param("size", "20")
//                        .param("sort", "id,asc")
//                        .cookie(cookie))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message", Matchers.is("Successfully retrieved assignment details.")));
//    }

    @Test
    void testCreateAssignment_WhenInputValid_ThenReturnAssignmentAndMessageSuccess() throws Exception {

        mockMvc.perform(post("/api/v1/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void testEditAssignment_WhenInputValid_ThenReturnAssignmentAndMessageSuccess() throws Exception {

        mockMvc.perform(patch("/api/v1/assignments/14")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .content(objectMapper.writeValueAsString(editRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
