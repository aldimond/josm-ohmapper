# josm-ohmapper
OHMapper ([OpenHistoricalMap](http://openhistoricalmap.org) editor) plugin for
JOSM. This is designed to facilitate editing of OpenHistoricalMap data using
JOSM, and isn't going to be very useful for OpenStreetMap. So make sure you're
signed up for OHM and [pointed at OHM
data](https://wiki.openstreetmap.org/wiki/Open_Historical_Map/OHM_Basics#JOSM).

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
 
## How to install pre-built plugin from release

Download "ohmapper.jar" from a release and copy to your JOSM plugins directory.
For a per-user location:
 - Mac: `${HOME}/Library/JOSM/plugins`
 - Unix-ish: `${HOME}/.josm/plugins` seems to get first priority;
 `${HOME}/.local/share/JOSM/plugins` also works (assuming your
 `{$XDG_DATA_HOME}` is normal).
 - See [here](https://josm.openstreetmap.de/wiki/DevelopersGuide/DevelopingPlugins#Testing)
 for dev-focused plugin directory notes.

I've tested on Mac and Linux. If I start doing real UI features I might get my
Windows VM running again. Feel free to submit issues if something is broken or
find me on OpenHistoricalMap slack.

### Verification of release builds

The release JARs are built by me (Al Dimond). For each ".jar" I will provide
SHA sums and GPG signatures. To verify a SHA sum:

1. Download "ohmapper.jar" and "ohmapper.jar.shasum" from the same release to
   the same directory
1. Run `shasum -c ohmapper.jar.shasum`

This verifies integrity of the JAR (as long as you trust me, GitHub, and my
ability to keep my dev machines and GitHub account secure). I'll keep an
independent store of release bits and checksums in case you trust some other
means of contacting me more.

If you want to use GPG verification you can additionally be assured that the
releases are built by someone with access to my keys, and that they aren't
changing too often. You still have to trust GitHub a bit (that's where you'll
download the public keys from initially) -- if you can chase me down by email
or OSM Slack and establish some communication you trust I'll verify key
IDs/signatures that way; by this point you should probably have the code
reviewed in triplicate :-).

1. (First-time only) Install an OpenPGP implementation (I'm using GnuPG at the moment) and `curl`.
1. (Needed whenever I update my keys, hopefully not that often) Download and
   install the public keys associated with my GitHub account. With GnuPG that's
   `curl https://github.com/aldimond.gpg | gpg --install`.
   - GPG will not consider these keys trusted, only (hopefully) valid, and
     every time you use them to verify a release it will warn you that there
     is, "No indication the signature belongs to the owner." That's true as
     far as as a PGP system can know! You additionally know that someone that
     controls this GitHub account put them there, for what it's worth (maybe
     not much).
   - You shouldn't confer trust onto these keys (by signing them with your
     keys) except within a keyring where "trust" means something limited like,
     "These keys are associated with some rando's GitHub account."
1. Download "ohmapper.jar.asc" and "ohmapper.jar" from the same release.
1. Run `gpg --verify ohmapper.jar.asc ohmapper.jar`.

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
