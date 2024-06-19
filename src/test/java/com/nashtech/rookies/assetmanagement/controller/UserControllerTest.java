package com.nashtech.rookies.assetmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.UpdateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.request.User.UserGetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.UserMapper;
import com.nashtech.rookies.assetmanagement.service.UserService;
import com.nashtech.rookies.assetmanagement.util.GenderConstant;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

//@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    List<UserDto> userDtoList;
    UserDetailsDto userDetailsDto;

    private User getUser() {
        User user = new User();
        user.setId(1);
        user.setGender(GenderConstant.MALE);
        user.setFirstName("test");
        return user;
    }

    @BeforeEach
    void setup(){
        userDtoList = List.of(
                UserDto.builder().id(1).firstName("test").build(),
                UserDto.builder().id(2).firstName("test2").build());
        
        userDetailsDto = UserDetailsDto.builder()
                .id(1)
                .roleName(RoleConstant.ADMIN)
                .username("admin")
                .location(LocationConstant.HCM)
                .build();
    }

    @Test
    public void testGetAll_whenRequestUserRoleAdmin_thenReturnUserList() throws Exception{
        PageableDto<List<UserDto>> usersPageDto = PageableDto.<List<UserDto>>builder()
                .content(userDtoList)
                .currentPage(0)
                .totalPage(1)
                .totalElements(Long.valueOf(userDtoList.size()))
                .build();

        when(userService.getAll(any(UserGetRequest.class), any(Pageable.class), any(UserDetailsDto.class))).thenReturn(
                ResponseDto.<PageableDto<List<UserDto>>>builder()
                .data(usersPageDto)
                .message("Get all users successfully.")
                .build());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("search", "a");
        params.add("page", "0");
        params.add("size", "20");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON)
                .params(params)
                .with(user(userDetailsDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", Matchers.hasSize(userDtoList.size())))
                .andExpect(jsonPath("$.data.currentPage", Matchers.is(0)))
                .andExpect(jsonPath("$.data.totalPage", Matchers.is(1)))
                .andExpect(jsonPath("$.data.totalElements", Matchers.is(userDtoList.size())));
    }

    @Test
    public void testGetUserById_WhenUserIdIsValid_thenReturnUser() throws Exception {
        Integer id = 1;
        var sampleResponse = ResponseDto.<UserDto>builder()
                .data(userMapper.entityToDto(getUser()))
                .message("Get user by id successfully")
                .build();

        when(userService.getUserById(id)).thenReturn(sampleResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", Matchers.is(1)));
    }

    @Test
    public void testGetUserById_WhenUserIdNotExist_thenThrowResourceNotFoundException() throws Exception {
        Integer id = 1;

        when(userService.getUserById(id)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.is("User not found")));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testUpdateUser_WhenUserIdIsValid_thenReturnUser() throws Exception {
        var sampleResponse = ResponseDto.<UserDto>builder()
                .data(userMapper.entityToDto(getUser()))
                .message("Update user successfully")
                .build();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .type(1)
                .joinedDate(LocalDate.parse("2024-06-12"))
                .dateOfBirth(LocalDate.parse("2002-05-19"))
                .gender(String.valueOf(GenderConstant.MALE))
                .build();

        doReturn(sampleResponse).when(userService).updateUser(any(UpdateUserRequest.class),anyInt());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
        assertNotNull(result.getResponse().getContentAsString());
        assertEquals(result.getResponse().getContentAsString(),objectMapper.writeValueAsString(sampleResponse));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testUpdateUser_whenUserIdInvalid_thenThrow() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .type(1)
                .joinedDate(LocalDate.parse("2024-06-12"))
                .dateOfBirth(LocalDate.parse("2002-05-19"))
                .gender(String.valueOf(GenderConstant.MALE))
                .build();
        // Ensure that the mock is set up to throw an exception
        given(userService.updateUser(any(UpdateUserRequest.class),anyInt())).willThrow(new ResourceNotFoundException("User not found"));

        // Perform the GET request and verify the result
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testDisableUser_whenUserIdIsValid_thenReturnResponseDtoSucess() throws Exception {
        var sampleResponse = ResponseDto.<Void>builder()
                .message("Disable user successfully")
                .build();

        when(userService.disableUser(anyInt())).thenReturn(sampleResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users/1/disable")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Matchers.is("Disable user successfully")));
    }

    @Test
    @WithMockUser(username = "test", roles = "ADMIN")
    public void testDisableUser_whenUserIdInvalid_thenReturnStatusNotFound() throws Exception {
        when(userService.disableUser(anyInt())).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users/1/disable")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.is("User not found")));
    }
}
