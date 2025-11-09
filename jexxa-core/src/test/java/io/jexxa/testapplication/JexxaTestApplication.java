package io.jexxa.testapplication;

import io.jexxa.core.JexxaMain;

public final class JexxaTestApplication {
    static void main()
    {
        var jexxaMain = new JexxaMain(JexxaTestApplication.class);

        jexxaMain.run();
    }

    private JexxaTestApplication()
    {
        //Private constructor since we only offer main
    }
}
