package SRT.test.dto.request.Auth;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotBlank(message = "Password is required")
    private String password;
}

