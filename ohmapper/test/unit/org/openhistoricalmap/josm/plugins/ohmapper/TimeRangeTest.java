package org.openhistoricalmap.josm.plugins.ohmapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class TimeRangeTest {
    @Test
    public void parse_before() {
        TimeRange r = TimeRange.parse(" before    1984-02-24 ");
        assertEquals(LocalDate.MIN, r.getStart());
        assertEquals(LocalDate.of(1984, 2, 24), r.getEnd());
    }

    @Test
    public void parse_after() {
        TimeRange r = TimeRange.parse("after 1900-08");
        assertEquals(LocalDate.of(1900, 9, 1), r.getStart());
        assertEquals(LocalDate.MAX, r.getEnd());
    }

    @Test
    public void parse_doubleDot() {
        TimeRange r = TimeRange.parse(" 2104-02-09 .. 2105-02-04");
        assertEquals(LocalDate.of(2104, 2, 9), r.getStart());
        assertEquals(LocalDate.of(2105, 2, 5), r.getEnd());
    }

    @Test
    public void parse_slash() {
        TimeRange r = TimeRange.parse("1990/2011");
        assertEquals(LocalDate.of(1990, 1, 1), r.getStart());
        assertEquals(LocalDate.of(2012, 1, 1), r.getEnd());
    }

    @Test
    public void parse_singleYear() {
        TimeRange r = TimeRange.parse("1997");
        assertEquals(LocalDate.of(1997, 1, 1), r.getStart());
        assertEquals(LocalDate.of(1998, 1, 1), r.getEnd());
    }

    @Test
    public void parse_singleMonth() {
        TimeRange r = TimeRange.parse("2020-03");
        assertEquals(LocalDate.of(2020, 3, 1), r.getStart());
        assertEquals(LocalDate.of(2020, 4, 1), r.getEnd());
    }

    @Test
    public void parse_singleDay() {
        TimeRange r = TimeRange.parse("1969-07-20");
        assertEquals(LocalDate.of(1969, 7, 20), r.getStart());
        assertEquals(LocalDate.of(1969, 7, 21), r.getEnd());
    }

    @Test
    public void parse_singleDateTime() {
        TimeRange r = TimeRange.parse("2021-01-22T01:00:05");
        // Time is ignored.
        assertEquals(LocalDate.of(2021, 01, 22), r.getStart());
        assertEquals(LocalDate.of(2021, 01, 23), r.getEnd());
    }

    @Test
    public void parse_dateTimeWithOffset() {
        TimeRange r = TimeRange.parse("2020-03-03T01:01:01+03:30");
        // Time, offset ignored.
        assertEquals(LocalDate.of(2020, 3, 3), r.getStart());
        assertEquals(LocalDate.of(2020, 3, 4), r.getEnd());
    }

    @Test
    public void parse_dateTimeWithZ() {
        TimeRange r = TimeRange.parse("2020-06-01T02:07:22Z");
        // Time, offset ignored.
        assertEquals(LocalDate.of(2020, 6, 1), r.getStart());
        assertEquals(LocalDate.of(2020, 6, 2), r.getEnd());
    }

    @Test
    public void fullyBefore() {
        assertBefore(TimeRange.parse("2010"), TimeRange.parse("2020"));
    }

    @Test
    public void fullyBefore_edge() {
        assertBefore(TimeRange.parse("2019-12"), TimeRange.parse("2020"));
    }

    @Test
    public void leftSide() {
        assertOverlaps(TimeRange.parse("2019-12..2020-01"), TimeRange.parse("2020"));
    }

    @Test
    public void contains() {
        assertOverlaps(TimeRange.parse("2020"), TimeRange.parse("2020-05"));
    }

    @Test
    public void containedBy() {
        assertOverlaps(TimeRange.parse("2020-02"), TimeRange.parse("2019-05..2020-05"));
    }

    @Test
    public void rightSide() {
        assertOverlaps(TimeRange.parse("2020-11..2022"), TimeRange.parse("2020"));
    }

    @Test
    public void fullyAfter_edge() {
        assertAfter(TimeRange.parse("2021"), TimeRange.parse("2020-12"));
    }

    @Test
    public void fullyAfter() {
        assertAfter(TimeRange.parse("2100..2200"), TimeRange.parse("1900..2000"));
    }

    private void assertBefore(TimeRange r1, TimeRange r2) {
        assertTrue(r1.isBefore(r2));
        assertFalse(r1.isAfter(r2));
        assertFalse(r1.overlaps(r2));
    }

    private void assertAfter(TimeRange r1, TimeRange r2) {
        assertTrue(r1.isAfter(r2));
        assertFalse(r1.isBefore(r2));
        assertFalse(r1.overlaps(r2));
    }

    private void assertOverlaps(TimeRange r1, TimeRange r2) {
        assertTrue(r1.overlaps(r2));
        assertFalse(r1.isBefore(r2));
        assertFalse(r1.isAfter(r2));
    }
}
