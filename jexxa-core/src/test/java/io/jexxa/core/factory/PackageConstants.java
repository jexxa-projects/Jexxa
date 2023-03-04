package io.jexxa.core.factory;

import io.jexxa.testapplication.JexxaTestApplication;

class PackageConstants {

    public static final String JEXXA_APPLICATION_SERVICE = JexxaTestApplication.class.getPackageName() + ".applicationservice";
    public static final String JEXXA_DOMAIN_SERVICE =  JexxaTestApplication.class.getPackageName() + ".domainservice";
    public static final String JEXXA_DRIVEN_ADAPTER = JexxaTestApplication.class.getPackageName() + ".infrastructure.drivenadapter";
    public static final String JEXXA_DRIVING_ADAPTER = JexxaTestApplication.class.getPackageName() + ".infrastructure.drivingadapter";
}
