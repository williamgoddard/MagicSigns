package me.uniqueimpact.magicsigns.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.uniqueimpact.magicsigns.MagicSigns;
import me.uniqueimpact.magicsigns.objects.MagicSign;
import me.uniqueimpact.magicsigns.objects.SignBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClickSignListener implements Listener {

    private final MagicSigns plugin;

    public ClickSignListener(MagicSigns plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignClicked(PlayerInteractEvent event) {

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (block == null) {
            return;
        }

        if (block.getState() instanceof Sign) {

            for (int i = 0; i < plugin.getSignBlockList().size(); i++) {

                SignBlock signBlock = plugin.getSignBlockList().get(i);

                if (block.getLocation().equals(signBlock.getLocation())) {

                    MagicSign magicSign = null;
                    for (int j = 0; j < plugin.getSignList().size(); j++) {
                        if (signBlock.getName().equals(plugin.getSignList().get(j).getName())) {
                            magicSign = plugin.getSignList().get(j);
                            break;
                        }
                    }

                    if (magicSign == null) {
                        plugin.getSignBlockList().remove(i);
                        plugin.saveSignBlocks();
                        return;
                    }

                    String permission = "magicsigns.use" + (magicSign.getPermission() != null ? "." + magicSign.getPermission() : "");
                    if (player.hasPermission(permission)) {

                        if (magicSign.getMessage() != null) {
                            String message = magicSign.getMessage();
                            if (plugin.isPapiLoaded()) {
                                message = PlaceholderAPI.setPlaceholders(player, message);
                            }
                            message = message.replace("$player", player.getName());
                            message = ChatColor.translateAlternateColorCodes('&', message);
                            player.sendMessage(message);
                        }

                        if (magicSign.getCommands() != null) {
                            for (int j = 0; j < magicSign.getCommands().size(); j++) {
                                String command = magicSign.getCommands().get(j);
                                if (plugin.isPapiLoaded()) {
                                    command = PlaceholderAPI.setPlaceholders(player, command);
                                }
                                command = command.replace("$player", player.getName());
                                plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                            }
                        }

                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou do not have permission to use that sign."));
                    }

                    return;

                }

            }

        }

    }

}
