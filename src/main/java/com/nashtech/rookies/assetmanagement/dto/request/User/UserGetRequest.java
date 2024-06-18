package com.nashtech.rookies.assetmanagement.dto.request.User;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserGetRequest {
    private String search;
    private List<Integer> types;
}
