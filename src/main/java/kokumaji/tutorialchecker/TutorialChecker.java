package kokumaji.tutorialchecker;

import kokumaji.tutorialchecker.commands.TCheckerCommand;
import kokumaji.tutorialchecker.listeners.BindingListener;
import kokumaji.tutorialchecker.util.PlayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class TutorialChecker extends JavaPlugin implements Listener {

    private static HashMap<Player, String> bindCache = new HashMap<Player, String>();

    private static HashMap<Player, List<String>> clearedCache = new HashMap<Player, List<String>>();

    private static HashMap<Location, String> sectionsCache = new HashMap<Location, String>();

    private File userFolder;

    public void onEnable() {
        this.saveDefaultConfig();
        userFolder = new File(getDataFolder(), "players");
        userFolder.mkdirs();

        Bukkit.getServer().getPluginManager().registerEvents(new BindingListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("tutorialchecker").setExecutor(new TCheckerCommand());
        this.getCommand("tutorialchecker").setTabCompleter(new TCheckerCommand());

        System.out.println("§aAttempting to load existing sections to cache");
        try {
            Set<String> savedSections = getPlugin().getConfig().getConfigurationSection("sections").getKeys(false);
            for(String s : savedSections) {

                String world = getPlugin().getConfig().getString("sections." + s + ".loc.world");
                double x = getPlugin().getConfig().getDouble("sections." + s + ".loc.x");
                double y = getPlugin().getConfig().getDouble("sections." + s + ".loc.y");
                double z = getPlugin().getConfig().getDouble("sections." + s + ".loc.z");

                Location locFromConfig = new Location(Bukkit.getWorld(world), x, y, z);
                cacheSection(locFromConfig, s);
                String successMessage = String.format("Loaded tutorial section %s to cache. %2s", s, beautifyLocation(locFromConfig));
                System.out.println("§a" + successMessage);
            }
        } catch(Exception err) {
            String errMessage = String.format("Uh oh! Something went wrong while loading the config to cache: %s", err.getMessage());
            System.out.println("§c" + errMessage);
        }

    }

    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e) throws IOException {
        Player ePlayer = e.getPlayer();
        PlayerConfig.createFile(ePlayer);

        YamlConfiguration playerYML = YamlConfiguration.loadConfiguration(PlayerConfig.loadFile(ePlayer));

        List<String> cleared = playerYML.getStringList("sectionsRead");
        loadPlayerToCache(ePlayer, cleared);
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent e) throws IOException {
        Player ePlayer = e.getPlayer();
        YamlConfiguration playerYML = YamlConfiguration.loadConfiguration(PlayerConfig.loadFile(ePlayer));
        List<String> savedCleared = playerYML.getStringList("sectionsRead");

        List<String> cachedClearedLast = getCleared(ePlayer);
        for(String s : cachedClearedLast) {
            if(!savedCleared.contains(s)) {
                savedCleared.add(s);
            }
        }

        playerYML.set("sectionsRead", savedCleared);
        playerYML.save(PlayerConfig.loadFile(ePlayer));
        removeFromClCache(ePlayer);
    }

    public static Plugin getPlugin() {
        return getPlugin(TutorialChecker.class);
    }

    public static void cachePlayer(Player pPlayer, String pSectionName) {
        bindCache.put(pPlayer, pSectionName);
    }

    public static void removeCached(Player pPlayer) {
        bindCache.remove(pPlayer);
    }

    public static boolean isCached(Player pPlayer) {
        return bindCache.containsKey(pPlayer);
    }

    public static void cacheSection(Location pLoc, String pName) {
        sectionsCache.put(pLoc, pName);
    }

    public static void removeSection(String pName) {
        String world = getPlugin().getConfig().getString("sections." + pName + ".loc.world");
        double x = getPlugin().getConfig().getDouble("sections." + pName + ".loc.x");
        double y = getPlugin().getConfig().getDouble("sections." + pName + ".loc.y");
        double z = getPlugin().getConfig().getDouble("sections." + pName + ".loc.z");

        Location locFromName = new Location(Bukkit.getWorld(world), x, y, z);

        sectionsCache.remove(locFromName);

        getPlugin().getConfig().set("sections." + pName, null);
    }

    public static String getSectionName(Location pLoc) {
        return sectionsCache.get(pLoc);
    }

    public static boolean isSection(Location pLoc) {
        return sectionsCache.containsKey(pLoc);
    }

    public static ArrayList<String> getActiveSections() {
        return new ArrayList<String>(sectionsCache.values());
    }

    public static String getCachedID(Player pPlayer) {
        return bindCache.get(pPlayer);
    }

    public static void addCleared(Player pPlayer, String pSectionName) {
        if(!clearedCache.containsKey(pPlayer)) {
            List<String> playerCleared = new ArrayList<String>();

            playerCleared.add(pSectionName);
            clearedCache.put(pPlayer, playerCleared);
        } else {
            List<String> playerCachedCleared = clearedCache.get(pPlayer);
            if(!playerCachedCleared.contains(pSectionName)) {
                playerCachedCleared.add(pSectionName);
                clearedCache.put(pPlayer, playerCachedCleared);
            }
        }
    }

    public static void removeFromClCache(Player pPlayer) {
        clearedCache.remove(pPlayer);
    }

    public static void loadPlayerToCache(Player pPlayer, List<String> pList) {
        clearedCache.put(pPlayer, pList);
    }

    public static List<String> getCleared(Player pPlayer) {
        return clearedCache.get(pPlayer);
    }

    public static boolean playerClearedSection(Player pPlayer, String pSectionName) {
        boolean isInCache = clearedCache.containsKey(pPlayer) && (getCleared(pPlayer).contains(pSectionName));
        return isInCache;
    }

    public static String beautifyLocation(Location pLoc) {
        int locX = pLoc.getBlockX();
        int locY = pLoc.getBlockY();
        int locZ = pLoc.getBlockZ();

        return String.format("Location (%d %2d %3d)", locX, locY, locZ);
    }

}
