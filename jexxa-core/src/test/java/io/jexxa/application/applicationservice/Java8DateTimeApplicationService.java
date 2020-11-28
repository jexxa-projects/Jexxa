package io.jexxa.application.applicationservice;


import java.time.LocalDate;
import java.time.LocalDateTime;

import io.jexxa.application.annotation.ApplicationService;

@SuppressWarnings("unused")
@ApplicationService
public class Java8DateTimeApplicationService
{
    private LocalDate localDate = LocalDate.now();
    private LocalDateTime localDateTime = LocalDateTime.now();

    public void setLocalDate(LocalDate localDate)
    {
        this.localDate = localDate;
    }

    public LocalDate getLocalDate()
    {
        return localDate;
    }

    public void setLocalDateTime(LocalDateTime localDateTime)
    {
        this.localDateTime = localDateTime;
    }

    public LocalDateTime getLocalDateTime()
    {
        return localDateTime;
    }

}
