package de.itsgraphax.fgtDiscovery.commands;

import de.itsgraphax.fgtDiscovery.HasPlugin;
import de.itsgraphax.fgtDiscovery.util.PlayerTransporter;
import de.itsgraphax.fgtDiscovery.util.ServerConnectionData;
import jdk.jfr.Description;
import net.strokkur.commands.Aliases;
import net.strokkur.commands.Command;
import net.strokkur.commands.Executes;
import net.strokkur.commands.arguments.StringArg;
import net.strokkur.commands.paper.Executor;
import org.bukkit.entity.Player;

@Command("custom")
@Aliases("c")
@Description("Join or set a Custom server")

public class Custom implements HasPlugin {

    @Executes
    void join(@Executor Player player, int slot) {
        ServerConnectionData data = plugin.pdcData().server(player, slot);

        if (data.isEmpty()) {
            player.sendMessage(rt.fromConfig("strings.custom-no-data"));
            return;
        }

        PlayerTransporter.join(player, data);
    }

    @Executes("remove")
    void remove(@Executor Player player, int slot) {
        ServerConnectionData data = plugin.pdcData().server(player, slot);

        if (data.isEmpty()) {
            player.sendMessage(rt.fromConfig("strings.custom-no-data"));
            return;
        }

        plugin.pdcData().deleteServer(player, slot);

        player.sendMessage(rt.fromConfig("strings.custom-deleted"));
    }

    @Executes("add")
    void add(@Executor Player player, @StringArg() String ip, int port, int slot) {
        ServerConnectionData data = plugin.pdcData().server(player, slot);

        if (!data.isEmpty()) {
            player.sendMessage(rt.fromConfig("strings.custom-already-data"));
            return;
        }

        plugin.pdcData().server(player, new ServerConnectionData(ip, port, null), slot);

        player.sendMessage(rt.fromConfig("strings.custom-added"));
    }

}
