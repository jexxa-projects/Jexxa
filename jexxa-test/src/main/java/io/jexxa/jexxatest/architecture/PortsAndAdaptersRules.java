package io.jexxa.jexxatest.architecture;


import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static io.jexxa.jexxatest.architecture.PackageName.DOMAIN;

/**
 * These tests validate the access direction af an onion architecture which is as follows:
 *
 * @startuml
 *
 * package ApplicationCore  #DDDDDD {
 *   [ApplicationService]
 *   [DomainService]
 *   [Domain]
 * }
 *
 * [ApplicationService] -down-> [DomainService]
 * [ApplicationService] -down-> [Domain]
 * [DomainService] -r-> [Domain]
 *
 * @enduml
 * ....
 */
public class PortsAndAdaptersRules extends ArchitectureRule {

    private final Architectures.OnionArchitecture onionArchitecture;
    @SuppressWarnings("unused")
    public PortsAndAdaptersRules(Class<?> project)
    {
        this(project, ImportOption.Predefined.DO_NOT_INCLUDE_TESTS);
    }

    protected PortsAndAdaptersRules(Class<?> project, ImportOption importOption)
    {
        super(project, importOption);
        this.onionArchitecture = onionArchitecture()
                .domainModels(project().getPackage().getName() + ".domain..")
                .domainServices(project().getPackage().getName() +".domainservice..")
                .applicationServices(project().getPackage().getName() + ".applicationservice..")
                .adapter("main", project.getPackage().getName());
        onionArchitecture.allowEmptyShould(true);
    }

    public PortsAndAdaptersRules addDrivenAdapterPackage(String drivenAdapterPackage)
    {
        onionArchitecture.adapter(drivenAdapterPackage, project().getPackageName() + "." + "infrastructure.drivenadapter." + drivenAdapterPackage + "..");
        return this;
    }

    public PortsAndAdaptersRules addDrivingAdapterPackage(String drivingAdapterPackage)
    {
        onionArchitecture.adapter(drivingAdapterPackage, project().getPackageName() + ".infrastructure.drivingadapter." + drivingAdapterPackage + "..");
        return this;
    }

    public void validate()
    {
        onionArchitecture.check(importedClasses());
        var drivingAdapter = noClasses().that().resideInAnyPackage(project().getPackageName() + ".infrastructure.drivingadapter..").should().dependOnClassesThat().resideInAnyPackage(DOMAIN).allowEmptyShould(true);
        drivingAdapter.check(importedClasses());

    }


}