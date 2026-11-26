package com.onclass.ms_capability.infrastructure.handler;

import com.onclass.ms_capability.application.usecase.*;
import com.onclass.ms_capability.domain.exception.BusinessException;
import com.onclass.ms_capability.domain.exception.ErrorCode;
import com.onclass.ms_capability.infrastructure.adapter.rest.dto.AssociateBootcampRequestDto;
import com.onclass.ms_capability.infrastructure.adapter.rest.dto.RegisterCapabilityRequestDto;
import com.onclass.ms_capability.infrastructure.adapter.rest.mapper.CapabilityRestMapper;
import com.onclass.ms_capability.infrastructure.validation.ValidationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CapabilityHandler {
    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final String SORT_BY_PARAM = "sortBy";
    private static final String SORT_DIRECTION_PARAM = "sortDirection";

    private final ListCapabilitiesUseCase listCapabilitiesUseCase;
    private final ListCapabilitiesByBootcampUseCase listCapabilitiesByBootcampUseCase;
    private final RegisterCapabilityUseCase registerCapabilityUseCase;
    private final AssociateBootcampCapabilitiesUseCase associateBootcampCapabilitiesUseCase;
    private final DeleteCapabilitiesByBootcampUseCase deleteCapabilitiesByBootcampUseCase;
    private final TransactionalOperator transactionalOperator;
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

    public Mono<ServerResponse> getCapabilities(ServerRequest serverRequest) {
        int page;
        int size;

        try {
            page = Integer.parseInt(serverRequest.queryParam(PAGE_PARAM).orElse("0"));
            size = Integer.parseInt(serverRequest.queryParam(SIZE_PARAM).orElse("20"));
        } catch (NumberFormatException ex) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_PAGINATION_PARAMS));
        }

        String sortBy = serverRequest.queryParam(SORT_BY_PARAM).orElse("name");
        String sortDirection = serverRequest.queryParam(SORT_DIRECTION_PARAM).orElse("ASC");

        return listCapabilitiesUseCase.getCapabilities(page, size, sortBy, sortDirection)
            .map(capabilityRestMapper::toCapabilityWithTechnologiesDto)
            .collectList()
            .flatMap(capabilities -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(capabilities));
    }

    public Mono<ServerResponse> associateBootcamp(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AssociateBootcampRequestDto.class)
            .flatMap(validationDto::validate)
            .flatMap(associateBootcampRequestDto ->
                associateBootcampCapabilitiesUseCase.associate(
                    associateBootcampRequestDto.getBootcampId(), associateBootcampRequestDto.getCapabilityIds()))
            .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> getCapabilitiesByBootcampId(ServerRequest serverRequest) {
        Long bootcampId;
        try {
            bootcampId = Long.valueOf(serverRequest.pathVariable("bootcampId"));
        } catch (NumberFormatException ex) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_BOOTCAMP_ID));
        }

        return listCapabilitiesByBootcampUseCase.getCapabilitiesByBootcampId(bootcampId)
            .map(capabilityRestMapper::toBootcampCapabilityResponseDto)
            .collectList()
            .flatMap(capabilities ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(capabilities));

    }

    public Mono<ServerResponse> deleteByBootcampId(ServerRequest serverRequest) {
        Long bootcampId = Long.valueOf(serverRequest.pathVariable("bootcampId"));
        return deleteCapabilitiesByBootcampUseCase.deleteByBootcampId(bootcampId)
            .as(transactionalOperator::transactional)
            .then(ServerResponse.noContent().build());
    }
}
