package com.onclass.ms_capability.infrastructure.configuration;

import com.onclass.ms_capability.application.usecase.AssociateBootcampCapabilitiesUseCase;
import com.onclass.ms_capability.application.usecase.ListCapabilitiesByBootcampUseCase;
import com.onclass.ms_capability.application.usecase.ListCapabilitiesUseCase;
import com.onclass.ms_capability.application.usecase.RegisterCapabilityUseCase;
import com.onclass.ms_capability.domain.repository.CapabilityRepository;
import com.onclass.ms_capability.domain.repository.TechnologyRepository;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RegisterCapabilityUseCase registerCapabilityUseCase(
        CapabilityRepository capabilityRepository,
        TechnologyRepository technologyRepository
    ) {
        return new RegisterCapabilityUseCase(capabilityRepository, technologyRepository);
    }

    @Bean
    public ListCapabilitiesUseCase listCapabilitiesUseCase(
        CapabilityRepository capabilityRepository,
        TechnologyRepository technologyRepository
    ) {
        return new ListCapabilitiesUseCase(capabilityRepository, technologyRepository);
    }

    @Bean
    public AssociateBootcampCapabilitiesUseCase associateBootcampCapabilitiesUseCase(
        CapabilityRepository capabilityRepository
    ) {
        return new AssociateBootcampCapabilitiesUseCase(
            capabilityRepository
        );
    }

    @Bean
    public ListCapabilitiesByBootcampUseCase listCapabilitiesByBootcampUseCase(
        CapabilityRepository capabilityRepository,
        TechnologyRepository technologyRepository
    ) {
        return new ListCapabilitiesByBootcampUseCase(
            capabilityRepository,
            technologyRepository
        );
    }

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }
}
