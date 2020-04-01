package kokumaji.tutorialchecker.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerCache {
    private static HashMap<UUID, List<String>> clearedCache = new HashMap<UUID, List<String>>();

    public static void addCleared(UUID pPlayer, String pSectionName) {
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

    public static void removeFromCache(UUID pPlayer) {
        clearedCache.remove(pPlayer);
    }

    public static void loadPlayerToCache(UUID pPlayer, List<String> pList) {
        clearedCache.put(pPlayer, pList);
    }

    public static List<String> getCleared(UUID pPlayer) {
        List<String> cleared;

        if(Bukkit.getOfflinePlayer(pPlayer).isOnline()) {
            cleared = clearedCache.get(pPlayer);

        } else {
            YamlConfiguration playerYML = YamlConfiguration.loadConfiguration(PlayerConfig.loadFile(pPlayer));
            cleared = playerYML.getStringList("sectionsRead");

        }
        return cleared;

    }

    public static boolean playerClearedSection(UUID pPlayer, String pSectionName) {
        boolean isInCache = clearedCache.containsKey(pPlayer) && (getCleared(pPlayer).contains(pSectionName));
        return isInCache;
    }
}
