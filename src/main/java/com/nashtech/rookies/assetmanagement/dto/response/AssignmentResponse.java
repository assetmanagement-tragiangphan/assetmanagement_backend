package com.nashtech.rookies.assetmanagement.dto.response;

import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {
    private Integer id;
    private String assetCode;
    private String assetName;
    private String assignBy;
    private String assignTo;
    private LocalDate assignedDate;
    private String note;
    private StatusConstant status;
}
