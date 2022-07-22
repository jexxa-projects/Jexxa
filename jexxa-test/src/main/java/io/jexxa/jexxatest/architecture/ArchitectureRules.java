package io.jexxa.jexxatest.architecture;

@SuppressWarnings("unused")
public class ArchitectureRules {
    public static OnionArchitecture onionArchitecture(Class<?> project)
    {
        return new OnionArchitecture(project);
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
