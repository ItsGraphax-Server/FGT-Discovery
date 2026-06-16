package de.itsgraphax.fgtDiscovery.util;

import de.itsgraphax.fgtDiscovery.FgtDiscovery;
import de.itsgraphax.grphxLib.utils.NamespacesBase;
import org.bukkit.NamespacedKey;

public class Namespaces extends NamespacesBase {
    public Namespaces() {
        super(FgtDiscovery.getInstance());
    }

    public NamespacedKey customServerIp(int slot) {
        return key(String.format("customServer.%s.ip", slot));
    }

    public NamespacedKey customServerPort(int slot) {
        return key(String.format("customServer.%s.port", slot));
    }

    public NamespacedKey lastPlayerlistUse() {
        return key("lastPlayerlistUse");
    }
}
