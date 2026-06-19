package de.itsgraphax.fgtDiscovery.commands;

import de.itsgraphax.fgtDiscovery.HasPlugin;
import de.itsgraphax.fgtDiscovery.serverInfo.PlayerSample;
import de.itsgraphax.fgtDiscovery.serverInfo.ServerInfo;
import de.itsgraphax.fgtDiscovery.util.ServerConnectionData;
import net.strokkur.commands.Command;
import net.strokkur.commands.Executes;
import net.strokkur.commands.paper.Description;
import net.strokkur.commands.paper.Executor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

@Command("playerlist")
@Description("Get the list of online players on a server")
public class PlayerList implements HasPlugin {
    @Executes()
    void playerList(@Executor Player player, @Join.JoinServerSuggestions String server) {
        LocalDateTime lastExecute = plugin.pdcData().lastPlayerlistUse(player);
        if (Duration.between(lastExecute, LocalDateTime.now()).toSeconds() < 3) {
            player.sendMessage(rt.fromConfig("playerlist.cooldown"));
            return;
        }
        plugin.pdcData().lastPlayerlistUse(player, LocalDateTime.now());

        plugin.getServer().getAsyncScheduler().runNow(plugin, _ -> playerListTask(player, server));
    }

    private void playerListTask(@Executor Player player, String server) {
        ServerConnectionData connectionData = ServerConnectionData.fromStringWithFeedback(server, player);
        if (connectionData == null || connectionData.isEmpty()) return;

        ServerInfo serverInfo;
        try {
            serverInfo = ServerInfo.request(connectionData);
        } catch (Exception e) {
            player.sendMessage(rt.fromConfig("playerlist.no-respond"));
            plugin.getComponentLogger().warn("error requesting server status: {}", String.valueOf(e));
            return;

        }

        player.sendMessage(rt.fromConfig("playerlist.list",
                "SERVER", connectionData.name(),
                "ONLINE_PLAYERS_COUNT", serverInfo.players().toString(),
                "ONLINE_PLAYERS", Objects.requireNonNullElse(
                            serverInfo.sample(),
                            new ArrayList<PlayerSample>()
                        )
                        .stream()
                        .map(PlayerSample::name)
                        .collect(Collectors.joining("\n    ")

        )));
    }

}
