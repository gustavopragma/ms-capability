package com.onclass.ms_capability.infrastructure.handler;

import com.onclass.ms_capability.application.usecase.RegisterCapabilityUseCase;
import com.onclass.ms_capability.infrastructure.adapter.rest.dto.RegisterCapabilityRequestDto;
import com.onclass.ms_capability.infrastructure.adapter.rest.mapper.CapabilityRestMapper;
import com.onclass.ms_capability.infrastructure.validation.ValidationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CapabilityHandler {
    private final RegisterCapabilityUseCase registerCapabilityUseCase;
    private final CapabilityRestMapper capabilityRestMapper;
    private final ValidationDto validationDto;

    public Mono<ServerResponse> registerCapability(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegisterCapabilityRequestDto.class)
            .flatMap(validationDto::validate)
            .map(capabilityRestMapper::toDomain)
            .flatMap(registerCapabilityUseCase::registerCapability)
            .map(capabilityRestMapper::toResponseDto)
            .flatMap(capabilityResponseDto ->
                ServerResponse.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(capabilityResponseDto));
    }
}
