package com.nashtech.rookies.assetmanagement.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResponseDto<T> {
    private T data;
    private String message;
}
