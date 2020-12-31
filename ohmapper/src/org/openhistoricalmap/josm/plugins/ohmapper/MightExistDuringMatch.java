package org.openhistoricalmap.josm.plugins.ohmapper;

import org.openstreetmap.josm.data.osm.Tagged;
import org.openstreetmap.josm.data.osm.search.SearchCompiler.TaggedMatch;

/**
 * Matches features that might exist during the search range.
 */
class MightExistDuringMatch extends TaggedMatch {

    private TimeRange searchRange;

    MightExistDuringMatch(TimeRange searchRange) {
        super();
    
        this.searchRange = searchRange;
    }

    @Override
    public boolean match(Tagged tags) {
        TimeRange existenceRange = TimeRange.outerRange(tags);
        return existenceRange.overlaps(searchRange);
    }
}
