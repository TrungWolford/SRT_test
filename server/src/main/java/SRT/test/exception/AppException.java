package SRT.test.exception;

import lombok.Getter;

/**
 * Custom application exception that wraps ErrorEnum
 * Provides consistent error handling across the application
 */
@Getter
public class AppException extends RuntimeException {

    private final ErrorEnum errorEnum;

    /**
     * Create exception with ErrorEnum (uses default message)
     */
    public AppException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }

    /**
     * Create exception with ErrorEnum and custom message
     */
    public AppException(ErrorEnum errorEnum, String customMessage) {
        super(customMessage);
        this.errorEnum = errorEnum;
    }

    /**
     * Create exception with ErrorEnum and cause
     */
    public AppException(ErrorEnum errorEnum, Throwable cause) {
        super(errorEnum.getMessage(), cause);
        this.errorEnum = errorEnum;
    }

    /**
     * Create exception with ErrorEnum, custom message and cause
     */
    public AppException(ErrorEnum errorEnum, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorEnum = errorEnum;
    }

    /**
     * Get the HTTP status code associated with this exception
     */
    public int getStatusCode() {
        return errorEnum.getHttpStatus().value();
    }

    /**
     * Get the error code as integer
     */
    public int getErrorCode() {
        return errorEnum.getCode();
    }

    /**
     * Get the error code as string
     */
    public String getErrorCodeAsString() {
        return errorEnum.getCodeAsString();
    }

    /**
     * Check if this is a client error (4xx)
     */
    public boolean isClientError() {
        return errorEnum.isClientError();
    }

    /**
     * Check if this is a server error (5xx)
     */
    public boolean isServerError() {
        return errorEnum.isServerError();
    }

    /**
     * Check if this is an authentication error
     */
    public boolean isAuthenticationError() {
        return errorEnum.isAuthenticationError();
    }

    /**
     * Check if this is an authorization error
     */
    public boolean isAuthorizationError() {
        return errorEnum.isAuthorizationError();
    }

    @Override
    public String toString() {
        return String.format("AppException{errorCode=%d, message='%s', httpStatus=%s}",
                errorEnum.getCode(), getMessage(), errorEnum.getHttpStatus());
    }
}

