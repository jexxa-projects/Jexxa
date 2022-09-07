package io.jexxa.application;

import io.jexxa.core.JexxaMain;

public final class JexxaTestApplication {
    public static void main(String[] args)
    {
        var jexxaMain = new JexxaMain(JexxaTestApplication.class);

        jexxaMain.run();
    }

    private JexxaTestApplication()
    {
        //Private constructor since we only offer main
    }
}
