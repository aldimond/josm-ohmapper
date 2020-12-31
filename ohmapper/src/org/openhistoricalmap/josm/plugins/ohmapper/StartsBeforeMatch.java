package org.openhistoricalmap.josm.plugins.ohmapper;

import org.openstreetmap.josm.data.osm.Tagged;
import org.openstreetmap.josm.data.osm.search.SearchCompiler.TaggedMatch;

/**
 * Matches features that definitely start before the search range.
 */
class StartsBeforeMatch extends TaggedMatch {

    private TimeRange searchRange;

    StartsBeforeMatch(TimeRange searchRange) {
        super();
    
        this.searchRange = searchRange;
    }

    @Override
    public boolean match(Tagged tags) {
        if (tags.hasKey("start_date")) {
            try {
                TimeRange startRange = TimeRange.parse(tags.get("start_date"));
                return startRange.isBefore(searchRange);
            } catch (Exception e) {
                // Couldn't parse; we don't know if it starts before...
                return false;
            }
        }

        // The feature appears to be timeless.
        return true;
    }
}
