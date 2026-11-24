package com.onclass.ms_capability.domain.exception;

public enum ErrorCode {
    CAPABILITY_ALREADY_EXISTS("SE_001", "Technology name already exists"),
    INVALID_TECHNOLOGY_COUNT("SE_002", "A capability must have between 3 and 20 technologies"),
    DUPLICATE_TECHNOLOGIES("SE_003", "Technologies list cannot contain duplicate IDs");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}