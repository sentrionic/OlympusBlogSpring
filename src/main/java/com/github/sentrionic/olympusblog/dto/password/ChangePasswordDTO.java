package com.github.sentrionic.olympusblog.dto.password;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@PasswordsEqualConstraint
public class ChangePasswordDTO {
    @NotNull(message = "Current Password is required")
    private String currentPassword;

    @NotNull(message = "New Password is required")
    @Size(min = 6, max = 150, message = "Password must be between 6 and 150 characters")
    private String newPassword;

    @NotNull(message = "New Password is required")
    @Size(min = 6, max = 150, message = "Password must be between 6 and 150 characters")
    private String confirmNewPassword;
}
