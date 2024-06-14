package com.nashtech.rookies.assetmanagement.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class UpdateUserRequest {
    private LocalDate dateOfBirth;
    private LocalDate joinedDate;
    private String gender;
    private Integer type; //(roleId)
}
