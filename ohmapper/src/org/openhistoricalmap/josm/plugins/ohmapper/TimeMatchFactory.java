package org.openhistoricalmap.josm.plugins.ohmapper;

import static java.util.stream.Collectors.toMap;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import org.openstreetmap.josm.data.osm.search.PushbackTokenizer;
import org.openstreetmap.josm.data.osm.search.SearchCompiler;
import org.openstreetmap.josm.data.osm.search.SearchCompiler.SimpleMatchFactory;
import org.openstreetmap.josm.data.osm.search.SearchCompiler.TaggedMatch;
import org.openstreetmap.josm.data.osm.search.SearchParseError;

public class TimeMatchFactory implements SimpleMatchFactory {

    private static SimpleEntry<String, Function<TimeRange, TaggedMatch>> entry(
            String name, Function<TimeRange, TaggedMatch> maker) {
        return new SimpleEntry<>(name, maker);
    }

    private static Map<String, Function<TimeRange, TaggedMatch>> matchMakers = Stream.of(
            entry("startsbefore", r -> new StartsBeforeMatch(r)),
            entry("startsafter", r -> new StartsAfterMatch(r)),
            entry("endsbefore", r -> new EndsBeforeMatch(r)),
            entry("endsafter", r -> new EndsAfterMatch(r)),
            entry("existsduring", r -> new ExistsDuringMatch(r)),
            entry("mightexistduring", r -> new MightExistDuringMatch(r)))
        .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));

    @Override
    public Collection<String> getKeywords() {
        return matchMakers.keySet();
    }

    @Override
    public SearchCompiler.Match get(
            String keyword,
            boolean caseSensitive,
            boolean regexSearch,
            PushbackTokenizer tokenizer)
        throws SearchParseError {
        Function<TimeRange, TaggedMatch> matchMaker = matchMakers.get(keyword);
        if (matchMaker == null) {
            throw new IllegalStateException("Not expecting keyword " + keyword);
        }
        if (tokenizer == null) {
            throw new SearchParseError(
                    "<html>"
                    + tr("Expecting {0} after {1}", "<code>:</code>", "<i>" + keyword + "</i>")
                    + "</html>");
        }
        TimeRange r = TimeRange.fromSearch(tokenizer);
        return matchMaker.apply(r);
    }
}
