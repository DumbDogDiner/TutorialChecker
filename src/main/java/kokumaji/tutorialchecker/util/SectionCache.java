package kokumaji.tutorialchecker.util;

import kokumaji.tutorialchecker.TutorialChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;

public class SectionCache {

    private static HashMap<Location, String> sectionsCache = new HashMap<>();

    public static void cacheSection(Location pLoc, String pName) {
        sectionsCache.put(pLoc, pName);
    }

    public static void removeSection(String pName) {
        String world = TutorialChecker.getPlugin().getConfig().getString("sections." + pName + ".loc.world");
        double x = TutorialChecker.getPlugin().getConfig().getDouble("sections." + pName + ".loc.x");
        double y = TutorialChecker.getPlugin().getConfig().getDouble("sections." + pName + ".loc.y");
        double z = TutorialChecker.getPlugin().getConfig().getDouble("sections." + pName + ".loc.z");


        Location locFromName = new Location(Bukkit.getWorld(world), x, y, z);

        sectionsCache.remove(locFromName);

        TutorialChecker.getPlugin().getConfig().set("sections." + pName, null);
    }

    public static String getSectionName(Location pLoc) {
        return sectionsCache.get(pLoc);
    }

    public static boolean isSection(Location pLoc) {
        return sectionsCache.containsKey(pLoc);
    }

    public static ArrayList<String> getActiveSections() {
        return new ArrayList<>(sectionsCache.values());
    }

}
