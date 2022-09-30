package me.uniqueimpact.magicsigns.listeners;

import me.uniqueimpact.magicsigns.MagicSigns;
import me.uniqueimpact.magicsigns.objects.SignBlock;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
public class BreakSignListener implements Listener {

    private final MagicSigns plugin;

    public BreakSignListener(MagicSigns plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignBroken(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Sign) {
            for (int i = 0; i < plugin.getSignBlockList().size(); i++) {
                SignBlock signBlock = plugin.getSignBlockList().get(i);
                if (block.getLocation().equals(signBlock.getLocation())) {
                    plugin.getSignBlockList().remove(i);
                    plugin.saveSignBlocks();
                    return;
                }
            }
        }
    }

}
