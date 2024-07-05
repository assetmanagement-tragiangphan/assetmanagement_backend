package com.nashtech.rookies.assetmanagement.dto.request.User;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGetRequest {
    private String search;
    private List<Integer> types;
    private Boolean self=false;
}
