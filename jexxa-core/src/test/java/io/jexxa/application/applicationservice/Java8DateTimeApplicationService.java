package io.jexxa.application.applicationservice;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;

import io.jexxa.application.annotation.ApplicationService;

@SuppressWarnings("unused")
@ApplicationService
public class Java8DateTimeApplicationService
{
    private LocalTime localTime = LocalTime.now();
    private LocalDate localDate = LocalDate.now();
    private LocalDateTime localDateTime = LocalDateTime.now();
    private ZonedDateTime zonedDateTime = ZonedDateTime.now();
    private Period period = Period.of(1, 0, 0);
    private Duration duration = Duration.ofDays(1);
    private Instant instant = Instant.now();

    // -- LocalDate
    public void setLocalDate(LocalDate localDate)
    {
        this.localDate = localDate;
    }

    public LocalDate getLocalDate()
    {
        return localDate;
    }

    // -- LocalDateTime
    public void setLocalDateTime(LocalDateTime localDateTime)
    {
        this.localDateTime = localDateTime;
    }

    public LocalDateTime getLocalDateTime()
    {
        return localDateTime;
    }

    // -- LocalTime
    public void setLocalTime(LocalTime localTime)
    {
        this.localTime = localTime;
    }

    public LocalTime getLocalTime()
    {
        return localTime;
    }

    // -- ZoneDateTime
    public void setZonedDateTime(ZonedDateTime zonedDateTime)
    {
        this.zonedDateTime = zonedDateTime;
    }

    public ZonedDateTime getZonedDateTime()
    {
        return zonedDateTime;
    }

    // -- Period
    public void setPeriod(Period period)
    {
        this.period = period;
    }

    public Period getPeriod()
    {
        return period;
    }

    // -- Period
    public void setDuration(Duration duration)
    {
        this.duration = duration;
    }

    public Duration getDuration()
    {
        return duration;
    }

    // -- Period
    public void setInstant(Instant instant)
    {
        this.instant = instant;
    }

    public Instant getInstant()
    {
        return instant;
    }

}
