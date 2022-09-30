package me.uniqueimpact.magicsigns.commands;

import me.uniqueimpact.magicsigns.MagicSigns;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private MagicSigns plugin;

    public ReloadCommand(MagicSigns plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                sender.sendMessage("This server is running the Magic Signs plugin, by UniqueImpact.");
            } else if (args.length == 1) {
                if (args[0].equals("reload")) {
                    if (player.hasPermission("magicsigns.reload")) {
                        plugin.saveSignBlocks();
                        plugin.reset();
                        sender.sendMessage("The Magic Signs plugin has been successfully reloaded.");
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to use that command."));
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage("This command can only be excecuted by a player.");
        }

        return true;

    }

}
