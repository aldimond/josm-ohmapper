package org.openhistoricalmap.josm.plugins.ohmapper;

import org.openstreetmap.josm.data.osm.Tagged;
import org.openstreetmap.josm.data.osm.search.SearchCompiler.TaggedMatch;

/**
 * Matches features that definitely exist during the search range.
 */
class ExistsDuringMatch extends TaggedMatch {

    private TimeRange searchRange;

    ExistsDuringMatch(TimeRange searchRange) {
        super();
    
        this.searchRange = searchRange;
    }

    @Override
    public boolean match(Tagged tags) {
        TimeRange existenceRange = TimeRange.innerRange(tags);
        return existenceRange.overlaps(searchRange);
    }
}
