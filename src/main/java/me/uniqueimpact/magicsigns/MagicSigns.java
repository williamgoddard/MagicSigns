package me.uniqueimpact.magicsigns;

import me.clip.placeholderapi.PlaceholderAPI;
import me.uniqueimpact.magicsigns.commands.ReloadCommand;
import me.uniqueimpact.magicsigns.listeners.BreakSignListener;
import me.uniqueimpact.magicsigns.listeners.ClickSignListener;
import me.uniqueimpact.magicsigns.listeners.PlaceSignListener;
import me.uniqueimpact.magicsigns.objects.MagicSign;
import me.uniqueimpact.magicsigns.objects.SignBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class MagicSigns extends JavaPlugin {

    private BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    private FileConfiguration config;
    private File signBlocksFile;
    private FileConfiguration signBlocksConfig;

    private List<MagicSign> magicSigns;
    private List<SignBlock> signBlocks;
    private boolean papiLoaded = false;

    @Override
    public void onEnable() {
        papiLoaded = (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null);

        this.saveDefaultConfig();
        this.reset();
        this.saveSignBlocks();

        getServer().getPluginManager().registerEvents(new PlaceSignListener(this), this);
        getServer().getPluginManager().registerEvents(new BreakSignListener(this), this);
        getServer().getPluginManager().registerEvents(new ClickSignListener(this), this);

        getCommand("magicsigns").setExecutor(new ReloadCommand(this));

        int updateFrequency = config.getInt("UpdateFrequency");

        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                updateSignBlocks();
            }
        }, updateFrequency * 20, updateFrequency * 20);
    }

    @Override
    public void onDisable() {

        this.saveSignBlocks();

    }

    public void reset() {
        papiLoaded = (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null);

        reloadConfig();
        this.config = getConfig();
        this.signBlocksFile = new File(this.getDataFolder() + "/signs.yml");
        this.signBlocksConfig = YamlConfiguration.loadConfiguration(signBlocksFile);

        this.refreshSignList();
        this.refreshSignBlockList();
    }

    public void refreshSignList() {

        this.magicSigns = new ArrayList<MagicSign>();
        ConfigurationSection configSection = config.getConfigurationSection("Signs");

        if (!(configSection == null)) {

            Set<String> keys = configSection.getKeys(false);
            Iterator<String> keysIterator = keys.iterator();

            while (keysIterator.hasNext()) {

                String key = keysIterator.next();
                List<String> setupText = config.getStringList("Signs." + key + ".Lines");
                List<String> newText = config.getStringList("Signs." + key + ".ChangeTo");
                List<String> commands = config.getStringList("Signs." + key + ".Commands");
                String message = config.getString("Signs." + key + ".Message");
                String permission = config.getString("Signs." + key + ".Permission");

                MagicSign magicSign = new MagicSign(key, setupText, newText, commands, message, permission);
                this.magicSigns.add(magicSign);

            }

        }

    }

    public void refreshSignBlockList() {

        this.signBlocks = new ArrayList<SignBlock>();
        ConfigurationSection configSection = signBlocksConfig.getConfigurationSection("SignBlocks");

        if (!(configSection == null)) {

            Set<String> keys = configSection.getKeys(false);
            Iterator<String> keysIterator = keys.iterator();

            while (keysIterator.hasNext()) {

                String key = keysIterator.next();
                String name = signBlocksConfig.getString("SignBlocks." + key + ".SignType");
                String placedBy = signBlocksConfig.getString("SignBlocks." + key + ".PlacedBy");
                Location location = signBlocksConfig.getLocation("SignBlocks." + key + ".Location");

                SignBlock signBlock = new SignBlock(name, location, placedBy);
                this.signBlocks.add(signBlock);

            }

        }

    }

    public void saveSignBlocks() {
        this.signBlocksConfig.set("SignBlocks", null);
        for (int i = 0; i < signBlocks.size(); i++) {
            this.signBlocksConfig.set("SignBlocks." + i + ".SignType", signBlocks.get(i).getName());
            this.signBlocksConfig.set("SignBlocks." + i + ".PlacedBy", signBlocks.get(i).getPlacedBy());
            this.signBlocksConfig.set("SignBlocks." + i + ".Location", signBlocks.get(i).getLocation());
        }
        try {
            this.signBlocksConfig.save(signBlocksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSignBlocks() {

        List<SignBlock> notSigns = new ArrayList<>();

        for (int i = 0; i < this.signBlocks.size(); i++) {

            SignBlock signBlock = this.signBlocks.get(i);
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(signBlock.getPlacedBy()));
            Block block = signBlock.getLocation().getBlock();

            if (block.getState() instanceof Sign) {

                Sign sign = (Sign) block.getState();

                for (int j = 0; j < magicSigns.size(); j++) {
                    MagicSign magicSign = magicSigns.get(j);

                    if (signBlock.getName().equals(magicSign.getName())) {

                        List<String> newLines = magicSign.getNewText();
                        for (int k = 0; k < 4; k++) {
                            try {
                                String line = newLines.get(k);
                                if (this.isPapiLoaded()) {
                                    line = PlaceholderAPI.setPlaceholders(player, line);
                                }
                                line = line.replace("$player", player.getName());
                                line = ChatColor.translateAlternateColorCodes('&', line);
                                sign.setLine(k, line);
                            } catch (IndexOutOfBoundsException e) {
                                sign.setLine(k, "");
                            }
                        }
                        sign.update();

                        this.saveSignBlocks();
                        break;
                    }

                }

            } else {

                notSigns.add(signBlock);

            }

        }

        for (int i = 0; i < notSigns.size(); i++) {
            this.signBlocks.remove(notSigns.get(i));
        }

    }

    public List<MagicSign> getSignList() {
        return magicSigns;
    }

    public List<SignBlock> getSignBlockList() {
        return signBlocks;
    }

    public boolean isPapiLoaded() {
        return papiLoaded;
    }
}
