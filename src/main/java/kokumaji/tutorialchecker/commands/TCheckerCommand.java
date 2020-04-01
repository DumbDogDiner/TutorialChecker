package kokumaji.tutorialchecker.commands;

import kokumaji.tutorialchecker.TutorialChecker;
import kokumaji.tutorialchecker.util.CommandCache;
import kokumaji.tutorialchecker.util.MojangRequest;
import kokumaji.tutorialchecker.util.PlayerCache;
import kokumaji.tutorialchecker.util.SectionCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TCheckerCommand implements CommandExecutor, TabCompleter, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("check")) {
                if(!sender.hasPermission("tutorialchecker.check")) {
                    sender.sendMessage("§cYou can't execute this command.");
                }
                if(args.length < 2) {
                    sender.sendMessage("Please specify a player");
                } else {
                    UUID pUUID = MojangRequest.getUUID(args[1]);

                    if(pUUID != null && Bukkit.getOfflinePlayer(pUUID).hasPlayedBefore()) {
                        List<String> clearedByPlayer = null;
                        clearedByPlayer = PlayerCache.getCleared(pUUID);

                        ArrayList<String> clearedResults = new ArrayList<>();

                        for (String s : SectionCache.getActiveSections()) {
                            String sectionName = TutorialChecker.getPlugin().getConfig().getString("sections." + s + ".displayname");
                            if(clearedByPlayer.contains(s)) {
                                clearedResults.add("§7[§a§l✓§7] §a" + sectionName);
                            } else {
                                clearedResults.add("§7[§c§l✗§7] §c" + sectionName);
                            }
                        }

                        sender.sendMessage("§7§lSections read by player §6" + args[1] + "§7:\n" + String.join("\n§r", clearedResults));

                    } else {
                        sender.sendMessage("§6§lTChecker §7» Couldn't find player data");
                    }

                }
            } else if (args[0].equalsIgnoreCase("sections")) {

                if(args[1].equalsIgnoreCase("list")) {
                    List<String> allSections = SectionCache.getActiveSections();

                    List<String> beautifiedSections = new ArrayList<>();
                    for(String s : allSections) {
                        beautifiedSections.add("§8» §6" + s);
                    }

                    String listOutput = String.join("\n", beautifiedSections);
                    sender.sendMessage("§6§lTChecker §7» Available tutorial sections: \n" + listOutput);
                }

                if(args[1].equalsIgnoreCase("modify")) {
                    if(!sender.hasPermission("tutorialchecker.modify")) {
                        sender.sendMessage("§cYou can't execute this command.");
                    } else {
                        Player pSender = (Player)sender;

                        Inventory inv = Bukkit.createInventory(null, 9, "Edit Section " + args[2]);
                        pSender.openInventory(inv);
                        initInventory(inv, args[2]);
                    }
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if(args.length != 3) {
                        sender.sendMessage("§6§lTChecker §7» Wrong command usage! /tchecker sections remove < name >");
                    } else {
                        if(SectionCache.getActiveSections().contains(args[2])) {
                            SectionCache.removeSection(args[2]);
                            sender.sendMessage("§6§lTChecker §7» Removed tutorial section \"§6" + args[2] + "§7\" from the config.");
                        } else {
                            sender.sendMessage("§6§lTChecker §7» Couldn't find section in config");
                        }
                    }
                } else if (args[1].equalsIgnoreCase("bind")) {
                    if(!sender.hasPermission("tutorialchecker.bind")) {
                        sender.sendMessage("§cYou can't execute this command.");
                    }
                    if(sender instanceof Player) {
                        if(!CommandCache.isCached((Player) sender)) {
                            if(args.length != 3) {
                                sender.sendMessage("§6§lTChecker §7» Wrong command usage! /tchecker sections bind < name >");
                            } else {
                                String bindMessage = String.format("§6§lTChecker §7» %sRight-click a sign or a button to add a new section", ChatColor.GRAY);
                                sender.sendMessage(bindMessage);
                                CommandCache.cachePlayer((Player) sender, args[2]);
                            }

                        } else {
                            String cancelMessage = String.format("§6§lTChecker §7» %s%2sCancelled binding command", ChatColor.RED, ChatColor.BOLD);
                            sender.sendMessage(cancelMessage);
                            CommandCache.removeCached((Player) sender);
                        }

                    }
                }

            }
        } else {
            String defaultMessage = String.format("%s%2sTutorialChecker%3sDeveloped by Kokumaji", ChatColor.GOLD, ChatColor.BOLD, ChatColor.GRAY);
            sender.sendMessage(defaultMessage);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("tutorialchecker")) {
            if(args.length == 1) {
                ArrayList<String> options = new ArrayList<>();

                if(args[0].equalsIgnoreCase("")) {
                    options.add("check");
                    options.add("sections");

                    return options;
                }
            } else if (args.length == 2) {
                ArrayList<String> subOptions = new ArrayList<>();
                if(args[0].equalsIgnoreCase("sections")) {
                    subOptions.add("remove");
                    subOptions.add("bind");
                    subOptions.add("list");
                    subOptions.add("modify");

                    Collections.sort(subOptions);

                    return subOptions;
                }
            } else if (args.length == 3) {
                if(args[0].equalsIgnoreCase("sections")) {
                    ArrayList<String> availableSections = SectionCache.getActiveSections();
                    Collections.sort(availableSections);

                    return availableSections;
                }
            }
        }
        return null;
    }

    private void initInventory(Inventory inv, String pSection) {
        inv.clear();
        String displayName = TutorialChecker.getPlugin().getConfig().getString("sections." + pSection + ".displayname");
        boolean playSound = TutorialChecker.getPlugin().getConfig().getBoolean("sections." + pSection + ".playSound");
        boolean playEffect = TutorialChecker.getPlugin().getConfig().getBoolean("sections." + pSection + ".playEffect");

        inv.addItem(createGuiItem(Material.NAME_TAG, "§6§lDisplayname", "§7Current: " + displayName));
        if(playEffect) {
            inv.addItem(createGuiItem(Material.GREEN_CONCRETE, "§6§lParticle Effects", "§7Current: §a" + playEffect));
        } else {
            inv.addItem(createGuiItem(Material.RED_CONCRETE, "§6§lParticle Effects", "§7Current: §c" + playEffect));
        }

        if(playSound) {
            inv.addItem(createGuiItem(Material.GREEN_CONCRETE, "§6§lPlay Sound", "§7Current: §a" + playSound));
        } else {
            inv.addItem(createGuiItem(Material.RED_CONCRETE, "§6§lPlay Sound", "§7Current: §c" + playSound));
        }

        inv.setItem(8, createGuiItem(Material.BARRIER, "§cClose"));
    }

    private ItemStack createGuiItem(Material material, String name, String...lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> metaLore = new ArrayList<>();

        for(String loreComments : lore) {
            metaLore.add(loreComments);
        }

        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getSize() != 9 && !e.getView().getTitle().equals("Edit Section")) {
            return;
        }
        if (e.getClick().equals(ClickType.NUMBER_KEY)){
            e.setCancelled(true);
        }

        e.setCancelled(true);

        String pSection = e.getView().getTitle().replace("Edit Section ", "");

        if(e.getCurrentItem() != null) {

            if(e.getCurrentItem().getType() == Material.BARRIER) {
                e.getWhoClicked().closeInventory();
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Particle Effects")) {
                boolean currentVal = TutorialChecker.getPlugin().getConfig().getBoolean("sections." + pSection + ".playEffect");
                TutorialChecker.getPlugin().getConfig().set("sections." + pSection + ".playEffect", !currentVal);
                TutorialChecker.getPlugin().saveConfig();

                initInventory(e.getInventory(), pSection);


            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Play Sound")) {
                boolean currentVal = TutorialChecker.getPlugin().getConfig().getBoolean("sections." + pSection + ".playSound");
                TutorialChecker.getPlugin().getConfig().set("sections." + pSection + ".playSound", !currentVal);
                TutorialChecker.getPlugin().saveConfig();

                initInventory(e.getInventory(), pSection);

            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Displayname")) {
                CommandCache.cachePlayerRename((Player) e.getWhoClicked(), pSection);
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().sendMessage("§6§lTChecker §7» Enter the new name in chat:");
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if(!CommandCache.isRenameCached(e.getPlayer())) return;

        String newName = e.getMessage();
        String renameSectionID = CommandCache.getCachedRenameID(e.getPlayer());
        TutorialChecker.getPlugin().getConfig().set("sections." + renameSectionID + ".displayname", newName);
        TutorialChecker.getPlugin().saveConfig();

        CommandCache.removeCachedPlayerRename(e.getPlayer());
        e.setCancelled(true);
        String confirmMsg = String.format("§6§lTChecker §7» Changed name of section §6%s §7to §6%2s", renameSectionID, newName);
        e.getPlayer().sendMessage(confirmMsg);
    }

}
