package com.nashtech.rookies.assetmanagement.dto.response;

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
