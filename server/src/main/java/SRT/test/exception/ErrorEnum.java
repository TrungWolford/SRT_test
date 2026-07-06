package SRT.test.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Centralized error code definitions for the application.
 */
@Getter
public enum ErrorEnum {

    // ===== GENERAL ERRORS (0 - 999) =====
    BAD_REQUEST(0, "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(1, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR(2, "Xác thực dữ liệu thất bại", HttpStatus.BAD_REQUEST),
    FORBIDDEN(3, "Truy cập bị từ chối", HttpStatus.FORBIDDEN),
    NOT_FOUND(4, "Không tìm thấy tài nguyên", HttpStatus.NOT_FOUND),
    CONFLICT(5, "Tài nguyên đã tồn tại", HttpStatus.CONFLICT),
    INTERNAL_SERVER_ERROR(999, "Lỗi máy chủ nội bộ", HttpStatus.INTERNAL_SERVER_ERROR),

    // ===== USER MANAGEMENT (1000 - 1999) =====
    USER_NOT_FOUND(1000, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(1100, "Người dùng đã tồn tại", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(1101, "Email đã được đăng ký", HttpStatus.CONFLICT),
    ACCOUNT_NAME_ALREADY_EXISTS(1102, "Tên tài khoản đã tồn tại", HttpStatus.CONFLICT),
    PHONE_NUMBER_ALREADY_EXISTS(1103, "Số điện thoại đã được đăng ký", HttpStatus.CONFLICT),
    PASSWORDS_NOT_MATCH(1200, "Mật khẩu và xác nhận mật khẩu không khớp", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT(1201, "Định dạng email không hợp lệ", HttpStatus.BAD_REQUEST),
    WEAK_PASSWORD(1204, "Mật khẩu không đáp ứng yêu cầu bảo mật", HttpStatus.BAD_REQUEST),


    // ===== AUTHENTICATION (2000 - 2999) =====
    UNAUTHORIZED(2000, "Chưa được xác thực", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(2001, "Tên đăng nhập hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(2002, "Token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(2003, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(2004, "Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_INVALID(2005, "Access token không hợp lệ", HttpStatus.UNAUTHORIZED);


    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorEnum(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCodeAsString() {
        return String.valueOf(code);
    }

    public boolean isClientError() {
        return httpStatus.is4xxClientError();
    }

    public boolean isServerError() {
        return httpStatus.is5xxServerError();
    }

    public boolean isAuthenticationError() {
        return httpStatus == HttpStatus.UNAUTHORIZED;
    }

    public boolean isAuthorizationError() {
        return httpStatus == HttpStatus.FORBIDDEN;
    }
}

