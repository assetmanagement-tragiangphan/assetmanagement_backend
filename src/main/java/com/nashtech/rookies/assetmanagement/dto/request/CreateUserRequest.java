package com.nashtech.rookies.assetmanagement.dto.request;

import com.nashtech.rookies.assetmanagement.util.GenderConstant;
import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private int roleId;
    private String firstName;
    private String lastName;
    private GenderConstant gender;
    private LocalDate joinedDate;
    private LocalDate dateOfBirth;
}