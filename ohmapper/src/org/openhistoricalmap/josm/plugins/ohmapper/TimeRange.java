package org.openhistoricalmap.josm.plugins.ohmapper;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.logging.Logger;
import org.openstreetmap.josm.data.osm.Tagged;
import org.openstreetmap.josm.data.osm.search.PushbackTokenizer;
import org.openstreetmap.josm.data.osm.search.SearchParseError;

/**
 * A range of time in ISO-8601 (Gregorian calendar). {@code start} is inclusive, {@code end}
 * exclusive.
 */
class TimeRange {
    private static final TimeRange FIRST_DAY =
        new TimeRange(LocalDate.MIN, LocalDate.MIN.plus(1, ChronoUnit.DAYS));
    private static final TimeRange LAST_DAY =
        new TimeRange(LocalDate.MAX.minus(1, ChronoUnit.DAYS), LocalDate.MAX);
    private static final TimeRange NULL_RANGE = new TimeRange(LocalDate.MIN, LocalDate.MIN);

    /** Parses and returns a value from search. */
    public static TimeRange fromSearch(PushbackTokenizer tokenizer) throws SearchParseError {
        String searchToken = tokenizer.readTextOrNumber();
        if (searchToken == null) {
            throw new SearchParseError(
                    "<html>"
                    + tr("Expecting a date")
                    + "</html>");
        }

        try {
            return TimeRange.parse(searchToken);
        } catch (Exception e) {
            throw new SearchParseError(
                    "<html>"
                    + tr("Could not parse {0} into a date or range", searchToken)
                    + "</html>", e);
        }
    }

    /**
     * Parses and returns a value from a string.
     *
     * TODO: handle other tag/range formats?
     */
    public static TimeRange parse(String rangeStr) {
        rangeStr = rangeStr.trim().toLowerCase(Locale.ENGLISH);

        // "before" ranges end (exclusively) at start of given unit.
        if (rangeStr.startsWith("before")) {
            return new TimeRange(
                    LocalDate.MIN, TimeRange.parseStart(rangeStr.substring("before".length())));
        }

        // "after" ranges start after end of given unit.
        if (rangeStr.startsWith("after")) {
            return new TimeRange(
                    TimeRange.parseEnd(rangeStr.substring("after".length())), LocalDate.MAX);
        }

        // Look for a ".." separator (optionally separates start and end of range)
        int doubleDotIndex = rangeStr.indexOf("..");
        if (doubleDotIndex >= 0) {
            return new TimeRange(
                    TimeRange.parseStart(rangeStr.substring(0, doubleDotIndex)),
                    TimeRange.parseEnd(rangeStr.substring(doubleDotIndex + "..".length())));
        }

        // Look for a "/" separator (EDTF equivalent for "..")
        int slashIndex = rangeStr.indexOf("/");
        if (slashIndex >= 0) {
            return new TimeRange(
                    TimeRange.parseStart(rangeStr.substring(0, slashIndex)),
                    TimeRange.parseEnd(rangeStr.substring(slashIndex + "/".length())));
        }

        // Otherwise this should be a single unit.
        return new TimeRange(TimeRange.parseStart(rangeStr), TimeRange.parseEnd(rangeStr));
    }

    /** Returns the range during which a tagged feature definitely exists. */
    public static TimeRange innerRange(Tagged tags) {
        TimeRange startRange = FIRST_DAY;
        if (tags.hasKey("start_date")) {
            try {
                startRange = parse(tags.get("start_date"));
            } catch (Exception e) {
                // Could be anything; for inner range let's quit now.
                return NULL_RANGE;
            }
        }
        TimeRange endRange = LAST_DAY;
        if (tags.hasKey("end_date")) {
            try {
                endRange = parse(tags.get("end_date"));
            } catch (Exception e) {
                // Could be anything; for inner range let's quit now.
                return NULL_RANGE;
            }
        }

        try {
            return new TimeRange(
                    startRange.getEnd().minus(1, ChronoUnit.DAYS),
                    endRange.getStart().plus(1, ChronoUnit.DAYS));
        } catch (Exception e) {
            return NULL_RANGE;
        }
    }

    /** Returns the range during which a tagged feature might exist. */
    public static TimeRange outerRange(Tagged tags) {
        TimeRange startRange = FIRST_DAY;
        if (tags.hasKey("start_date")) {
            try {
                startRange = parse(tags.get("start_date"));
            } catch (Exception e) {
                // Could be anything; for outer range we'll keep FIRST_DAY
            }
        }
        TimeRange endRange = LAST_DAY;
        if (tags.hasKey("end_date")) {
            try {
                endRange = parse(tags.get("end_date"));
            } catch (Exception e) {
                // Could be anything; for outer range we'll keep LAST_DAY
            }
        }

        try {
            return new TimeRange(startRange.getStart(), endRange.getEnd());
        } catch (Exception e) {
            return NULL_RANGE;
        }
    }

    public boolean overlaps(TimeRange that) {
        return this.end.compareTo(that.start) > 0 && this.start.compareTo(that.end) < 0;
    }

    public boolean isBefore(TimeRange that) {
        return this.end.compareTo(that.start) <= 0;
    }

    public boolean isAfter(TimeRange that) {
        return this.start.compareTo(that.end) >= 0;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    /**
     * Returns start of unit represented by {@code dateStr}.
     *
     * TODO: handle other date formats?
     */
    private static LocalDate parseStart(String dateStr) {
        return TimeRange.parseDate(dateStr).date;
    }

    /**
     * Returns (exclusive) end of unit represented by {@code dateStr}.
     *
     * TODO: handle other date formats?
     */
    private static LocalDate parseEnd(String dateStr) {
        ParsedDate parsed = TimeRange.parseDate(dateStr);
        return parsed.date.plus(1, parsed.unit);
    }

    private static class ParsedDate {
        final ChronoUnit unit;
        final LocalDate date;

        ParsedDate(ChronoUnit unit, LocalDate date) {
            this.unit = unit;
            this.date = date;
        }
    }

    private static ParsedDate parseDate(String dateStr) {
        dateStr = dateStr.trim();

        // Look for "-" separators
        String[] parts = dateStr.split("-", 3);
        switch (parts.length) {
            case 1:
                return new ParsedDate(
                        ChronoUnit.YEARS, LocalDate.of(Integer.parseUnsignedInt(parts[0]), 1, 1));
            case 2:
                return new ParsedDate(
                        ChronoUnit.MONTHS, LocalDate.of(
                            Integer.parseUnsignedInt(parts[0]), Integer.parseUnsignedInt(parts[1]), 1));
            case 3:
                return new ParsedDate(
                        ChronoUnit.DAYS, LocalDate.of(
                            Integer.parseUnsignedInt(parts[0]),
                            Integer.parseUnsignedInt(parts[1]),
                            Integer.parseUnsignedInt(parts[2])));
            default:
                throw new IllegalArgumentException("Expected 1, 2, or 3 parts to a date.");
        }
    }

    private final LocalDate start;
    private final LocalDate end;

    private TimeRange(LocalDate start, LocalDate end) {
        if (end.compareTo(start) < 0) {
            throw new IllegalArgumentException("end < start");
        }
        this.start = start;
        this.end = end;
    }
}
