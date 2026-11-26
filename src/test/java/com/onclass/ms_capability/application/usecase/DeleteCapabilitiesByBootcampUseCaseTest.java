package com.onclass.ms_capability.application.usecase;

import com.onclass.ms_capability.domain.exception.BusinessException;
import com.onclass.ms_capability.domain.exception.ErrorCode;
import com.onclass.ms_capability.domain.repository.CapabilityRepository;
import com.onclass.ms_capability.domain.repository.TechnologyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteCapabilitiesByBootcampUseCaseTest {

    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private DeleteCapabilitiesByBootcampUseCase deleteCapabilitiesByBootcampUseCase;

    @Test
    void deleteCapabilitiesByBootcampIdSuccess() {
        Long bootcampId = 5L;
        when(capabilityRepository.findCapabilityIdsByBootcampId(bootcampId))
            .thenReturn(Flux.just(10L, 20L));
        when(capabilityRepository.deleteAllByBootcampId(bootcampId))
            .thenReturn(Mono.empty());
        when(capabilityRepository.countByCapabilityId(10L)).thenReturn(Mono.just(0L));
        when(capabilityRepository.countByCapabilityId(20L)).thenReturn(Mono.just(1L));
        when(technologyRepository.deleteTechnologiesByCapabilityIds(List.of(10L)))
            .thenReturn(Mono.empty());
        when(capabilityRepository.deleteAllById(List.of(10L)))
            .thenReturn(Mono.empty());

        StepVerifier.create(deleteCapabilitiesByBootcampUseCase.deleteByBootcampId(bootcampId))
            .verifyComplete();

        verify(capabilityRepository).deleteAllByBootcampId(bootcampId);
        verify(technologyRepository).deleteTechnologiesByCapabilityIds(List.of(10L));
        verify(capabilityRepository).deleteAllById(List.of(10L));
    }

    @Test
    void noDeleteCapabilitiesIfExistsAssociation() {
        Long bootcampId = 5L;
        when(capabilityRepository.findCapabilityIdsByBootcampId(bootcampId))
            .thenReturn(Flux.just(20L));
        when(capabilityRepository.deleteAllByBootcampId(bootcampId))
            .thenReturn(Mono.empty());
        when(capabilityRepository.countByCapabilityId(20L)).thenReturn(Mono.just(2L));

        StepVerifier.create(deleteCapabilitiesByBootcampUseCase.deleteByBootcampId(bootcampId))
            .verifyComplete();

        verify(technologyRepository, never()).deleteTechnologiesByCapabilityIds(anyList());
        verify(capabilityRepository, never()).deleteAllById(anyList());
    }

    @Test
    void successWhenBootcampCapabilitiesAreEmpty() {
        Long bootcampId = 5L;
        when(capabilityRepository.findCapabilityIdsByBootcampId(bootcampId))
            .thenReturn(Flux.empty());

        StepVerifier.create(deleteCapabilitiesByBootcampUseCase.deleteByBootcampId(bootcampId))
            .verifyComplete();

        verify(capabilityRepository, never()).deleteAllByBootcampId(anyLong());
        verify(technologyRepository, never()).deleteTechnologiesByCapabilityIds(anyList());
        verify(capabilityRepository, never()).deleteAllById(anyList());
    }

    @Test
    void throwErrorWhenTechnologyServiceFails() {
        Long bootcampId = 5L;
        PublisherProbe<Void> deleteCapabilitiesProbe = PublisherProbe.empty();

        when(capabilityRepository.findCapabilityIdsByBootcampId(bootcampId))
            .thenReturn(Flux.just(10L));
        when(capabilityRepository.deleteAllByBootcampId(bootcampId))
            .thenReturn(Mono.empty());
        when(capabilityRepository.countByCapabilityId(10L)).thenReturn(Mono.just(0L));
        when(technologyRepository.deleteTechnologiesByCapabilityIds(List.of(10L)))
            .thenReturn(Mono.error(new BusinessException(ErrorCode.ERROR_DELETE_TECHNOLOGIES)));
        when(capabilityRepository.deleteAllById(List.of(10L)))
            .thenReturn(deleteCapabilitiesProbe.mono());

        StepVerifier.create(deleteCapabilitiesByBootcampUseCase.deleteByBootcampId(bootcampId))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.ERROR_DELETE_TECHNOLOGIES.getCode()))
            .verify();

        deleteCapabilitiesProbe.assertWasNotSubscribed();
    }
}
