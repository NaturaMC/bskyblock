package us.tastybento.bskyblock.listeners.flags;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockRedstoneEvent;

import us.tastybento.bskyblock.api.flags.AbstractFlagListener;
import us.tastybento.bskyblock.lists.Flags;

public class OfflineRedstoneListener extends AbstractFlagListener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockRedstone(BlockRedstoneEvent e) {
        // If offline redstone is allowed then return
        if (!Flags.OFFLINE_REDSTONE.isSetForWorld(e.getBlock().getWorld())) {
            // Check if island exists and members are online
            getIslands().getProtectedIslandAt(e.getBlock().getLocation()).ifPresent(i -> {
                for (UUID uuid : i.getMemberSet()) {
                    if (Bukkit.getPlayer(uuid) != null) {
                        return;
                    }
                }
                e.setNewCurrent(0);
            });
        }
    }
}
