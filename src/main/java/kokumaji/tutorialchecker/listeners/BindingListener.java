package kokumaji.tutorialchecker.listeners;

import kokumaji.tutorialchecker.TutorialChecker;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class BindingListener implements Listener {
    Material[] buttonTypes = {Material.BIRCH_BUTTON,
            Material.ACACIA_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.STONE_BUTTON,
    };

    Material[] signTypes = {Material.OAK_SIGN,
            Material.OAK_WALL_SIGN,
            Material.SPRUCE_SIGN,
            Material.SPRUCE_WALL_SIGN,
            Material.ACACIA_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.BIRCH_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.JUNGLE_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.DARK_OAK_SIGN,
            Material.DARK_OAK_WALL_SIGN
    };

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player ePlayer = e.getPlayer();
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = e.getClickedBlock();

            if(Arrays.asList(buttonTypes).contains(clickedBlock.getType()) || Arrays.asList(signTypes).contains(clickedBlock.getType())) {
                if(TutorialChecker.isCached(ePlayer)) {
                    if(!ePlayer.hasPermission("tutorialchecker.bind")) return;
                    if(TutorialChecker.isSection(clickedBlock.getLocation())) {
                        ePlayer.sendMessage(ChatColor.RED + "Section already exists. Either edit or remove this section!");
                        TutorialChecker.removeCached(ePlayer);
                        return;
                    }

                    String sectionID = TutorialChecker.getCachedID(ePlayer);

                    String confirmMessage = String.format("\n%s+%2s%3sAdded new tutorial section!%4s%5s", ChatColor.DARK_GREEN, ChatColor.GREEN, ChatColor.BOLD, ChatColor.GRAY, TutorialChecker.beautifyLocation(clickedBlock.getLocation()));
                    TextComponent openConfigurator = new TextComponent("§8[Configure Section]\n");
                    openConfigurator.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tcheck modify " + sectionID));

                    ePlayer.sendMessage(confirmMessage);
                    ePlayer.spigot().sendMessage(openConfigurator);
                    TutorialChecker.removeCached(ePlayer);
                    TutorialChecker.cacheSection(clickedBlock.getLocation(), sectionID);


                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".displayname", sectionID);
                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".loc.world", clickedBlock.getLocation().getWorld().getName());
                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".loc.x", clickedBlock.getLocation().getX());
                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".loc.y", clickedBlock.getLocation().getY());
                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".loc.z", clickedBlock.getLocation().getZ());
                    TutorialChecker.getPlugin().saveConfig();
                    TutorialChecker.getPlugin().reloadConfig();

                } else {


                    if(TutorialChecker.isSection(clickedBlock.getLocation())) {
                        String sectionName = TutorialChecker.getSectionName(clickedBlock.getLocation());

                        if(!TutorialChecker.playerClearedSection(ePlayer, sectionName)) {
                            ePlayer.playSound(ePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1f);

                            ePlayer.spawnParticle(Particle.VILLAGER_HAPPY, ePlayer.getLocation(), 20, 0.5, 0.5, 0.5);

                            String sectionFinished = "\n§a§lFinished Section§6 " + sectionName + "§a§l!";
                            ePlayer.sendMessage(sectionFinished);

                            TutorialChecker.addCleared(ePlayer, sectionName);
                        } else {
                            ePlayer.playSound(ePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.2f, 0.3f);

                            String sectionAlreadyCleared = "§c§lYou already read this section!";

                            ePlayer.sendMessage(sectionAlreadyCleared);
                        }


                        return;
                    }

                }
            }
        }
    }
}
