package com.nashtech.rookies.assetmanagement.dto.request.Assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssignmentRequest {
    private String staffCode;
    private String assetCode;
    private LocalDate assignedDate;
    @Max(value = 300, message = "Note cannot exceed 300 characters")
    private String note;
}
