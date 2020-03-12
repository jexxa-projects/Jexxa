package io.ddd.jhexa.infrastructure;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;

import java.util.List;

import javax.sound.sampled.Port;

import io.ddd.stereotype.applicationcore.ApplicationService;
import org.junit.Test;

public class PortScannerTest
{
    @Test
    public void findApplicationService() {
        PortScanner portScanner = new PortScanner("io.ddd.jhexa.applicationcore");

        List<Class<?>> applicationServiceList = portScanner.findAnnotation(ApplicationService.class);

        assertFalse(applicationServiceList.isEmpty());
        assertTrue(applicationServiceList.size() == 1);
    }
}
