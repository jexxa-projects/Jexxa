package io.jexxa.jexxatest.architecture;


import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.Repository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

/**
 * These tests validate the access direction af an onion architecture which is as follows:
 *
 * @startuml
 *
 * package "DrivingAdapter " #DDDDDD {
 *    [DrivingAdapter] #Implementation
 *  }
 * package ApplicationCore  #DDDDDD {
 *   [ApplicationService] #lightgreen
 *   [DomainService]      #lightgreen
 *   [Domain]             #lightgreen
 * }
 * package "DrivenAdapter "  #DDDDDD {
 *     [DrivenAdapter] #Implementation
 *  }
 * [DrivingAdapter] -r--> [ApplicationService] : uses
 * [DrivingAdapter] -r--> [DomainService] : uses
 * [ApplicationService] -down-> [DomainService] : uses
 * [ApplicationService] -down-> [Domain] : uses
 * [DomainService] -r-> [Domain] : uses
 * [DrivenAdapter] .u..> [Domain]
 * [DrivenAdapter] .u..> [DomainService] : implements
 *
 * @enduml
 * ....
 */
public class PortsAndAdaptersArchitecture extends ArchitectureRule {

    private final Architectures.OnionArchitecture onionArchitecture;
    @SuppressWarnings("unused")
    public PortsAndAdaptersArchitecture(Class<?> project)
    {
        this(project, ImportOption.Predefined.DO_NOT_INCLUDE_TESTS);
    }

    protected PortsAndAdaptersArchitecture(Class<?> project, ImportOption importOption)
    {
        super(project, importOption);
        this.onionArchitecture = onionArchitecture()
                .domainModels(project().getPackage().getName() + ".domain..")
                .domainServices(project().getPackage().getName() +".domainservice..")
                .applicationServices(project().getPackage().getName() + ".applicationservice..");

        this.onionArchitecture.allowEmptyShould(true);
    }

    public PortsAndAdaptersArchitecture addDrivenAdapterPackage(String drivenAdapterPackage)
    {
        onionArchitecture.adapter(drivenAdapterPackage, project().getPackageName() + "." + "infrastructure.drivenadapter." + drivenAdapterPackage + "..");
        return this;
    }

    public PortsAndAdaptersArchitecture addDrivingAdapterPackage(String drivingAdapterPackage)
    {
        onionArchitecture.adapter(drivingAdapterPackage, project().getPackageName() + ".infrastructure.drivingadapter." + drivingAdapterPackage + "..");
        return this;
    }

    public void validate()
    {
        onionArchitecture.check(importedClasses());

        //Don't access a repository or aggregate directly from a driving adapter
        var drivingAdapter = noClasses().that()
                .resideInAnyPackage(project().getPackageName() + ".infrastructure.drivingadapter..").should()
                .dependOnClassesThat().areAnnotatedWith(Aggregate.class).orShould()
                .dependOnClassesThat().areAnnotatedWith(Repository.class);
        drivingAdapter.check(importedClasses());
    }


}