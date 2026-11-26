package com.onclass.ms_capability.domain.exception;

public enum ErrorCode {
    CAPABILITY_ALREADY_EXISTS("SE_001", "Capability name already exists"),
    INVALID_TECHNOLOGY_COUNT("SE_002", "A capability must have between 3 and 20 technologies"),
    DUPLICATE_TECHNOLOGIES("SE_003", "Technologies list cannot contain duplicate IDs"),
    ASSOCIATE_TECHNOLOGY_ERROR("SE004", "Error associating technologies in technology service"),
    INTERNAL_SERVER_ERROR("SE005", "Unexpected internal server error"),
    VALIDATION_ERROR("SE_006", "Validation error"),
    TECHNOLOGY_SERVICE_UNAVAILABLE("SE007", "Technology service is currently unavailable"),
    INVALID_PAGINATION_PARAMS("SE008", "Page must be >= 0 and size must be > 0"),
    INVALID_SORT_PARAMS("SE009", "Invalid sortBy value(name, technologyCount) or sortDirection(ASC, DESC)"),
    RETRIEVE_TECHNOLOGIES_ERROR("SE010", "Error retrieving technology"),
    CAPABILITY_NOT_FOUND("SE011", "Capability don't exists"),
    INVALID_BOOTCAMP_ID("SE012", "Invalid bootcamp id param"),
    ERROR_DELETE_TECHNOLOGIES("SE012", "Error deleting in technology service");


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