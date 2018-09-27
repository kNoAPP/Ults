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

public class Executor implements CommandExecutor, TabCompleter {

	private static String[] COMMANDS = new String[] {"compass"};
	private HashMap<String, CommandHandler> commands = new HashMap<String, CommandHandler>();
	
	public Executor() {
		//commands.put("compass help", new HelpCommand(true, "/compass help", null));
		commands.put("compass setlobby", new SetlobbyCommand(false, "/compass setlobby", "compass.setlobby"));
		commands.put("compass generateplots", new PlotGenerationCommand(false, "/compass generateplots <length> <width> <size> <space>", "compass.generateplots", 
				GenericType.INTEGER, GenericType.INTEGER, GenericType.INTEGER, GenericType.INTEGER));
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
						else return ch.execute(sender, nargs);
					} else sender.sendMessage(Message.WARN.getMessage("Unknown command, type /" + command + " for help."));
				} else {
					if(command.equals("compass")) {
						sender.sendMessage(Message.INFO.getMessage("Compass - By kNoAPP"));
						sender.sendMessage(ChatColor.DARK_GREEN + "------------------");
						sender.sendMessage(Message.HELP.getMessage("/compass help - Show Help"));
						if(commands.get("compass setlobby").hasPermission(sender)) sender.sendMessage(Message.HELP.getMessage("/compass setlobby - Set the lobby at your location"));
						if(commands.get("compass generateplots").hasPermission(sender)) sender.sendMessage(Message.HELP.getMessage("/compass generateplots <length> <width> <size> <space> - Plots"));
						return true;
					}
				}
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
						String syntax = ch.getUsage().replaceFirst("/compass", "").split("\\<")[0].split("\\[")[0].split("\\(")[0];
						String[] parts = ch.getUsage().replaceFirst("/compass", "").split(" ");
						if((syntax.startsWith(typed) || typed.startsWith(syntax)) && parts.length > args.length) suggestions.add(parts[args.length]);
					}
				}
			}
			if(suggestions.size() == 0) {
				for(Player pl : Bukkit.getOnlinePlayers()) {
					if(sender instanceof ConsoleCommandSender || ((Player) sender).canSee(pl)) suggestions.add(pl.getName());
				}
			}
			Collections.sort(suggestions);
		}
		return suggestions;
	}
}
