package io.ddd.jexxa.dummyapplication.applicationservice;

public class ApplicationWrapper
{
    private final ApplicationServiceWithDrivenApdapters applicationServiceWithDrivenApdapters;

    public ApplicationWrapper(ApplicationServiceWithDrivenApdapters applicationServiceWithDrivenApdapters)
    {
        this.applicationServiceWithDrivenApdapters = applicationServiceWithDrivenApdapters;
    }

    public ApplicationServiceWithDrivenApdapters getPort()
    {
        return applicationServiceWithDrivenApdapters;
    }
}
