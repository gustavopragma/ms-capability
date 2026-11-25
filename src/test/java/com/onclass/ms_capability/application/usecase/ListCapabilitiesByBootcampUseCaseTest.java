package com.onclass.ms_capability.application.usecase;

import com.onclass.ms_capability.domain.exception.BusinessException;
import com.onclass.ms_capability.domain.exception.ErrorCode;
import com.onclass.ms_capability.domain.model.Capability;
import com.onclass.ms_capability.domain.model.Technology;
import com.onclass.ms_capability.domain.repository.CapabilityRepository;
import com.onclass.ms_capability.domain.repository.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListCapabilitiesByBootcampUseCaseTest {

    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private ListCapabilitiesByBootcampUseCase listCapabilitiesByBootcampUseCase;

    private Capability backendCapability;
    private Capability frontendCapability;

    @BeforeEach
    void setUp() {
        backendCapability = new Capability(1L, "Backend", "Java y Spring", null);
        frontendCapability = new Capability(2L, "Frontend", "Angular y React", null);
    }

    @Test
    void getCapabilitiesByBootcampIdSuccess() {
        Long bootcampId = 100L;

        when(capabilityRepository.findAllByBootcampId(bootcampId))
            .thenReturn(Flux.just(backendCapability, frontendCapability));

        when(technologyRepository.findTechnologyByCapabilityId(1L))
            .thenReturn(Flux.just(
                new Technology(10L, "Java"),
                new Technology(11L, "Spring Boot")
            ));

        when(technologyRepository.findTechnologyByCapabilityId(2L))
            .thenReturn(Flux.just(
                new Technology(20L, "Angular")
            ));

        StepVerifier.create(listCapabilitiesByBootcampUseCase
                .getCapabilitiesByBootcampId(bootcampId).collectList())
            .assertNext(capabilities -> {
                assertEquals(2, capabilities.size());

                Capability first = capabilities.stream()
                    .filter(c -> c.getId().equals(1L))
                    .findFirst()
                    .orElseThrow();
                assertEquals("Backend", first.getName());
                assertNotNull(first.getTechnologies());
                assertEquals(2, first.getTechnologies().size());

                Capability second = capabilities.stream()
                    .filter(c -> c.getId().equals(2L))
                    .findFirst()
                    .orElseThrow();
                assertEquals("Frontend", second.getName());
                assertNotNull(second.getTechnologies());
                assertEquals(1, second.getTechnologies().size());
            })
            .verifyComplete();
    }

    @Test
    void getCapabilitiesByBootcampIdEmptyWhenNoFoundCapabilities() {
        Long bootcampId = 999L;

        when(capabilityRepository.findAllByBootcampId(bootcampId))
            .thenReturn(Flux.empty());

        StepVerifier.create(listCapabilitiesByBootcampUseCase.getCapabilitiesByBootcampId(bootcampId))
            .verifyComplete();

        verify(technologyRepository, never()).findTechnologyByCapabilityId(anyLong());
    }

    @Test
    void propageErrorWhenTechnologyServiceFails() {
        Long bootcampId = 100L;

        when(capabilityRepository.findAllByBootcampId(bootcampId))
            .thenReturn(Flux.just(backendCapability));

        when(technologyRepository.findTechnologyByCapabilityId(1L))
            .thenReturn(Flux.error(new BusinessException(ErrorCode.TECHNOLOGY_SERVICE_UNAVAILABLE)));

        StepVerifier.create(listCapabilitiesByBootcampUseCase.getCapabilitiesByBootcampId(bootcampId))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.TECHNOLOGY_SERVICE_UNAVAILABLE.getCode()))
            .verify();
    }
}
