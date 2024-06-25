package com.nashtech.rookies.assetmanagement.dto.request.Assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {
    private String staffCode;
    private String assetCode;
    private LocalDate assignDate;
    private String note;
}
