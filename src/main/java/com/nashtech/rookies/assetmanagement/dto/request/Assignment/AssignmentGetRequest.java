package com.nashtech.rookies.assetmanagement.dto.request.Assignment;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentGetRequest {
    private String search;
    private List<String> status; 
    private LocalDate assignedDate;
}
