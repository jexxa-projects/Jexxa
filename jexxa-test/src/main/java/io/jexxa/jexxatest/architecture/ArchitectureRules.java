package io.jexxa.jexxatest.architecture;

@SuppressWarnings("unused")
public final class ArchitectureRules {
    public static PortsAndAdapters portsAndAdapters(Class<?> project)
    {
        return new PortsAndAdapters(project);
    }

    public static PatternLanguage patternLanguage(Class<?> project)
    {
        return new PatternLanguage(project);
    }

    public static AggregateRules aggregateRules(Class<?> project)
    {
        return new AggregateRules(project);
    }

    private ArchitectureRules()
    {
        //Empty private constructor
    }
}
