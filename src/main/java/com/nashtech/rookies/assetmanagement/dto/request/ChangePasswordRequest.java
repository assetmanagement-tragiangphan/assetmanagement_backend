package com.nashtech.rookies.assetmanagement.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    private String oldPassword;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$!^%*?&])[A-Za-z\\d@#$!%*?&]{8,}$",
            message = "Password must have at least 8 characters, including at least 1 uppercase letters, alphanumeric " +
                    "and special characters")
    private String newPassword;
}
