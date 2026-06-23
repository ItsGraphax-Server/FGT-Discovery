package de.itsgraphax.fgtDiscovery.commands;

import de.itsgraphax.fgtDiscovery.HasPlugin;
import jdk.jfr.Description;
import net.strokkur.commands.Command;
import net.strokkur.commands.Executes;
import net.strokkur.commands.paper.RequiresOP;
import org.bukkit.command.CommandSender;

@Command("discovery")
@Description("Operator Commands")
@RequiresOP
public class Main implements HasPlugin {
    @Executes("reload")
    void reload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(rt.fromConfig("config-reloaded"));
    }
}
