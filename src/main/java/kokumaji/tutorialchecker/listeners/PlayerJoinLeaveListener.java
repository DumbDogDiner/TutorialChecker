package kokumaji.tutorialchecker.listeners;

import kokumaji.tutorialchecker.util.PlayerCache;
import kokumaji.tutorialchecker.util.PlayerConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.List;

public class PlayerJoinLeaveListener implements Listener {
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e) throws IOException {
        Player ePlayer = e.getPlayer();
        PlayerConfig.createFile(ePlayer);

        YamlConfiguration playerYML = YamlConfiguration.loadConfiguration(PlayerConfig.loadFile(ePlayer.getUniqueId()));

        List<String> cleared = playerYML.getStringList("sectionsRead");
        PlayerCache.loadPlayerToCache(ePlayer.getUniqueId(), cleared);
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent e) throws IOException {
        Player ePlayer = e.getPlayer();
        YamlConfiguration playerYML = YamlConfiguration.loadConfiguration(PlayerConfig.loadFile(ePlayer.getUniqueId()));
        List<String> savedCleared = playerYML.getStringList("sectionsRead");

        List<String> cachedClearedLast = PlayerCache.getCleared(ePlayer.getUniqueId());
        if(cachedClearedLast == null) return;

        for(String s : cachedClearedLast) {
            if(!savedCleared.contains(s)) {
                savedCleared.add(s);
            }
        }

        playerYML.set("sectionsRead", savedCleared);
        playerYML.save(PlayerConfig.loadFile(ePlayer.getUniqueId()));
        PlayerCache.removeFromCache(ePlayer.getUniqueId());
    }
}
