package de.itsgraphax.fgtDiscovery.commands;

import de.itsgraphax.fgtDiscovery.HasPlugin;
import de.itsgraphax.fgtDiscovery.exceptions.ServerConfigInvalidException;
import de.itsgraphax.fgtDiscovery.exceptions.ServerNotFoundException;
import de.itsgraphax.fgtDiscovery.serverInfo.PlayerSample;
import de.itsgraphax.fgtDiscovery.serverInfo.ServerInfo;
import de.itsgraphax.fgtDiscovery.util.ServerConnectionData;
import jdk.jfr.Description;
import net.kyori.adventure.text.Component;
import net.strokkur.commands.Command;
import net.strokkur.commands.Executes;
import net.strokkur.commands.paper.Executor;
import net.strokkur.commands.paper.RequiresOP;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Command("summonplayerlist")
@Description("Operator Commands")
@RequiresOP
public class SummonPlayerList implements HasPlugin {
    @Executes()
    void summonPlayerList(@Executor Player player, @Join.JoinServerSuggestions String server) {
        player.getWorld().spawn(player.getLocation().setRotation(0,0), TextDisplay.class, textDisplay -> {
            textDisplay.text(rt.fromConfig("playerlist.textdisplay.loading"));
            textDisplay.setBillboard(Display.Billboard.CENTER);
            textDisplay.setPersistent(true);
            textDisplay.getPersistentDataContainer().set(plugin.namespaces().playerlistDisplay(), PersistentDataType.STRING, server);
        });
        updatePlayerListDisplays();
    }

    public static void updatePlayerListDisplays() {
        List<Entity> entities = plugin.getServer().getRespawnWorld().getEntities();
        entities.forEach(entity -> {
            if (entity.getPersistentDataContainer().has(plugin.namespaces().playerlistDisplay())) {
                String server = entity.getPersistentDataContainer().get(plugin.namespaces().playerlistDisplay(), PersistentDataType.STRING);
                // make request async
                plugin.getServer().getAsyncScheduler().runNow(plugin, _ -> {
                    ServerConnectionData connectionData;
                    try {
                        connectionData = ServerConnectionData.fromConfig(server);
                    } catch (ServerNotFoundException |
                             ServerConfigInvalidException e) {
                        throw new RuntimeException(e);
                    }

                    ServerInfo serverInfo;
                    try {
                        serverInfo = ServerInfo.request(connectionData);
                    } catch (Exception e) {
                        plugin.getComponentLogger().warn("error requesting server status: {}", String.valueOf(e));
                        plugin.getServer().getScheduler().runTask(plugin, _-> {
                            TextDisplay textDisplay = ((TextDisplay) entity);
                            textDisplay.text(rt.fromConfig("playerlist.no-respond"));
                        });
                        return;
                    }

                    // change text tick-sync
                    plugin.getServer().getScheduler().runTask(plugin, _-> {
                        TextDisplay textDisplay = ((TextDisplay) entity);
                        textDisplay.text(getDisplayComponent(serverInfo));
                    });


                });

            }
        });
    }

    private static Component getDisplayComponent(ServerInfo info) {
        if (info.players() == 0) {
            return rt.fromConfig("playerlist.textdisplay.empty");
        } else if (info.players() <= plugin.getConfig().getInt("playerlist.max-display")) {
            return rt.fromConfig("playerlist.textdisplay.fits",
                    "ONLINE_PLAYERS_COUNT", info.players().toString(),
                    "ONLINE_PLAYERS", getOnlinePlayersString(info)
            );
        } else {
            return rt.fromConfig("playerlist.textdisplay.more",
                    "ONLINE_PLAYERS_COUNT", info.players().toString(),
                    "ONLINE_PLAYERS", getOnlinePlayersString(info)
            );
        }
    }

    private static String getOnlinePlayersString(ServerInfo info) {
        List<PlayerSample> sample = Objects.requireNonNullElse(
                info.sample(),
                new ArrayList<>()
        );
        if (sample.size() > 3) {
            sample = sample.subList(0, plugin.getConfig().getInt("playerlist.max-display"));
        }
        return sample
                .stream()
                .map(PlayerSample::name)
                .collect(Collectors.joining("\n")
        );
    }
}
