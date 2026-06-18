package de.itsgraphax.fgtDiscovery.commands;

import de.itsgraphax.fgtDiscovery.HasPlugin;
import de.itsgraphax.fgtDiscovery.exceptions.ServerConfigInvalidException;
import de.itsgraphax.fgtDiscovery.exceptions.ServerNotFoundException;
import de.itsgraphax.fgtDiscovery.serverInfo.PlayerSample;
import de.itsgraphax.fgtDiscovery.serverInfo.ServerInfo;
import de.itsgraphax.fgtDiscovery.util.ServerConnectionData;
import jdk.jfr.Description;
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
        player.getWorld().spawn(player.getLocation(), TextDisplay.class, textDisplay -> {
            textDisplay.text(rt.fromConfig("playerlist-loading"));
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
                        return;
                    }

                    // change text tick-sync
                    plugin.getServer().getScheduler().runTask(plugin, _-> {
                        TextDisplay textDisplay = ((TextDisplay) entity);
                        textDisplay.text(
                                rt.fromConfig("playerlist-textdisplay-list",
                                        "PLAYERS", serverInfo.players().toString(),
                                        "ONLINE_PLAYERS", Objects.requireNonNullElse(
                                                        serverInfo.sample(),
                                                        new ArrayList<PlayerSample>()
                                                )
                                                .stream()
                                                .map(PlayerSample::name)
                                                .collect(Collectors.joining("\n"))
                                ));
                    });


                });

            }
        });
    }
}
