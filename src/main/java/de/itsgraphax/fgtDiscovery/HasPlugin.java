package de.itsgraphax.fgtDiscovery;

import de.itsgraphax.grphxLib.utils.RichText;

public interface HasPlugin {
    FgtDiscovery plugin = FgtDiscovery.getInstance();
    RichText.RichConfigText rt = plugin.richText();
}
