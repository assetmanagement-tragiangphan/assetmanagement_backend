package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.request.UpdateUserRequest;
import com.nashtech.rookies.assetmanagement.entity.Role;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.exception.InvalidDateException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.UserMapper;
import com.nashtech.rookies.assetmanagement.repository.RoleRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.util.GenderConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private UserServiceImpl userService;

    @BeforeEach
    public void reset_mock() throws Exception {
        userService = new UserServiceImpl(userRepository, userMapper, roleRepository);
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
}
