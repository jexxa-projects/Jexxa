package io.jexxa.application.applicationservice;


import java.time.LocalDate;

import io.jexxa.application.annotation.ApplicationService;

@ApplicationService
public class Java8DateTimeApplicationService
{
    private LocalDate localDate = LocalDate.now();

    public void setLocalDate(LocalDate localDate)
    {
        System.out.println(localDate);
        this.localDate = localDate;
    }

    public LocalDate getLocalDate()
    {
        return localDate;
    }

}
