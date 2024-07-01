package com.nashtech.rookies.assetmanagement.dto.response;

import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnAssetRequestDTO {
    private Integer id;
    private String assetCode;
    private String assetName;
    private String requestedBy;
    private LocalDate assignedDate;
    private String acceptedBy;
    private String returnedDate = "";
    private StatusConstant state;
}
