package com.nashtech.rookies.assetmanagement.dto.request;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private LocalDate dateOfBirth;
    private LocalDate joinedDate;
    private String gender;
    private Integer type; //(roleId)
}
