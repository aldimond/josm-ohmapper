package org.openhistoricalmap.josm.plugins.ohmapper;

import org.openstreetmap.josm.data.osm.search.SearchCompiler;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class OHMapperPlugin extends Plugin {
  public OHMapperPlugin(PluginInformation info) {
    super(info);

    // Register search operators
    SearchCompiler.addMatchFactory(new TimeMatchFactory());
  }
}
