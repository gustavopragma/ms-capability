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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListCapabilitiesUseCaseTest {

    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private ListCapabilitiesUseCase listCapabilitiesUseCase;

    private Capability capabilityA;
    private Capability capabilityB;
    private Capability capabilityC;

    @BeforeEach
    void setUp() {
        capabilityA = new Capability(1L, "Backend", "Java y Spring", null);
        capabilityB = new Capability(2L, "Mobile", "Flutter", null);
        capabilityC = new Capability(3L, "Frontend", "Angular y React", null);
    }

    private void createMocks() {
        when(capabilityRepository.findAll()).thenReturn(Flux.just(capabilityA, capabilityB, capabilityC));

        when(technologyRepository.findTechnologyByCapabilityId(1L)).thenReturn(Flux.just(
            new Technology(10L, "Java"),
            new Technology(11L, "Spring Boot"),
            new Technology(12L, "MySQL")
        ));

        when(technologyRepository.findTechnologyByCapabilityId(2L)).thenReturn(Flux.just(
            new Technology(20L, "Templates"),
            new Technology(21L, "Views")
        ));

        when(technologyRepository.findTechnologyByCapabilityId(3L)).thenReturn(Flux.just(
            new Technology(30L, "HTML"),
            new Technology(31L, "CSS"),
            new Technology(32L, "TypeScript"),
            new Technology(33L, "Angular")
        ));
    }

    @Test
    void getCapabilitiesSortByNameASCWithTechnologies() {
        createMocks();

        StepVerifier.create(listCapabilitiesUseCase.getCapabilities(0, 10, "name", "ASC"))
            .assertNext(firstCap -> {
                assertEquals("Backend", firstCap.getName());
                assertEquals(3, firstCap.getTechnologies().size());
            })
            .assertNext(secondCap -> {
                assertEquals("Frontend", secondCap.getName());
                assertEquals(4, secondCap.getTechnologies().size());
            })
            .assertNext(thirdCap -> {
                assertEquals("Mobile", thirdCap.getName());
                assertEquals(2, thirdCap.getTechnologies().size());
            })
            .verifyComplete();
    }

    @Test
    void getCapabilitiesSortByTechnologiesSizeAndDesc() {
        createMocks();

        StepVerifier.create(listCapabilitiesUseCase.getCapabilities(0, 10, "technologyCount", "DESC"))
            .assertNext(firstCap -> {
                assertEquals("Frontend", firstCap.getName());
                assertEquals(4, firstCap.getTechnologies().size());
            })
            .assertNext(secondCap -> {
                assertEquals("Backend", secondCap.getName());
                assertEquals(3, secondCap.getTechnologies().size());
            })
            .assertNext(thirdCap -> {
                assertEquals("Mobile", thirdCap.getName());
                assertEquals(2, thirdCap.getTechnologies().size());
            })
            .verifyComplete();
    }

    @Test
    void getCapabilitiesWithCorrectPagination() {
        createMocks();

        StepVerifier.create(listCapabilitiesUseCase.getCapabilities(1, 1, "name", "ASC"))
            .assertNext(result -> assertEquals("Frontend", result.getName()))
            .verifyComplete();
    }

    @Test
    void exceptionWhenParamsAreIncorrect() {
        StepVerifier.create(listCapabilitiesUseCase.getCapabilities(-1, 10, "name", "ASC"))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_PAGINATION_PARAMS.getCode()))
            .verify();

        StepVerifier.create(listCapabilitiesUseCase.getCapabilities(0, 0, "name", "ASC"))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_PAGINATION_PARAMS.getCode()))
            .verify();

        verify(capabilityRepository, never()).findAll();
    }

    @Test
    void exceptionWhenSortAreIncorrect() {
        StepVerifier.create(listCapabilitiesUseCase.getCapabilities(0, 10, "message", "ASC"))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_SORT_PARAMS.getCode()))
            .verify();

        StepVerifier.create(listCapabilitiesUseCase.getCapabilities(0, 10, "name", "ascendant"))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_SORT_PARAMS.getCode()))
            .verify();

        verify(capabilityRepository, never()).findAll();
    }

    @Test
    void failWhenTechnologyServiceIsDown() {
        when(capabilityRepository.findAll()).thenReturn(Flux.just(capabilityA));
        when(technologyRepository.findTechnologyByCapabilityId(1L))
            .thenReturn(Flux.error(new BusinessException(ErrorCode.TECHNOLOGY_SERVICE_UNAVAILABLE)));

        StepVerifier.create(listCapabilitiesUseCase.getCapabilities(0, 10, "name", "ASC"))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.TECHNOLOGY_SERVICE_UNAVAILABLE.getCode()))
            .verify();
    }
}
