package com.onclass.ms_capability.application.usecase;

import com.onclass.ms_capability.domain.exception.BusinessException;
import com.onclass.ms_capability.domain.exception.ErrorCode;
import com.onclass.ms_capability.domain.repository.CapabilityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssociateBootcampCapabilitiesUseCaseTest {

    @Mock
    private CapabilityRepository capabilityRepository;

    @InjectMocks
    private AssociateBootcampCapabilitiesUseCase associateBootcampCapabilitiesUseCase;

    @Test
    void associateCapabilitiesSuccess() {
        Long bootcampId = 1L;
        List<Long> capabilityIds = List.of(1L, 2L, 3L);

        when(capabilityRepository.findExistingIds(Set.of(1L, 2L, 3L)))
            .thenReturn(Flux.just(1L, 2L, 3L));
        when(capabilityRepository.associateBootcampWithCapabilities(eq(bootcampId), anyList()))
            .thenReturn(Mono.empty());

        StepVerifier.create(associateBootcampCapabilitiesUseCase.associate(bootcampId, capabilityIds))
            .verifyComplete();

        verify(capabilityRepository).associateBootcampWithCapabilities(eq(bootcampId), anyList());
    }

    @Test
    void associateTechnologiesSuccessWhenDuplicateIdsArePassed() {
        Long bootcampId = 1L;
        List<Long> duplicatedIds = List.of(1L, 1L, 2L, 3L);

        when(capabilityRepository.findExistingIds(Set.of(1L, 2L, 3L)))
            .thenReturn(Flux.just(1L, 2L, 3L));
        when(capabilityRepository.associateBootcampWithCapabilities(eq(bootcampId), anyList()))
            .thenReturn(Mono.empty());

        StepVerifier.create(associateBootcampCapabilitiesUseCase.associate(bootcampId, duplicatedIds))
            .verifyComplete();

        verify(capabilityRepository).findExistingIds(Set.of(1L, 2L, 3L));
    }

    @Test
    void failedAssociateWhenTechnologiesDoNotExist() {
        Long bootcampId = 1L;
        List<Long> capabilityIds = List.of(1L, 2L, 99L);

        when(capabilityRepository.findExistingIds(Set.of(1L, 2L, 99L)))
            .thenReturn(Flux.just(1L, 2L));

        StepVerifier.create(associateBootcampCapabilitiesUseCase.associate(bootcampId, capabilityIds))
            .expectErrorSatisfies(throwable -> {
                assertThat(throwable).isInstanceOf(BusinessException.class);
                BusinessException ex = (BusinessException) throwable;
                assertThat(ex.getCode()).isEqualTo(ErrorCode.CAPABILITY_NOT_FOUND.getCode());
                assertThat(ex.getMessage()).contains("99");
            })
            .verify();

        verify(capabilityRepository, never()).associateBootcampWithCapabilities(eq(bootcampId), anyList());
    }
}
