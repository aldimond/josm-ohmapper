# josm-ohmapper
OHMapper ([OpenHistoricalMap](http://openhistoricalmap.org) editor) plugin for JOSM. This is designed to facilitate editing of OpenHistoricalMap data using JOSM, and isn't going to be very useful for OpenStreetMap. So make sure you're signed up for OHM and [pointed at OHM data](https://wiki.openstreetmap.org/wiki/Open_Historical_Map/OHM_Basics#JOSM).

## Features

Initially this is just a set of search/filter predicates on the
"start\_date" and "end\_date" tags.
 - `startsbefore`
 - `startsafter`
 - `endsbefore`
 - `endsafter`
 - `existsduring`
 - `mightexistduring` (when a range is given for "start\_date" or "end\_date"
   the other predicates interpret the range as narrowly as possible; this
   interprets the range as widely as possible.)

Each takes a single date as its argument, which may be YYYY, YYYY-MM,
YYYY-MM-DD... any of [these
formats](https://wiki.openstreetmap.org/wiki/Open_Historical_Map/OHM_Basics#Start_.26_End_Dates)
essentially. That looks like, for example:
 - "startsbefore:1900"
 - "endsafter:2020"
 - "mightexistduring:1912-06"

This is particularly useful in the "filter" pane. Since JOSM filters are
negative, to see what just what might exist at any point in 1915, use
"-mightexistduring:1915".

## Future features

 - Date filter UI using "autofilters"?
 - More complete support for different date/tagging formats?
 - Making consistency checks time-aware?
 - Y'all probably have better ideas than me!

## How to build

This is a bit rough for now but it's just the initial code dump -- it will get
better soon :-). The basic plot is to check out JOSM source, copy our plugin
into the JOSM plugin directory, then follow [JOSM Plugin
Development](https://josm.openstreetmap.de/wiki/DevelopersGuide/DevelopingPlugins)
instructions.

1. You will need OpenJDK, Ant, and Subversion installed and working (links
   below or, preferably, consult your package manager).
   - [OpenJDK](https://adoptopenjdk.net) ; depending on your system you may need
    to configure your environment for OpenJDK binaries and includes to be seen.
   - [Ant](https://ant.apache.org)
   - [Subversion](https://subversion.apache.org)

1. Check out JOSM source into an empty directory outside this repo called
   "josm". Starting from this directory:
   - `cd ..`
   - `svn co https://josm.openstreetmap.de/osmsvn/applications/editors/josm`

1. Copy the "ohmapper" directory sitting next to this README file into the JOSM
   checkout, under "josm/plugins".
   - Get back to this directory with `cd -`, then
   - `cp -r ohmapper ../josm/plugins/`

1. IIRC you have to build JOSM core first:
   - `cd ../josm`
   - `ant clean dist`

1. Then you can build the plugin:
   - `cd plugins/ohmapper`
   - `ant dist`
   - `cd ../..` (get back to "josm" dir)

1. Copy the built plugin ("dist/ohmapper.jar") to your JOSM plugins
   directory (see
   [here](https://josm.openstreetmap.de/wiki/DevelopersGuide/DevelopingPlugins#Testing)),
   run JOSM, and you'll be able to find the "ohmapper" plugin in the plugin page
   of preferences.
