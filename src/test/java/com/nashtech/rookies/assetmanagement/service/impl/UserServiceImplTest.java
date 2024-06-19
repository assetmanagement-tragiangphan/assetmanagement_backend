package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.ChangePasswordRequest;
import com.nashtech.rookies.assetmanagement.dto.request.CreateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.request.UpdateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.request.User.UserGetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Role;
import com.nashtech.rookies.assetmanagement.entity.Token;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.exception.InvalidDateException;
import com.nashtech.rookies.assetmanagement.exception.InvalidUserCredentialException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.UserMapper;
import com.nashtech.rookies.assetmanagement.repository.RoleRepository;
import com.nashtech.rookies.assetmanagement.repository.TokenRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.util.GenderConstant;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.Id;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TokenRepository tokenRepository;

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private UserServiceImpl userService;
    @Mock
    private PasswordEncoder passwordEncoder;

    List<User> users;
    UserGetRequest userGetRequest;
    UserDetailsDto userDetailsDto;
    ChangePasswordRequest changePasswordRequest;

    @BeforeEach
    public void reset_mock() throws Exception {
        userService = new UserServiceImpl(userRepository, userMapper, roleRepository, passwordEncoder, tokenRepository);

        Token token = Token.builder().id(1).token("token1").build();

        User user1 = User.builder().id(1).staffCode("staffCode1").firstName("firstName1").lastName("lastName1")
                .username("username1").password("password1").tokens(List.of(token)).status(StatusConstant.ACTIVE).build();
        User user2 = User.builder().id(2).staffCode("staffCode2").firstName("firstName2").lastName("lastName2")
                .username("username2").password("password2").build();
        User user3 = User.builder().id(3).staffCode("staffCode3").firstName("firstName3").lastName("lastName3")
                .username("username3").password("password3").build();

        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        userGetRequest = new UserGetRequest();

        userDetailsDto = UserDetailsDto.builder().id(1).username("username1").location(LocationConstant.HCM).build();
    }

    public Role getRole(int id, RoleConstant name) {
        Role role = new Role();
        role.setName(name);
        role.setId(id);
        return role;
    }
    public User getUser(int id, int roleId, RoleConstant roleName, GenderConstant gender, String joinedDate, String dateOfBirth) {
        User user = new User();
        user.setId(id);
        user.setGender(GenderConstant.MALE);
        Role role = new Role();
        role.setId(roleId);
        role.setName(roleName);
        user.setRole(role);
        user.setGender(gender);
        user.setJoinedDate(LocalDate.parse(joinedDate));
        user.setDateOfBirth(LocalDate.parse(dateOfBirth));
        return user;
    }

	@Test
    void testGetUserEntityById_whenExistUser_thenReturnUser() {
        // Arrange
        Integer id = 1;
        when(userRepository.findByIdAndStatus(any(Integer.class), any(StatusConstant.class))).thenReturn(Optional.of(users.get(0)));
        // Act
        User result = userService.getUserEntityById(id);

        // Assert
        assertEquals(users.get(0).getId(), result.getId());
        assertEquals(users.get(0).getStaffCode(), result.getStaffCode());
        assertEquals(users.get(0).getFirstName(), result.getFirstName());
        assertEquals(users.get(0).getLastName(), result.getLastName());

        verify(userRepository, times(1)).findByIdAndStatus(any(Integer.class), any(StatusConstant.class));
    }

    @Test
    void testGetUserEntityById_whenNotExistUser_thenThrowNotFoundException() {
        // Arrange
        Integer id = 1;
        when(userRepository.findByIdAndStatus(any(Integer.class), any(StatusConstant.class))).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserEntityById(id));
        verify(userRepository, times(1)).findByIdAndStatus(any(Integer.class), any(StatusConstant.class));
    }

    @Test
    void testGetUserById_whenExistUser_thenReturnResponseDtoUserDto() {
        // Arrange
        Integer id = 1;
        when(userRepository.findByIdAndStatus(any(Integer.class), any(StatusConstant.class))).thenReturn(Optional.of(users.get(0)));

        // Act
        ResponseDto<UserDto> result = userService.getUserById(id);

        // Assert
        assertEquals(result.getData().getId(), users.get(0).getId());
        assertEquals(result.getData().getStaffCode(), users.get(0).getStaffCode());
        assertEquals(result.getData().getFirstName(), users.get(0).getFirstName());
        assertEquals(result.getData().getLastName(), users.get(0).getLastName());

        verify(userRepository, times(1)).findByIdAndStatus(any(Integer.class), any(StatusConstant.class));
    }

    @Test
    void testGetAll_whenInputPageable_thenReturnResponseDtoPageUserDto() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        Page<User> page = new PageImpl<>(users, pageable, users.size());
        
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Act
        ResponseDto<PageableDto<List<UserDto>>> result = userService.getAll(userGetRequest, pageable, userDetailsDto);

        // Assert
        assertEquals(result.getData().getTotalElements(), 3);
        assertEquals(result.getData().getContent().size(), 3);
        assertEquals(result.getData().getContent().get(0).getId(), users.get(0).getId());
        assertEquals(result.getData().getContent().get(0).getStaffCode(), users.get(0).getStaffCode());
        assertEquals(result.getData().getContent().get(0).getFirstName(), users.get(0).getFirstName());
        assertEquals(result.getData().getContent().get(0).getLastName(), users.get(0).getLastName());

        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testUpdateUser_whenUserIdIsValid_thenReturnUser() {
        Role sampleRole = getRole(1,RoleConstant.ADMIN);
        User sampleUser = getUser(1,2, RoleConstant.STAFF, GenderConstant.MALE, "2024-01-01", "2001-01-01");
        when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(sampleRole));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UpdateUserRequest dto = UpdateUserRequest.builder()
                .joinedDate(LocalDate.parse("2024-12-12"))
                .dateOfBirth(LocalDate.parse("2002-01-01"))
                .gender(String.valueOf(GenderConstant.FEMALE))
                .type(1)
                .build();

        var actual = userService.updateUser(dto, 1);

        Assert.notNull(actual.getData());
        verify(roleRepository).findById(1);
        verify(userRepository).findById(1);
        assertEquals(actual.getData().getJoinedDate().toString(),"2024-12-12");
        assertEquals(actual.getData().getDateOfBirth().toString(),"2002-01-01");
        assertEquals(actual.getData().getGender(), "FEMALE");
        assertEquals(actual.getData().getRoleId(),1);
    }

    @Test()
    public void testUpdateUser_whenUserIdIsInvalid_thenThrow() {
        UpdateUserRequest dto = UpdateUserRequest.builder()
                .joinedDate(LocalDate.parse("2024-12-12"))
                .dateOfBirth(LocalDate.parse("2002-01-01"))
                .gender(String.valueOf(GenderConstant.FEMALE))
                .type(1)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(dto, 1));
    }

    @Test
    public void testUpdateUser_whenRoleIdIsInvalid_thenThrow() {
        User sampleUser = getUser(1,2, RoleConstant.STAFF, GenderConstant.MALE, "2024-01-01", "2001-01-01");
        UpdateUserRequest dto = UpdateUserRequest.builder()
                .joinedDate(LocalDate.parse("2024-12-12"))
                .dateOfBirth(LocalDate.parse("2002-01-01"))
                .gender(String.valueOf(GenderConstant.FEMALE))
                .type(1)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(sampleUser));
        when(roleRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(dto, 1));
    }

    @Test
    public void testUpdateUser_whenJoinedDateIsWeekend_thenThrows() {
        Role sampleRole = getRole(1,RoleConstant.ADMIN);
        User sampleUser = getUser(1,2, RoleConstant.STAFF, GenderConstant.MALE, "2024-01-01", "2001-01-01");
        UpdateUserRequest dto = UpdateUserRequest.builder()
                .joinedDate(LocalDate.parse("2024-06-16"))
                .dateOfBirth(LocalDate.parse("2002-01-01"))
                .gender(String.valueOf(GenderConstant.FEMALE))
                .type(1)
                .build();
        when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(sampleRole));

        assertThrows(InvalidDateException.class, () -> userService.updateUser(dto,1));
    }

    @Test
    public void testUpdateUser_whenJoinedDateIsBeforeBirthDay_thenThrows() {
        Role sampleRole = getRole(1,RoleConstant.ADMIN);
        User sampleUser = getUser(1,2, RoleConstant.STAFF, GenderConstant.MALE, "2024-01-01", "2001-01-01");
        UpdateUserRequest dto = UpdateUserRequest.builder()
                .joinedDate(LocalDate.parse("2001-06-16"))
                .dateOfBirth(LocalDate.parse("2002-01-01"))
                .gender(String.valueOf(GenderConstant.FEMALE))
                .type(1)
                .build();
        when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(sampleRole));

        assertThrows(InvalidDateException.class, () -> userService.updateUser(dto,1));
    }

    @Test
    public void testUpdateUser_whenDobIsInvalid_thenThrows() {
        Role sampleRole = getRole(1,RoleConstant.ADMIN);
        User sampleUser = getUser(1,2, RoleConstant.STAFF, GenderConstant.MALE, "2024-01-01", "2001-01-01");
        UpdateUserRequest dto = UpdateUserRequest.builder()
                .joinedDate(LocalDate.parse("2024-12-12"))
                .dateOfBirth(LocalDate.parse("2022-01-01"))
                .gender(String.valueOf(GenderConstant.FEMALE))
                .type(1)
                .build();
        when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(sampleRole));

        assertThrows(InvalidDateException.class, () -> userService.updateUser(dto,1));
    }

    @Test
    public void testCreateUser_whenRequestIsValid_thenReturnCreatedUser() {
        UserDetailsDto admin = UserDetailsDto.builder()
                .roleName(RoleConstant.valueOf("ADMIN"))
                .location(LocationConstant.HCM)
                .build();

        CreateUserRequest sampleRequest = CreateUserRequest.builder()
                .roleId(1)
                .gender(GenderConstant.MALE)
                .joinedDate(LocalDate.parse("2024-04-22"))
                .dateOfBirth(LocalDate.parse("2002-05-19"))
                .firstName("Nguyen")
                .lastName("Pham Sy")
                .build();

        User sampleUser = userMapper.createUserRequestToEntity(sampleRequest);
        sampleUser.setId(1);

        Role sampleRole = new Role();
        sampleRole.setId(1);
        when(roleRepository.findById(anyInt())).thenReturn(Optional.of(sampleRole));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(userRepository.findByUsernameStartsWith(anyString())).thenReturn(new ArrayList<User>());
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        var actual = userService.saveUser(sampleRequest,admin);

        assertNotNull(actual.getData());
        assertEquals(actual.getData().getStaffCode(),"SD0001");
    }

    @Test
    public void testCreateUser_whenDobIsInvalid_thenThrows() {
        UserDetailsDto admin = UserDetailsDto.builder()
                .roleName(RoleConstant.valueOf("ADMIN"))
                .location(LocationConstant.HCM)
                .build();
        CreateUserRequest sampleRequest = CreateUserRequest.builder()
                .roleId(1)
                .gender(GenderConstant.MALE)
                .joinedDate(LocalDate.parse("2024-04-22"))
                .dateOfBirth(LocalDate.parse("2022-05-19")) //under 18
                .firstName("Nguyen")
                .lastName("Pham Sy")
                .build();
        User sampleUser = userMapper.createUserRequestToEntity(sampleRequest);
        sampleUser.setId(1);

        Role sampleRole = new Role();
        sampleRole.setId(1);
        when(roleRepository.findById(1)).thenReturn(Optional.of(sampleRole));

        assertThrows(InvalidDateException.class, () -> userService.saveUser(sampleRequest,admin));
    }

    @Test
    public void testCreateUser_whenJoinedDateBeforeDob_thenThrows() {
        UserDetailsDto admin = UserDetailsDto.builder()
                .roleName(RoleConstant.valueOf("ADMIN"))
                .location(LocationConstant.HCM)
                .build();
        CreateUserRequest sampleRequest = CreateUserRequest.builder()
                .roleId(1)
                .gender(GenderConstant.MALE)
                .joinedDate(LocalDate.parse("2001-06-16"))
                .dateOfBirth(LocalDate.parse("2002-01-01"))
                .firstName("Nguyen")
                .lastName("Pham Sy")
                .build();
        User sampleUser = userMapper.createUserRequestToEntity(sampleRequest);
        sampleUser.setId(1);

        Role sampleRole = new Role();
        sampleRole.setId(1);
        when(roleRepository.findById(1)).thenReturn(Optional.of(sampleRole));
        assertThrows(InvalidDateException.class, () -> userService.saveUser(sampleRequest,admin));
    }

    @Test
    public void testCreateUser_whenDobIsWeekend_thenThrows() {
        UserDetailsDto admin = UserDetailsDto.builder()
                .roleName(RoleConstant.valueOf("ADMIN"))
                .location(LocationConstant.HCM)
                .build();
        CreateUserRequest sampleRequest = CreateUserRequest.builder()
                .roleId(1)
                .gender(GenderConstant.MALE)
                .joinedDate(LocalDate.parse("2024-06-16"))
                .dateOfBirth(LocalDate.parse("2002-05-19"))
                .firstName("Nguyen")
                .lastName("Pham Sy")
                .build();
        User sampleUser = userMapper.createUserRequestToEntity(sampleRequest);
        sampleUser.setId(1);

        Role sampleRole = new Role();
        sampleRole.setId(1);
        when(roleRepository.findById(1)).thenReturn(Optional.of(sampleRole));
        assertThrows(InvalidDateException.class, () -> userService.saveUser(sampleRequest, admin));
    }
    @Test
    public void testChangePassword_whenValidInput_thenSuccess() {
        changePasswordRequest = ChangePasswordRequest.builder()
                .newPassword("New Password")
                .oldPassword("Old Password")
                .build();
        int userId = 1;
        var user = User.builder()
                .id(1)
                .password("Encoded Password")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("New Password", "Encoded Password"))
                .thenReturn(false);
        when(passwordEncoder.matches("Old Password", "Encoded Password"))
                .thenReturn(true);
        when(passwordEncoder.encode("New Password"))
                .thenReturn("New Encoded Password");
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        var resData = userService.changePassword(userId, changePasswordRequest);

        assertThat(resData.getMessage()).isEqualTo("Change password successfully.");
        assertThat(resData.getData()).isNull();

        ArgumentCaptor<User> captorUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captorUser.capture());
        assertThat(captorUser.getValue().getPassword()).isEqualTo("New Encoded Password");
    }

    @Test
    public void testChangePassword_whenFirstTimeLogin_thenSuccess() {
        changePasswordRequest = ChangePasswordRequest.builder()
                .newPassword("New Password")
                .oldPassword("")
                .build();
        int userId = 1;
        var user = User.builder()
                .id(1)
                .password("Encoded Password")
                .isChangePassword(false)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("New Password", "Encoded Password"))
                .thenReturn(false);
        when(passwordEncoder.encode("New Password"))
                .thenReturn("New Encoded Password");
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        var resData = userService.changePassword(userId, changePasswordRequest);

        assertThat(resData.getMessage()).isEqualTo("Change password successfully.");
        assertThat(resData.getData()).isNull();

        ArgumentCaptor<User> captorUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captorUser.capture());
        assertThat(captorUser.getValue().getPassword()).isEqualTo("New Encoded Password");
        assertThat(captorUser.getValue().getIsChangePassword()).isTrue();
    }

    @Test
    public void testChangePassword_whenInvalidUserId_thenResourceNotFoundException() {
        changePasswordRequest = ChangePasswordRequest.builder()
                .newPassword("New Password")
                .oldPassword("Old Password")
                .build();
        int userId = 1;
        var user = User.builder()
                .id(1)
                .password("Encoded Password")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class, () ->
                userService.changePassword(userId, changePasswordRequest));

        assertThat(ex.getMessage()).isEqualTo("User not found.");
    }

    @Test
    public void testChangePassword_whenChangeSamePassword_thenBadRequestException() {
        changePasswordRequest = ChangePasswordRequest.builder()
                .newPassword("New Password")
                .oldPassword("Old Password")
                .build();
        int userId = 1;
        var user = User.builder()
                .id(1)
                .password("Encoded Password")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("New Password", "Encoded Password"))
                .thenReturn(true);

        var ex = assertThrows(BadRequestException.class, () ->
                userService.changePassword(userId, changePasswordRequest));

        assertThat(ex.getMessage()).isEqualTo("New password must be different from current password.");
    }

    @Test
    public void testChangePassword_whenProvideInvalidPassword_thenBadRequestException() {
        changePasswordRequest = ChangePasswordRequest.builder()
                .newPassword("New Password")
                .oldPassword("Old Password")
                .build();
        int userId = 1;
        var user = User.builder()
                .id(1)
                .password("Encoded Password")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("New Password", "Encoded Password"))
                .thenReturn(false);
        when(passwordEncoder.matches("Old Password", "Encoded Password"))
                .thenReturn(false);

        var ex = assertThrows(BadRequestException.class, () ->
                userService.changePassword(userId, changePasswordRequest));

        assertThat(ex.getMessage()).isEqualTo("Password is incorrect.");
    }

    @Test
    public void testChangePassword_whenEmptyOldPassword_thenBadRequestException() {
        changePasswordRequest = ChangePasswordRequest.builder()
                .newPassword("New Password")
                .oldPassword("")
                .build();
        int userId = 1;
        var user = User.builder()
                .id(1)
                .password("Encoded Password")
                .isChangePassword(true)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("New Password", "Encoded Password"))
                .thenReturn(false);

        var ex = assertThrows(BadRequestException.class, () ->
                userService.changePassword(userId, changePasswordRequest));

        assertThat(ex.getMessage()).isEqualTo("You must provide your current password.");
    }


    @Test 
    void testDisableUser_whenUserIdIsValid_thenReturnResponseDtoSucess() {
        var user = users.get(0);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findByIdAndStatus(1, StatusConstant.ACTIVE)).thenReturn(Optional.of(user));
        when(userRepository.save(userCaptor.capture())).thenReturn(user);
        
        var result = userService.disableUser(1);
        var updatedUser = userCaptor.getValue();

        assertEquals(updatedUser.getStatus(), StatusConstant.INACTIVE);
        assertEquals(result.getMessage(), "Disable user successfully.");
        verify(userRepository, times(1)).findByIdAndStatus(1, StatusConstant.ACTIVE);
        verify(tokenRepository, times(1)).deleteAll(any(List.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDisableUser_whenUserIdNotExist_thenThrowResourceNotFoundException() {
        when(userRepository.findByIdAndStatus(1, StatusConstant.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.disableUser(1));
        verify(userRepository, times(1)).findByIdAndStatus(1, StatusConstant.ACTIVE);
    }
    
}







