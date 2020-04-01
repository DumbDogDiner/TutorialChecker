package kokumaji.tutorialchecker;

import kokumaji.tutorialchecker.commands.TCheckerCommand;
import kokumaji.tutorialchecker.listeners.BindingListener;
import kokumaji.tutorialchecker.listeners.PlayerJoinLeaveListener;
import kokumaji.tutorialchecker.util.PlayerCache;
import kokumaji.tutorialchecker.util.PlayerConfig;
import kokumaji.tutorialchecker.util.SectionCache;
import kokumaji.tutorialchecker.util.StringFormatting;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class TutorialChecker extends JavaPlugin implements Listener {

    private File userFolder;
    private String consolePrefix = "[TutorialChecker] ";

    public void onEnable() {
        this.saveDefaultConfig();
        userFolder = new File(getDataFolder(), "players");
        userFolder.mkdirs();

        Bukkit.getServer().getPluginManager().registerEvents(new BindingListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new TCheckerCommand(), this);
        this.getCommand("tutorialchecker").setExecutor(new TCheckerCommand());
        this.getCommand("tutorialchecker").setTabCompleter(new TCheckerCommand());

        System.out.println(consolePrefix + "§aAttempting to load existing sections to cache");
        try {
            Set<String> savedSections = getPlugin().getConfig().getConfigurationSection("sections").getKeys(false);
            for(String s : savedSections) {

                String world = getPlugin().getConfig().getString("sections." + s + ".loc.world");
                double x = getPlugin().getConfig().getDouble("sections." + s + ".loc.x");
                double y = getPlugin().getConfig().getDouble("sections." + s + ".loc.y");
                double z = getPlugin().getConfig().getDouble("sections." + s + ".loc.z");

                Location locFromConfig = new Location(Bukkit.getWorld(world), x, y, z);
                SectionCache.cacheSection(locFromConfig, s);
                String successMessage = String.format(consolePrefix + "Loaded tutorial section %s to cache. %2s", s, StringFormatting.beautifyLocation(locFromConfig));
                System.out.println("§a" + successMessage);
            }
        } catch(Exception err) {
            String errMessage = String.format(consolePrefix + "Uh oh! Something went wrong while loading the config to cache: %s", err.getMessage());
            System.out.println("§c" + errMessage);
        }

    }

    public void onDisable() {
        System.out.println(consolePrefix + "§aAttempting to save cached data before shutting down");
        for(Player p : Bukkit.getOnlinePlayers()) {
            YamlConfiguration playerYML = YamlConfiguration.loadConfiguration(PlayerConfig.loadFile(p.getUniqueId()));
            List<String> savedCleared = playerYML.getStringList("sectionsRead");

            List<String> cachedClearedLast = PlayerCache.getCleared(p.getUniqueId());
            if(cachedClearedLast == null) continue;
            for(String s : cachedClearedLast) {
                if(s == null) continue;
                if(!savedCleared.contains(s)) {
                    savedCleared.add(s);
                }
            }

            playerYML.set("sectionsRead", savedCleared);
            try {
                playerYML.save(PlayerConfig.loadFile(p.getUniqueId()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            PlayerCache.removeFromCache(p.getUniqueId());
        }
    }

    public static Plugin getPlugin() {
        return getPlugin(TutorialChecker.class);
    }

}
