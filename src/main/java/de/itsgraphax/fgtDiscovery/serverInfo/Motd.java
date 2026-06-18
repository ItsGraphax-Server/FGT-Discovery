package de.itsgraphax.fgtDiscovery.serverInfo;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.text.Component;

/**
 * corresponds the "description" in the ServerInfo packet
 */
public final class Motd {
    @SerializedName("text")
    private String text;
    @SerializedName("extra")
    private Component component;

    /**
     * @return the text of the motd, if it isn't a {@link Component}
     * @see Motd#component()
     */
    public String text() {
        return text;
    }

    /**
     * @return the component of the motd, if it isn't only a string
     * @see Motd#text()
     */
    public Component component() {
        return component;
    }
}
