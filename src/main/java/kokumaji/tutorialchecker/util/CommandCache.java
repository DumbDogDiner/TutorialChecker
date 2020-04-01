package kokumaji.tutorialchecker.util;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandCache {
    private static HashMap<Player, String> bindCache = new HashMap<>();
    private static HashMap<Player, String> renameCache = new HashMap<>();

    public static void cachePlayer(Player pPlayer, String pSectionName) {
        bindCache.put(pPlayer, pSectionName);
    }

    public static void removeCached(Player pPlayer) {
        bindCache.remove(pPlayer);
    }

    public static boolean isCached(Player pPlayer) {
        return bindCache.containsKey(pPlayer);
    }

    public static String getCachedID(Player pPlayer) {
        return bindCache.get(pPlayer);
    }

    public static void cachePlayerRename(Player pPlayer, String pString) {
        renameCache.put(pPlayer, pString);
    }

    public static void removeCachedPlayerRename(Player pPlayer) {
        renameCache.remove(pPlayer);
    }

    public static Boolean isRenameCached(Player pPlayer) {
        return renameCache.containsKey(pPlayer);
    }

    public static String getCachedRenameID(Player pPlayer) {
        return renameCache.get(pPlayer);
    }
}
