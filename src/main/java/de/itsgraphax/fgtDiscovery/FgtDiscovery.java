package de.itsgraphax.fgtDiscovery;

import de.itsgraphax.fgtDiscovery.commands.*;
import de.itsgraphax.fgtDiscovery.listeners.HubListener;
import de.itsgraphax.fgtDiscovery.listeners.JoinQuitMessage;
import de.itsgraphax.fgtDiscovery.tick.HubTick;
import de.itsgraphax.fgtDiscovery.util.Namespaces;
import de.itsgraphax.fgtDiscovery.util.PdcData;
import de.itsgraphax.grphxLib.shorthands.OnEnable;
import de.itsgraphax.grphxLib.utils.RichText;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;

public final class FgtDiscovery extends JavaPlugin {
    private static FgtDiscovery instance;

    private Namespaces namespaces;
    private final RichText.RichConfigText richText = new RichText.RichConfigText(this);
    private PdcData pdcData;

    private static void accept(BukkitTask ignoredTask) {

        HubTick.tick();

    }

    private void startTickTasks() {
        getServer().getScheduler().runTaskTimer(this, FgtDiscovery::accept, 1, 1);
    }

    @Override
    public void onEnable() {
        instance = this;
        namespaces = new Namespaces();
        pdcData = new PdcData();

        saveDefaultConfig();

        OnEnable.registerCommands(Set.of(
                MainBrigadier::register,
                JoinBrigadier::register,
                CustomBrigadier::register,
                PlayerListBrigadier::register,
                SummonPlayerListBrigadier::register,
                c -> {
                    if (getConfig().getBoolean("enable-hub", false)) {
                        HubBrigadier.register(c);
                    }
                }
        ), this);
        OnEnable.registerEvents(Set.of(
                new JoinQuitMessage(),
                new HubListener()
        ), this);
        startTickTasks();
    }

    public static FgtDiscovery getInstance() {
        return instance;
    }

    public Namespaces namespaces() {
        return namespaces;
    }

    public RichText.RichConfigText richText() {
        return richText;
    }

    public PdcData pdcData() {
        return pdcData;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
