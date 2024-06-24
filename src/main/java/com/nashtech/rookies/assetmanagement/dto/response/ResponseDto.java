package com.nashtech.rookies.assetmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ResponseDto<T> {
    private T data;
    private String message;
}
