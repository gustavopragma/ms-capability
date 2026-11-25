package com.onclass.ms_capability.infrastructure.adapter.webclient.repository;

import com.onclass.ms_capability.domain.exception.BusinessException;
import com.onclass.ms_capability.domain.exception.ErrorCode;
import com.onclass.ms_capability.domain.model.Technology;
import com.onclass.ms_capability.domain.repository.TechnologyRepository;
import com.onclass.ms_capability.infrastructure.adapter.webclient.dto.AssociateTechnologiesRequestDto;
import com.onclass.ms_capability.infrastructure.adapter.webclient.dto.ErrorResponseDto;
import com.onclass.ms_capability.infrastructure.adapter.webclient.dto.TechnologyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TechnologyRepositoryImpl implements TechnologyRepository {

    private static final String ASSOCIATE_TECHNOLOGY_PATH = "/api/v1/technologies/associate-capability";
    private static final String GET_TECHNOLOGIES_PATH = "/api/v1/technologies/by-capability/{capabilityId}";

    private final WebClient webClient;

    @Override
    public Mono<Void> associateTechnologies(Long capabilityId, List<Long> technologyIds) {
        AssociateTechnologiesRequestDto request = new AssociateTechnologiesRequestDto(
            capabilityId,
            technologyIds
        );

        return webClient.post()
            .uri(ASSOCIATE_TECHNOLOGY_PATH)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                clientResponse.bodyToMono(ErrorResponseDto.class)
                    .flatMap(errorBody -> Mono.error(
                        new BusinessException(ErrorCode.ASSOCIATE_TECHNOLOGY_ERROR, errorBody.getDescription())
                    )))
            .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                Mono.error(new BusinessException(ErrorCode.TECHNOLOGY_SERVICE_UNAVAILABLE)))
            .bodyToMono(Void.class)
            .onErrorMap(WebClientRequestException.class, ex ->
                new BusinessException(ErrorCode.TECHNOLOGY_SERVICE_UNAVAILABLE));
    }

    @Override
    public Flux<Technology> findTechnologyByCapabilityId(Long capabilityId) {
        return webClient.get()
            .uri(GET_TECHNOLOGIES_PATH, capabilityId)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                clientResponse.bodyToMono(ErrorResponseDto.class)
                    .flatMap(errorBody -> Mono.error(
                        new BusinessException(ErrorCode.RETRIEVE_TECHNOLOGIES_ERROR, errorBody.getDescription())
                    )))
            .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                Mono.error(new BusinessException(ErrorCode.TECHNOLOGY_SERVICE_UNAVAILABLE)))
            .bodyToFlux(TechnologyResponseDto.class)
            .map(technologyResponseDto ->
                new Technology(technologyResponseDto.getId(), technologyResponseDto.getName()))
            .onErrorMap(WebClientRequestException.class, ex ->
                new BusinessException(ErrorCode.TECHNOLOGY_SERVICE_UNAVAILABLE));
    }
}
