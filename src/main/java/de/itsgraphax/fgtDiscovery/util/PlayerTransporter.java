package de.itsgraphax.fgtDiscovery.util;

import de.itsgraphax.fgtDiscovery.HasPlugin;
import org.bukkit.entity.Player;

import static de.itsgraphax.fgtDiscovery.commands.SummonPlayerList.updatePlayerListDisplays;

public class PlayerTransporter implements HasPlugin {
    public static void join(Player player, String server) {
        ServerConnectionData connectionData = ServerConnectionData.fromStringWithFeedback(server, player);
        if (connectionData == null) return;

        join(player, connectionData);
    }

    public static void join(Player player, ServerConnectionData connectionData) {
        player.sendMessage(rt.fromConfig("transferring", "SERVER", connectionData.name()));

        player.transfer(connectionData.ip(), connectionData.port());

        plugin.getServer().sendMessage(
                rt.fromConfig("transferred", "PLAYER", player.getName(), "SERVER", connectionData.name())
        );

        plugin.getServer().getScheduler().runTaskLater(plugin, _ -> updatePlayerListDisplays(), 200);
    }

    public static void hub(Player player) {
        join(player, "hub");
    }
}
