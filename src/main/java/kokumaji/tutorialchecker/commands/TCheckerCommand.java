package kokumaji.tutorialchecker.commands;

import kokumaji.tutorialchecker.TutorialChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TCheckerCommand implements CommandExecutor, TabCompleter {
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

                    List<String> clearedByPlayer = TutorialChecker.getCleared((Player) Bukkit.getOfflinePlayer(args[1]));
                    ArrayList<String> clearedResults = new ArrayList<String>();

                    if(clearedByPlayer != null) {
                        for (String s : TutorialChecker.getActiveSections()) {
                            if(clearedByPlayer.contains(s)) {
                                clearedResults.add("§7[§a§l✓§7] §a" + s);
                            } else {
                                clearedResults.add("§7[§c§l✗§7] §c" + s);
                            }
                        }

                        sender.sendMessage("§7§lSections read by player §6" + args[1] + "§7:\n" + String.join("\n§r", clearedResults));
                    } else {
                        for (String s : TutorialChecker.getActiveSections()) {
                            clearedResults.add("§7[§c§l✗§7] §c" + s);
                        }

                        sender.sendMessage("§7§lSections read by player §6" + args[1] + "§7:\n" + String.join("\n§r", clearedResults));
                    }

                }
            } else if (args[0].equalsIgnoreCase("sections")) {

                if(args[1].equalsIgnoreCase("list")) {
                    List<String> allSections = TutorialChecker.getActiveSections();

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
                    }
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if(args.length != 3) {
                        sender.sendMessage("§6§lTChecker §7» Wrong command usage! /tchecker sections bind < name >");
                    } else {
                        if(TutorialChecker.getActiveSections().contains(args[2])) {
                            TutorialChecker.removeSection(args[2]);
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
                        if(!TutorialChecker.isCached((Player) sender)) {
                            String bindMessage = String.format("§6§lTChecker §7» %sRight-click a sign or a button to add a new section", ChatColor.GRAY);
                            sender.sendMessage(bindMessage);
                            if(args.length != 3) {
                                sender.sendMessage("§6§lTChecker §7» Wrong command usage! /tchecker sections bind < name >");
                            } else {
                                TutorialChecker.cachePlayer((Player) sender, args[2]);
                            }

                        } else {
                            String cancelMessage = String.format("§6§lTChecker §7» %s%2sCancelled binding command", ChatColor.RED, ChatColor.BOLD);
                            sender.sendMessage(cancelMessage);
                            TutorialChecker.removeCached((Player) sender);
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
            }
        }
        return null;
    }
}
