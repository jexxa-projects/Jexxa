package io.jexxa.application.domain.model;

import java.util.Arrays;

@SuppressWarnings("unused")
public record JexxaRecord(String[] jexxaRecord) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JexxaRecord other = (JexxaRecord) o;
        return Arrays.equals(this.jexxaRecord, other.jexxaRecord);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(jexxaRecord);
    }

    @Override
    public String toString() {
        return "JexxaRecord{" +
                "jexxaRecord=" + Arrays.toString(jexxaRecord)+
                '}';
    }
}
