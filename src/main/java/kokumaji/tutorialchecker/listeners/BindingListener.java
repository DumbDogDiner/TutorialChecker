package kokumaji.tutorialchecker.listeners;

import kokumaji.tutorialchecker.TutorialChecker;
import kokumaji.tutorialchecker.util.CommandCache;
import kokumaji.tutorialchecker.util.PlayerCache;
import kokumaji.tutorialchecker.util.SectionCache;
import kokumaji.tutorialchecker.util.StringFormatting;
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

            if(clickedBlock == null) return;
            if(Arrays.asList(buttonTypes).contains(clickedBlock.getType()) || Arrays.asList(signTypes).contains(clickedBlock.getType())) {
                if(CommandCache.isCached(ePlayer)) {
                    if(!ePlayer.hasPermission("tutorialchecker.bind")) return;
                    if(SectionCache.isSection(clickedBlock.getLocation())) {
                        ePlayer.sendMessage(ChatColor.RED + "Section already exists. Either edit or remove this section!");
                        CommandCache.removeCached(ePlayer);
                        return;
                    }

                    String sectionID = CommandCache.getCachedID(ePlayer);

                    String confirmMessage = String.format("\n%s+%2s%3sAdded new tutorial section!%4s%5s", ChatColor.DARK_GREEN, ChatColor.GREEN, ChatColor.BOLD, ChatColor.GRAY, StringFormatting.beautifyLocation(clickedBlock.getLocation()));
                    TextComponent openConfigurator = new TextComponent("§8[Configure Section]\n");
                    openConfigurator.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tcheck sections modify " + sectionID));

                    ePlayer.sendMessage(confirmMessage);
                    ePlayer.spigot().sendMessage(openConfigurator);
                    CommandCache.removeCached(ePlayer);
                    SectionCache.cacheSection(clickedBlock.getLocation(), sectionID);


                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".displayname", sectionID);
                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".playEffect", true);
                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".playSound", true);

                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".loc.world", clickedBlock.getLocation().getWorld().getName());
                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".loc.x", clickedBlock.getLocation().getX());
                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".loc.y", clickedBlock.getLocation().getY());
                    TutorialChecker.getPlugin().getConfig().set("sections." + sectionID + ".loc.z", clickedBlock.getLocation().getZ());
                    TutorialChecker.getPlugin().saveConfig();
                    TutorialChecker.getPlugin().reloadConfig();

                } else {


                    if(SectionCache.isSection(clickedBlock.getLocation())) {
                        String sectionID = SectionCache.getSectionName(clickedBlock.getLocation());
                        String sectionName = TutorialChecker.getPlugin().getConfig().getString("sections." + sectionID + ".displayname");

                        if(!PlayerCache.playerClearedSection(ePlayer.getUniqueId(), sectionID)) {
                            if(TutorialChecker.getPlugin().getConfig().getBoolean("sections." + sectionID + ".playSound")) {
                                ePlayer.playSound(ePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1f);
                            }
                            if(TutorialChecker.getPlugin().getConfig().getBoolean("sections." + sectionID + ".playEffect")) {
                                ePlayer.spawnParticle(Particle.VILLAGER_HAPPY, ePlayer.getLocation(), 20, 0.5, 0.5, 0.5);
                            }

                            String sectionFinished = "\n§a§lFinished Section§6 " + sectionName + "§a§l!";
                            ePlayer.sendMessage(sectionFinished);

                            PlayerCache.addCleared(ePlayer.getUniqueId(), sectionID);
                        } else {
                            if(TutorialChecker.getPlugin().getConfig().getBoolean("sections." + sectionID + ".playSound")) {
                                ePlayer.playSound(ePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.2f, 0.3f);
                            }

                            String sectionAlreadyCleared = "§c§lYou already read this section!";

                            ePlayer.sendMessage(sectionAlreadyCleared);
                        }

                    }

                }
            }
        }
    }
}
