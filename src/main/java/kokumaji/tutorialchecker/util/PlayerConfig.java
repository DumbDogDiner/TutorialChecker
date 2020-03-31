package kokumaji.tutorialchecker.util;

import kokumaji.tutorialchecker.TutorialChecker;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class PlayerConfig {
    private static File playerConfig;

    public static File loadFile(Player pPlayer) {
        return new File(TutorialChecker.getPlugin().getDataFolder() + "/players", pPlayer.getUniqueId() + ".yml");
    }

    public static void createFile(Player pPlayer) throws IOException {
        playerConfig = new File(TutorialChecker.getPlugin().getDataFolder() + "/players", pPlayer.getUniqueId() + ".yml");
        if(!playerConfig.exists()) {
            playerConfig.createNewFile();
            YamlConfiguration initYML = new YamlConfiguration();

            initYML.set("sectionsRead", null);
            initYML.save(playerConfig);
        }
    }

}
