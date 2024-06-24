package com.nashtech.rookies.assetmanagement.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Integer id;
    private String prefix;
    private String name;
}
