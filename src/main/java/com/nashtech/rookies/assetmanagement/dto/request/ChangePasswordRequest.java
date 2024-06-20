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
    @Pattern(regexp = "^(?=.*[a-zA-Z\\S])(?=.*\\d)[a-zA-Z\\d\\S]{8,}$",
            message = "Password must have at least 8 characters, including letters (or special characters) and numbers")
    private String newPassword;
}
