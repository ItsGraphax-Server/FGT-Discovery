package de.itsgraphax.fgtDiscovery.listeners;

import de.itsgraphax.fgtDiscovery.FgtDiscovery;
import de.itsgraphax.fgtDiscovery.util.HubUtil;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class HubListener implements Listener {
    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Block interactedBlock = event.getClickedBlock();
        boolean isInInteractable = false;
        if (interactedBlock != null) {
            isInInteractable = !FgtDiscovery.getInstance().getConfig().getStringList("hub.interactable-blocks").contains(interactedBlock.getType().toString());
        }
        if (HubUtil.isAdventureEvent(event) && HubUtil.hubEnabled() && isInInteractable) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onItemFrame(PlayerItemFrameChangeEvent event) {
        if (HubUtil.isAdventureEvent(event) && HubUtil.hubEnabled()) event.setCancelled(true);
    }

    @EventHandler
    private void onPainting(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player player) {
            if (event.getEntity() instanceof Painting && HubUtil.isAdventure(player) && HubUtil.hubEnabled()) event.setCancelled(true);
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        if (HubUtil.isAdventureEvent(event) && HubUtil.hubEnabled()) {
            FgtDiscovery plugin = FgtDiscovery.getInstance();
            ConfigurationSection spawnLocConfig = FgtDiscovery.getInstance().getConfig();

            event.getPlayer().teleport(new Location(plugin.getServer().getRespawnWorld(),
                    spawnLocConfig.getInt("x", 0)+0.5,
                    spawnLocConfig.getInt("y", 0)+0.5,
                    spawnLocConfig.getInt("z", 0)+0.5));
        }
    }
}
