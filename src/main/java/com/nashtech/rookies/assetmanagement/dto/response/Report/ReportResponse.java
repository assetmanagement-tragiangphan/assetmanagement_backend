package com.nashtech.rookies.assetmanagement.dto.response.Report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private String category;
    private Long total;
    private Long assigned;
    private Long available;
    private Long notAvailable;
    private Long waitingForRecycling;
    private Long recycled;
}
