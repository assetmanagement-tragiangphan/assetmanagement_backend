package com.nashtech.rookies.assetmanagement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class UserDto {
    private Integer id;
    private Integer roleId;
    private String staffCode;
    private String firstName;
    private String lastName;
    private String username;
    private String gender;
    private String location;
    private LocalDate joinedDate;
    private LocalDate dateOfBirth;
    private Boolean isChangePassword;
    private String status;
}
