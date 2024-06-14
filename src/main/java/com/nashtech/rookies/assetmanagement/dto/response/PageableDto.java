package com.nashtech.rookies.assetmanagement.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PageableDto<T> {
    private T content;
    private Integer currentPage;
    private Integer totalPage;
    private Long totalElements;
}
