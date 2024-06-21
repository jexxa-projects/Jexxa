package io.jexxa.testapplication.domain.model;

public record JexxaRecordComparable(int number) implements Comparable<JexxaRecordComparable> {
    @Override
    public int compareTo(JexxaRecordComparable o) {
        return Integer.compare(number, o.number);
    }
}
