package com.onclass.ms_capability.application.usecase;

import com.onclass.ms_capability.domain.exception.BusinessException;
import com.onclass.ms_capability.domain.exception.ErrorCode;
import com.onclass.ms_capability.domain.model.Capability;
import com.onclass.ms_capability.domain.repository.CapabilityRepository;
import com.onclass.ms_capability.domain.repository.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.LongStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterCapabilityUseCaseTest {

    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private RegisterCapabilityUseCase registerCapabilityUseCase;

    private Capability validCapability;

    @BeforeEach
    void setUp() {
        validCapability = new Capability(null, "Backend Java", "Desarrollo reactivo", List.of(1L, 2L, 3L));
    }

    @Test
    void successRegisterCapability() {
        Capability savedCapability = new Capability(10L, "Backend Java", "Desarrollo reactivo", List.of(1L, 2L, 3L));

        when(capabilityRepository.existsByName("Backend Java")).thenReturn(Mono.just(Boolean.FALSE));
        when(capabilityRepository.save(validCapability)).thenReturn(Mono.just(savedCapability));
        when(technologyRepository.associateTechnologies(10L, List.of(1L, 2L, 3L))).thenReturn(Mono.empty());

        StepVerifier.create(registerCapabilityUseCase.registerCapability(validCapability))
            .expectNextMatches(result -> result.getId().equals(10L) &&
                result.getName().equals("Backend Java") &&
                result.getTechnologyIds().size() == 3
            )
            .verifyComplete();

        verify(capabilityRepository, never()).deleteById(anyLong());
    }

    @Test
    void failWhenLessThanThreeTechnologies() {
        Capability invalidCapability = new Capability(null, "Frontend", "Web UI", List.of(1L, 2L));

        StepVerifier.create(registerCapabilityUseCase.registerCapability(invalidCapability))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_TECHNOLOGY_COUNT.getCode()))
            .verify();

        verify(capabilityRepository, never()).existsByName(anyString());
    }

    @Test
    void failWhenMoreThanTwentyTechnologies() {
        List<Long> twentyIds = LongStream.rangeClosed(1, 21).boxed().toList();
        Capability invalidCapability = new Capability(null, "Frontend", "Web UI", twentyIds);

        StepVerifier.create(registerCapabilityUseCase.registerCapability(invalidCapability))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_TECHNOLOGY_COUNT.getCode()))
            .verify();

        verify(capabilityRepository, never()).existsByName(anyString());
    }

    @Test
    void failWhenTechnologyIdsAreRepeated() {
        Capability invalidCapability = new Capability(null, "Frontend", "Web UI", List.of(1L, 2L, 1L));

        StepVerifier.create(registerCapabilityUseCase.registerCapability(invalidCapability))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.DUPLICATE_TECHNOLOGIES.getCode()))
            .verify();

        verify(capabilityRepository, never()).existsByName(anyString());
    }

    @Test
    void failWhenCapabilityNameExists() {
        when(capabilityRepository.existsByName("Backend Java")).thenReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(registerCapabilityUseCase.registerCapability(validCapability))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.CAPABILITY_ALREADY_EXISTS.getCode()))
            .verify();

        verify(capabilityRepository, never()).save(any());
        verify(technologyRepository, never()).associateTechnologies(anyLong(), anyList());
    }

    @Test
    void rollbackSavedCapabilityWhenTechnologyServiceFails() {
        Capability savedCapability = new Capability(10L, "Backend Java", "Desarrollo reactivo", List.of(1L, 2L, 3L));

        when(capabilityRepository.existsByName("Backend Java")).thenReturn(Mono.just(Boolean.FALSE));
        when(capabilityRepository.save(validCapability)).thenReturn(Mono.just(savedCapability));
        when(technologyRepository.associateTechnologies(10L, List.of(1L, 2L, 3L)))
            .thenReturn(Mono.error(new BusinessException(ErrorCode.TECHNOLOGY_SERVICE_UNAVAILABLE)));
        when(capabilityRepository.deleteById(10L)).thenReturn(Mono.empty());

        StepVerifier.create(registerCapabilityUseCase.registerCapability(validCapability))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.TECHNOLOGY_SERVICE_UNAVAILABLE.getCode()))
            .verify();

        verify(capabilityRepository).deleteById(10L);
    }
}
