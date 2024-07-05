package com.nashtech.rookies.assetmanagement.dto.response;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.nashtech.rookies.assetmanagement.util.StatusConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@AllArgsConstructor
// @NoArgsConstructor
public class AssignmentDetailResponse {
    private Integer id;
    private String assetCode;
    private String assetName;
    private String category;
    private String specification;
    private String assignedTo;
    private String assignedBy;
    private LocalDate assignedDate;
    private StatusConstant status;
    private String note;
}
