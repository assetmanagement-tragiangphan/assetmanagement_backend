package com.nashtech.rookies.assetmanagement.dto.request.ReturnAsset;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnAssetRequest {
    @NotNull
    private Integer assignmentId;
}
