package com.nashtech.rookies.assetmanagement.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LoginResponse {
    private Integer roleId;
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isChangePassword;
    private String token;
    private String location;

}
