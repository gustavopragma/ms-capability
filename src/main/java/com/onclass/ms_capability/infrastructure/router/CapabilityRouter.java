package com.onclass.ms_capability.infrastructure.router;

import com.onclass.ms_capability.infrastructure.adapter.rest.dto.CapabilityResponseDto;
import com.onclass.ms_capability.infrastructure.adapter.rest.dto.CapabilityWithTechnologiesDto;
import com.onclass.ms_capability.infrastructure.adapter.rest.dto.RegisterCapabilityRequestDto;
import com.onclass.ms_capability.infrastructure.exceptionHandler.ErrorResponse;
import com.onclass.ms_capability.infrastructure.handler.CapabilityHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CapabilityRouter {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/capabilities",
            method = RequestMethod.POST,
            beanClass = CapabilityHandler.class,
            beanMethod = "registerCapability",
            operation = @Operation(
                operationId = "registerCapability",
                summary = "Register a new capability with associated technologies",
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterCapabilityRequestDto.class))
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        description = "Capability registered successfully",
                        content = @Content(schema = @Schema(implementation = CapabilityResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Validation or business rule error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/capabilities",
            method = RequestMethod.GET,
            beanClass = CapabilityHandler.class,
            beanMethod = "getCapabilities",
            operation = @Operation(
                operationId = "getCapabilities",
                summary = "List capabilities with technologies using pagination and sorting ",
                parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number (0-indexed)", example = "0"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", example = "10"),
                    @Parameter(name = "sortBy", in = ParameterIn.QUERY, description = "Sort field: name or technologyCount", example = "name"),
                    @Parameter(name = "sortDirection", in = ParameterIn.QUERY, description = "Sort direction: ASC or DESC", example = "ASC")
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Capabilities retrieved successfully",
                        content = @Content(array = @ArraySchema(schema = @Schema(implementation = CapabilityWithTechnologiesDto.class)))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid pagination or sort parameters",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                }
            )
        )
    })
    public RouterFunction<ServerResponse> capabilityRoutes(CapabilityHandler capabilityHandler) {
        return route(POST("/api/v1/capabilities"), capabilityHandler::registerCapability)
            .andRoute(GET("/api/v1/capabilities"), capabilityHandler::getCapabilities);
    }
}
