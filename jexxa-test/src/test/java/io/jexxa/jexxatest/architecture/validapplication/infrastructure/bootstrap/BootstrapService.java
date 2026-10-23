package io.jexxa.jexxatest.architecture.validapplication.infrastructure.bootstrap;

import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidRepository;

import java.util.Objects;

@SuppressWarnings("unused")
public class BootstrapService
{
    public BootstrapService(ValidRepository validRepository)
    {
        Objects.requireNonNull(validRepository);
    }
}
