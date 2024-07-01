package com.nashtech.rookies.assetmanagement.dto.request.Assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditAssignmentRequest {
    private String staffCode;
    private String assetCode;
    private String note;
}
