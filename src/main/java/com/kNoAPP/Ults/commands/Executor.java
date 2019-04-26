package com.kNoAPP.Ults.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.kNoAPP.Ults.aspects.Message;
import com.kNoAPP.Ults.commands.CommandHandler.GenericType;

public class Executor implements CommandExecutor, TabCompleter {

	private static String[] COMMANDS = new String[] {"ult"};
	private HashMap<String, CommandHandler> commands = new HashMap<String, CommandHandler>();
	
	public Executor() {
		commands.put("ult help", new HelpCommand(true, "/ult help", null));
		commands.put("ult recall", new RecallCommand(false, "/ult recall (set/kill)", null, 0, GenericType.STRING));
		commands.put("ult scramble", new ScrambleCommand(false, "/ult scramble <off/on> [radius] [ticks]", null, 1, GenericType.STRING, GenericType.INTEGER, GenericType.INTEGER));
		commands.put("ult ults", new UltimateCommand(false, "/ult ults <kindred>", null, GenericType.STRING));
		commands.put("ult soundgen", new SoundGenCommand(false, "/ult soundgen (on/off)", null, 0, GenericType.STRING));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		for(String command : COMMANDS) {
			if(cmd.getName().equalsIgnoreCase(command)) {
				if(args.length > 0) {
					CommandHandler ch = commands.get(command + " " + args[0].toLowerCase());
					if(ch != null) {
						String nargs[] = new String[args.length-1];
						for(int i=1; i<args.length; i++) nargs[i-1] = args[i];
						if(!ch.hasPermission(sender)) sender.sendMessage(Message.MISSING.getMessage(ch.getPermission() != null ? ch.getPermission() : "OP"));
						else if(!ch.allowConsole() && sender instanceof ConsoleCommandSender) sender.sendMessage(ChatColor.GOLD + "Console> " + ChatColor.GRAY + "This command may only be run by players.");
						else if(!ch.validArgs(nargs)) sender.sendMessage(Message.USAGE.getMessage(ch.getUsage()));
						else if(!ch.execute(sender, nargs)) sender.sendMessage(Message.USAGE.getMessage(ch.getUsage()));
					} else sender.sendMessage(Message.WARN.getMessage("Unknown command, type /" + command + " for help."));
				} else {
					if(command.equals("ult")) {
						sender.sendMessage(Message.INFO.getMessage("Ultimates - By kNoAPP"));
						sender.sendMessage(ChatColor.DARK_GREEN + "------------------");
						sender.sendMessage(Message.HELP.getMessage("/ult help - Show Help"));
						if(commands.get("ult recall").hasPermission(sender)) sender.sendMessage(Message.HELP.getMessage("/ult recall <set/kill> - Recall to a location"));
						if(commands.get("ult scramble").hasPermission(sender)) sender.sendMessage(Message.HELP.getMessage("/ult scramble <off/on> <radius> <ticks> - Screw JayJay05"));
						if(commands.get("ult ults").hasPermission(sender)) sender.sendMessage(Message.HELP.getMessage("/ult ults <kindred> - Cool particle effects"));
						if(commands.get("ult soundgen").hasPermission(sender)) sender.sendMessage(Message.HELP.getMessage("/ult soundgen (on/off) - Generate custom sounds"));
					}
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> suggestions = new ArrayList<String>();
		for(String command : COMMANDS) {
			if(cmd.getName().equalsIgnoreCase(command)) {
				String typed = "";
				for(String arg : args) typed += " " + arg;
				
				for(CommandHandler ch : commands.values()) {
					if(ch.hasPermission(sender)) {
						String syntax = ch.getUsage().replaceFirst("/" + command, "").split("\\<")[0].split("\\[")[0].split("\\(")[0];
						String[] parts = ch.getUsage().replaceFirst("/" + command, "").split(" ");
						if((syntax.startsWith(typed) || typed.startsWith(syntax)) && parts.length > args.length) suggestions.add(parts[args.length]);
					}
				}
			}
			if(suggestions.size() == 0) {
				for(Player pl : Bukkit.getOnlinePlayers()) 
					if(sender instanceof ConsoleCommandSender || ((Player) sender).canSee(pl)) suggestions.add(pl.getName());
			}
			Collections.sort(suggestions);
		}
		return suggestions;
	}
}
