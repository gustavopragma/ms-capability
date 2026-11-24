package com.onclass.ms_capability.infrastructure.adapter.webclient.repository;

import com.onclass.ms_capability.domain.exception.BusinessException;
import com.onclass.ms_capability.domain.exception.ErrorCode;
import com.onclass.ms_capability.domain.repository.TechnologyRepository;
import com.onclass.ms_capability.infrastructure.adapter.webclient.dto.AssociateTechnologiesRequestDto;
import com.onclass.ms_capability.infrastructure.adapter.webclient.dto.ErrorResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TechnologyRepositoryImpl implements TechnologyRepository {

    private final WebClient webClient;

    @Override
    public Mono<Void> associateTechnologies(Long capabilityId, List<Long> technologyIds) {
        AssociateTechnologiesRequestDto request = new AssociateTechnologiesRequestDto(
            capabilityId,
            technologyIds
        );

        return webClient.post()
            .uri("/api/v1/technologies/associate-capability")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                clientResponse.bodyToMono(ErrorResponseDto.class)
                    .flatMap(errorBody -> Mono.error(
                        new BusinessException(ErrorCode.ASSOCIATE_TECHNOLOGY_ERROR, errorBody.getDescription())
                    )))
            .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                Mono.error(new BusinessException(
                    ErrorCode.ASSOCIATE_TECHNOLOGY_ERROR,
                    "Technology service is currently unavailable"
                )))
            .bodyToMono(Void.class);
    }
}
