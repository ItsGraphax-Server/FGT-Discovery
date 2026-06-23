package de.itsgraphax.fgtDiscovery.util;

import de.itsgraphax.fgtDiscovery.HasPlugin;
import de.itsgraphax.fgtDiscovery.exceptions.ServerConfigInvalidException;
import de.itsgraphax.fgtDiscovery.exceptions.ServerNotFoundException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public record ServerConnectionData(String ip, Integer port, String name) implements HasPlugin {
    public boolean isEmpty() {
        return (ip == null) || (port == null);
    }

    public static ServerConnectionData fromConfig(String server) throws ServerNotFoundException, ServerConfigInvalidException {
        ConfigurationSection serverConfig = plugin.getConfig().getConfigurationSection(String.format("servers.%s", server));
        if (serverConfig == null) {
            throw new ServerNotFoundException();
        }

        String ip = serverConfig.getString("ip");
        Integer port = serverConfig.getInt("port");
        String name = serverConfig.getString("name", server);
        ServerConnectionData connectionData = new ServerConnectionData(ip, port, name);
        if (connectionData.isEmpty()) {
            throw new ServerConfigInvalidException();
        }
        return connectionData;
    }

    public static ServerConnectionData fromStringWithFeedback(String server, Player player) {
        ServerConnectionData connectionData;
        try {
            connectionData = ServerConnectionData.fromConfig(server);
        } catch (ServerNotFoundException e) {
            player.sendMessage(plugin.richText().fromConfig("server-not-exist"));
            return null;
        } catch (ServerConfigInvalidException e) {
            player.sendMessage(plugin.richText().fromConfig("server-config-incorrect"));
            return null;
        }
        return connectionData;
    }
}
