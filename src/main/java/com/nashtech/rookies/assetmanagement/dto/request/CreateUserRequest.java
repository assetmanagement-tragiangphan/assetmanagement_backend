package com.nashtech.rookies.assetmanagement.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class CreateUserRequest {
    private Integer roleId;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate joinedDate;
    private LocalDate dateOfBirth;
}
