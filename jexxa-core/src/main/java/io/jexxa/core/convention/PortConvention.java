package io.jexxa.core.convention;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class PortConvention
{

    /**
     * Validates if given clazz matches the convention of a port.
     * If one of the conventions is not matched, an exception is thrown:
     * <t>
     *     <li>
     *         Exactly one public constructor.
     *     </li>
     *     <li>
     *         Attributes of the constructor must be interfaces
     *     </li>
     * </t>
     *
     * @param clazz that should be used as a Port
     * @param <T> generic type of the port
     */
    public static <T> void validate(Class<T> clazz)
    {
        validate(clazz, Collections.emptyList());
    }

    public static void validate(Class<?> clazz, List<String> outboundPorts) {
        // 1. Konstruktor-Anzahl per Switch Expression validieren
        var constructor = switch (clazz.getConstructors().length) {
            case 1 -> clazz.getConstructors()[0];
            case 0 -> throw new PortConventionViolation("No public constructor available for Port: " + clazz.getName());
            default -> throw new PortConventionViolation("More than one public constructor available for Port: " + clazz.getName());
        };

        // 2. Erlaubte Packages prüfen
        boolean invalidParameterExists = Arrays.stream(constructor.getParameterTypes())
                .filter(param -> !param.isInterface())
                .map(Class::getPackageName)
                .anyMatch(pkg -> outboundPorts.stream().noneMatch(allowed -> pkg.equals(allowed) || pkg.startsWith(allowed + ".")));

        if (invalidParameterExists) {
            throw new PortConventionViolation(
                    String.format("Non-interface parameter in constructor of Port '%s' is not part of the allowed outbound ports packages: %s",
                            clazz.getName(), outboundPorts)
            );
        }
    }


    private PortConvention()
    {
        //Private Constructor
    }

}
