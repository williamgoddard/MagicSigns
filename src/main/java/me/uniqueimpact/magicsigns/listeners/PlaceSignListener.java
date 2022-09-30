package me.uniqueimpact.magicsigns.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.uniqueimpact.magicsigns.MagicSigns;
import me.uniqueimpact.magicsigns.objects.MagicSign;
import me.uniqueimpact.magicsigns.objects.SignBlock;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.List;

public class PlaceSignListener implements Listener {

    private final MagicSigns plugin;

    public PlaceSignListener(MagicSigns plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignPlaced(SignChangeEvent event) {
        List<MagicSign> magicSigns = plugin.getSignList();
        String[] signText = event.getLines();
        Block block = event.getBlock();
        Player player = event.getPlayer();

        for (int i = 0; i < magicSigns.size(); i++) {
            MagicSign magicSign = magicSigns.get(i);
            List<String> lines = magicSign.getSetupText();
            boolean matches = true;

            for (int j = 0; j < 4; j++) {
                try {
                    if (!lines.get(j).equals(signText[j])) {
                        matches = false;
                    }
                } catch (IndexOutOfBoundsException e) {
                    if (!signText[j].equals("")) {
                        matches = false;
                    }
                }
            }

            if (matches) {

                List<String> newLines = magicSign.getNewText();
                for (int j = 0; j < 4; j++) {
                    try {
                        String line = newLines.get(j);
                        if (plugin.isPapiLoaded()) {
                            line = PlaceholderAPI.setPlaceholders(player, line);
                        }
                        line = line.replace("$player", player.getName());
                        line = ChatColor.translateAlternateColorCodes('&', line);
                        event.setLine(j, line);
                    } catch (IndexOutOfBoundsException e) {
                        event.setLine(j, "");
                    }
                }

                plugin.getSignBlockList().add(new SignBlock(magicSign.getName(), block.getLocation(), player.getUniqueId().toString()));
                plugin.saveSignBlocks();

                return;
            }

        }

    }

}
