package com.nashtech.rookies.assetmanagement.dto.request.Asset;

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
public class EditAssetRequest {
    private String assetName;
    private String specification;
    private LocalDate installDate;
    private StatusConstant assetState;
}