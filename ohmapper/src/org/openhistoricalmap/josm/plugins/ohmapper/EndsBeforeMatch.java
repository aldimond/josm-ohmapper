package org.openhistoricalmap.josm.plugins.ohmapper;

import org.openstreetmap.josm.data.osm.Tagged;
import org.openstreetmap.josm.data.osm.search.SearchCompiler.TaggedMatch;

/**
 * Matches features that definitely end before the search range.
 */
class EndsBeforeMatch extends TaggedMatch {

    private TimeRange searchRange;

    EndsBeforeMatch(TimeRange searchRange) {
        super();
    
        this.searchRange = searchRange;
    }

    @Override
    public boolean match(Tagged tags) {
        if (tags.hasKey("end_date")) {
            try {
                TimeRange endRange = TimeRange.parse(tags.get("end_date"));
                return endRange.isBefore(searchRange);
            } catch (Exception e) {
                // Couldn't parse; we don't know if it ends before...
                return false;
            }
        }

        // The feature never ends -- before nothing.
        return false;
    }
}
