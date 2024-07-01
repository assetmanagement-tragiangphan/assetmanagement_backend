package com.nashtech.rookies.assetmanagement.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDetailResponse {
    private String assetCode;
    private String assetName;
    private String category;
    private String specification;
    private String assignedTo;
    private String assignedBy;
    private LocalDate assignedDate;
    private String status;
    private String note;
}
