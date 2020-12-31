package org.openhistoricalmap.josm.plugins.ohmapper;

import org.openstreetmap.josm.data.osm.Tagged;
import org.openstreetmap.josm.data.osm.search.SearchCompiler.TaggedMatch;

/**
 * Matches features that definitely end after the search range.
 */
class EndsAfterMatch extends TaggedMatch {

    private TimeRange searchRange;

    EndsAfterMatch(TimeRange searchRange) {
        super();
    
        this.searchRange = searchRange;
    }

    @Override
    public boolean match(Tagged tags) {
        if (tags.hasKey("end_date")) {
            try {
                TimeRange endRange = TimeRange.parse(tags.get("end_date"));
                return endRange.isAfter(searchRange);
            } catch (Exception e) {
                // Couldn't parse; we don't know if it ends after...
                return false;
            }
        }

        // The feature never ends -- after everything.
        return true;
    }
}
