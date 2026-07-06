package SRT.test.dto.response.Auth;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private String message;
    private String userName;
    private String accountName;
    private String fullName;

    public static RegisterResponse success(String userName, String accountName, String fullName) {
        return RegisterResponse.builder()
                .message("Registration successful!")
                .userName(userName)
                .accountName(accountName)
                .fullName(fullName)
                .build();
    }
}