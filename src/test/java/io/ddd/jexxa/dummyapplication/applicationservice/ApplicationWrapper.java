package io.ddd.jexxa.dummyapplication.applicationservice;

public class ApplicationWrapper
{
    private final ApplicationServiceWithDrivenAdapters applicationServiceWithDrivenAdapters;

    public ApplicationWrapper(ApplicationServiceWithDrivenAdapters applicationServiceWithDrivenAdapters)
    {
        this.applicationServiceWithDrivenAdapters = applicationServiceWithDrivenAdapters;
    }

    public ApplicationServiceWithDrivenAdapters getPort()
    {
        return applicationServiceWithDrivenAdapters;
    }
}
