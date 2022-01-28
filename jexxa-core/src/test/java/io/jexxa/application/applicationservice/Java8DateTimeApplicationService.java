package io.jexxa.application.applicationservice;


import io.jexxa.application.annotation.ApplicationService;

import java.time.*;
import java.util.Objects;

@SuppressWarnings("unused")
@ApplicationService
public class Java8DateTimeApplicationService
{
    private Java8DateTimeWrapper java8DateTimeWrapper= new Java8DateTimeWrapper( LocalTime.now()
            , LocalDate.now()
            , LocalDateTime.now()
            , ZonedDateTime.now().withFixedOffsetZone()
            , Period.of(1, 0, 0)
            , Duration.ofDays(1)
            , Instant.now()
    );

    // -- LocalDate
    public void setLocalDate(LocalDate localDate)
    {
        java8DateTimeWrapper.localDate(localDate);
    }

    public LocalDate getLocalDate()
    {
        return java8DateTimeWrapper.getLocalDate();
    }

    // -- LocalDateTime
    public void setLocalDateTime(LocalDateTime localDateTime)
    {
        this.java8DateTimeWrapper.localDateTime(localDateTime);
    }

    public LocalDateTime getLocalDateTime()
    {
        return java8DateTimeWrapper.getLocalDateTime();
    }

    // -- LocalTime
    public void setLocalTime(LocalTime localTime)
    {
        this.java8DateTimeWrapper.localTime(localTime);
    }

    public LocalTime getLocalTime()
    {
        return java8DateTimeWrapper.getLocalTime();
    }

    // -- ZoneDateTime
    public void setZonedDateTime(ZonedDateTime zonedDateTime)
    {
        java8DateTimeWrapper.zonedDateTime(zonedDateTime);
    }

    public ZonedDateTime getZonedDateTime()
    {
        return java8DateTimeWrapper.getZonedDateTime();
    }

    // -- Period
    public void setPeriod(Period period)
    {
        this.java8DateTimeWrapper.period(period);
    }

    public Period getPeriod()
    {
        return java8DateTimeWrapper.getPeriod();
    }

    // -- Period
    public void setDuration(Duration duration)
    {
        this.java8DateTimeWrapper.duration(duration);
    }

    public Duration getDuration()
    {
        return java8DateTimeWrapper.getDuration();
    }

    // -- Period
    public void setInstant(Instant instant)
    {
        this.java8DateTimeWrapper.instant(instant);
    }

    public Instant getInstant()
    {
        return java8DateTimeWrapper.getInstant();
    }

    public void setJava8DateTimeWrapper(Java8DateTimeWrapper java8DateTimeWrapper)
    {
        this.java8DateTimeWrapper = java8DateTimeWrapper;
    }
    
    public Java8DateTimeWrapper getJava8DateTimeWrapper() 
    {
        return java8DateTimeWrapper;
    }

    public static class Java8DateTimeWrapper
    {
        private LocalTime localTime;
        private LocalDate localDate;
        private LocalDateTime localDateTime;
        private ZonedDateTime zonedDateTime;
        private Period period;
        private Duration duration;
        private Instant instant;

        public Java8DateTimeWrapper(LocalTime localTime
                , LocalDate localDate
                , LocalDateTime localDateTime
                , ZonedDateTime zonedDateTime
                , Period period
                , Duration duration
                , Instant instant)
        {
            this.localTime  = localTime;
            this.localDate = localDate;
            this.localDateTime = localDateTime;
            this.zonedDateTime = zonedDateTime;
            this.period = period;
            this.duration = duration;
            this.instant = instant;
        }
        public LocalTime getLocalTime()
        {
            return localTime;
        }

        public LocalDate getLocalDate()
        {
            return localDate;
        }

        public LocalDateTime getLocalDateTime()
        {
            return localDateTime;
        }

        public ZonedDateTime getZonedDateTime()
        {
            return zonedDateTime;
        }

        public Period getPeriod()
        {
            return period;
        }

        public Duration getDuration()
        {
            return duration;
        }

        public Instant getInstant()
        {
            return instant;
        }

        public void localTime(LocalTime localTime)
        {
            this.localTime = localTime;
        }

        public void localDate(LocalDate localDate)
        {
            this.localDate = localDate;
        }

        public void localDateTime(LocalDateTime localDateTime)
        {
            this.localDateTime = localDateTime;
        }

        public void zonedDateTime(ZonedDateTime zonedDateTime)
        {
            this.zonedDateTime = zonedDateTime;
        }

        public void period(Period period)
        {
            this.period = period;
        }

        public void duration(Duration duration)
        {
            this.duration = duration;
        }

        public void instant(Instant instant)
        {
            this.instant = instant;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            Java8DateTimeWrapper that = (Java8DateTimeWrapper) o;
            return localTime.equals(that.localTime) && localDate.equals(that.localDate) && localDateTime.equals(
                    that.localDateTime) && zonedDateTime.equals(
                    that.zonedDateTime) && period.equals(that.period) && duration.equals(that.duration) && instant.equals(that.instant);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(localTime, localDate, localDateTime, zonedDateTime, period, duration, instant);
        }
    }
}
