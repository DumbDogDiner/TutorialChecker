package kokumaji.tutorialchecker.util;

import org.bukkit.Location;

public class StringFormatting {

    public static String beautifyLocation(Location pLoc) {
        int locX = pLoc.getBlockX();
        int locY = pLoc.getBlockY();
        int locZ = pLoc.getBlockZ();

        return String.format("Location (%d %2d %3d)", locX, locY, locZ);
    }
}
