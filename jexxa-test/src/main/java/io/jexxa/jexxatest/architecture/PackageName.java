package io.jexxa.jexxatest.architecture;

/**
 * This class defines the package names to validate the onion architecture.
 *
 * In case you use another package structure for your application, please adjust these packages accordingly.
 */
class PackageName
{
    public static final String APPLICATIONSERVICE = "..applicationservice";
    public static final String DOMAIN_PROCESS_SERVICE = "..domainprocessservice";
    public static final String DOMAIN_SERVICE = "..domainservice";
    public static final String AGGREGATE = "..domain.aggregate";
    public static final String BUSINESS_EXCEPTION = "..domain.businessexception";
    public static final String DOMAIN_EVENT = "..domain.domainevent";
    public static final String VALUE_OBJECT = "..domain.valueobject";
    public static final String INFRASTRUCTURE = "..infrastructure..";
    public static final String DRIVEN_ADAPTER = "..drivenadapter..";
    public static final String DRIVING_ADAPTER = "..drivingadapter..";

    private PackageName()
    {
        //Private Constructor
    }
}
