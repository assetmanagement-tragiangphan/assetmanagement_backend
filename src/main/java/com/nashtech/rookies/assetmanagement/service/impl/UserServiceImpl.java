package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.UserDto;
import com.nashtech.rookies.assetmanagement.dto.request.ChangePasswordRequest;
import com.nashtech.rookies.assetmanagement.dto.request.CreateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.request.UpdateUserRequest;
import com.nashtech.rookies.assetmanagement.dto.request.User.UserGetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.exception.InvalidDateException;
import com.nashtech.rookies.assetmanagement.dto.response.PageableDto;
import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.entity.User_;
import com.nashtech.rookies.assetmanagement.exception.BadRequestException;
import com.nashtech.rookies.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.rookies.assetmanagement.mapper.UserMapper;
import com.nashtech.rookies.assetmanagement.repository.RoleRepository;
import com.nashtech.rookies.assetmanagement.repository.TokenRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.service.UserService;
import com.nashtech.rookies.assetmanagement.specifications.UserSpecification;
import com.nashtech.rookies.assetmanagement.util.GenderConstant;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.PrefixConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final int AGE = 18;

    @Override
    public ResponseDto<PageableDto<List<UserDto>>> getAll(UserGetRequest requestParams, Pageable pageable, UserDetailsDto requestUser) {

        LocationConstant requestUserLocation = requestUser.getLocation();
        Specification<User> spec = UserSpecification.userListFilter(requestParams.getSearch(), requestParams.getTypes(), requestUserLocation);

        PageRequest pageRequest = (PageRequest) pageable;
        if (pageRequest.getSort().equals(Sort.unsorted())) {
            pageRequest = pageRequest.withSort(Direction.ASC, User_.FIRST_NAME);
        }

        var userDtos = userRepository.findAll(spec, pageRequest).map(userMapper::entityToDto);

        PageableDto<List<UserDto>> usersPageDto = PageableDto.<List<UserDto>>builder()
                .content(userDtos.getContent())
                .currentPage(userDtos.getNumber())
                .totalPage(userDtos.getTotalPages())
                .totalElements(userDtos.getTotalElements())
                .build();
        return ResponseDto.<PageableDto<List<UserDto>>>builder()
                .data(usersPageDto)
                .message("Get all users successfully.")
                .build();
    }

    public User getUserEntityById(Integer id) {
        return userRepository.findByIdAndStatus(id, StatusConstant.ACTIVE)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    @Override
    public ResponseDto<UserDto> getUserById(Integer id) {
        var user = this.getUserEntityById(id);
        return ResponseDto.<UserDto>builder()
                .data(userMapper.entityToDto(user))
                .message("Get user by id successfully.")
                .build();
    }

    @Override
    public ResponseDto<UserDto> saveUser(CreateUserRequest request, UserDetailsDto requestUser) {

        var role = roleRepository.findById(request.getRoleId()).orElseThrow(() -> new ResourceNotFoundException("Role not found."));
        if (!isValidAge(request.getDateOfBirth()))
            throw new InvalidDateException("User is under 18. Please select a different date");
        if (request.getJoinedDate().isBefore(request.getDateOfBirth()))
            throw new InvalidDateException("Joined date is not later than Date of Birth. Please select a different date");
        if (isWeekend(request.getJoinedDate()))
            throw new InvalidDateException("joined date is Saturday or Sunday. Please select a different date");

        String username = genUsername(request);
        int duplicateNum = userRepository.findByUsernameStartsWith(username).size();
        var mappedUser = userMapper.createUserRequestToEntity(request);
        mappedUser.setUsername(duplicateNum > 0 ? username + duplicateNum : username);
        mappedUser.setRole(role);
        mappedUser.setLocation(requestUser.getLocation());

        String tempPassword = mappedUser.getUsername()+"@"+mappedUser.getDateOfBirth().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        mappedUser.setPassword(passwordEncoder.encode(tempPassword));
        mappedUser.setIsChangePassword(false);
        mappedUser = userRepository.save(mappedUser);
        mappedUser.setStatus(StatusConstant.ACTIVE);
        NumberFormat nf = new DecimalFormat("0000");
        mappedUser.setStaffCode(PrefixConstant.SD.toString()+nf.format(mappedUser.getId()));
        var user = userRepository.save(mappedUser);
        return ResponseDto.<UserDto>builder()
                .data(userMapper.entityToDto(user))
                .message("Create user successfully.")
                .build();
    }

    private String genUsername(CreateUserRequest request) {
        List<String> firstName = Arrays.stream(request.getLastName().split(" ")).toList();
        return request.getFirstName().toLowerCase()+firstName.stream().map(s -> s.substring(0, 1)) // Extract the first character of each string
                .collect(Collectors.joining()).toLowerCase();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsernameAndStatus(username, StatusConstant.ACTIVE)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return userMapper.entityToUserDetailsDto(user);
    }

    public ResponseDto<UserDto> updateUser(UpdateUserRequest request, Integer userId) {
        //Validate
        var user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        var role = roleRepository.findById(request.getType()).orElseThrow(() -> new ResourceNotFoundException("Role not found."));
        if (!isValidAge(request.getDateOfBirth()))
            throw new InvalidDateException("User is under 18. Please select a different date");
        if (request.getJoinedDate().isBefore(request.getDateOfBirth()))
            throw new InvalidDateException("Joined date is not later than Date of Birth. Please select a different date");
        if (isWeekend(request.getJoinedDate()))
            throw new InvalidDateException("joined date is Saturday or Sunday. Please select a different date");
        //Update
        User updatedUser = userMapper.updateUserRequestToEntity(user, request);
        updatedUser.setRole(role);
        var returnUser = userRepository.save(updatedUser);
        return ResponseDto.<UserDto>builder()
                .data(userMapper.entityToDto(updatedUser))
                .message("Update user successfully")
                .build();
    }

    @Override
    public ResponseDto<Void> changePassword(Integer userId, ChangePasswordRequest request) {
        var user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        if (request.getOldPassword() == null) {
            if (user.getIsChangePassword()) {
                throw new BadRequestException("You must provide your current password.");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setIsChangePassword(true);
            userRepository.save(user);
        } else {
            if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
            } else {
                throw new BadRequestException("Password is incorrect.");
            }
        }

        return ResponseDto.<Void>builder()
                .message("Change password successfully.")
                .build();
    }

    private boolean isValidAge(LocalDate dateOfBirth) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dateOfBirth, currentDate);
        return period.getYears() >= 18;
    }

    private boolean isWeekend(LocalDate joinedDate) {
        DayOfWeek dayOfWeek = joinedDate.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.SATURDAY;
    }

    public ResponseDto<Void> disableUser (Integer userId) {
        var user = this.getUserEntityById(userId);
        user.setStatus(StatusConstant.INACTIVE);
        var tokens = user.getTokens();
        if (tokens != null) {
            tokenRepository.deleteAll(tokens);
        }
        userRepository.save(user);
        return ResponseDto.<Void>builder()
                .message("Disable user successfully.")
                .build();
    }

}
