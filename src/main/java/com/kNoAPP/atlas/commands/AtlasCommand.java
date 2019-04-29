package com.kNoAPP.atlas.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AtlasCommand implements TabExecutor {

	private static final String NO_PERMISSION = ChatColor.GOLD + "Permission> " + ChatColor.GRAY + "You are missing permission " + ChatColor.DARK_AQUA + "%perm%" + ChatColor.GRAY + "!";
	private static final String USAGE = ChatColor.GOLD + "Try> " + ChatColor.GRAY + "%usage%";
	
	private CommandInfo info = getClass().getAnnotation(CommandInfo.class);

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(!info.permission().equals("") && !sender.hasPermission(info.permission()))
			return null;
		
		Formation form = getFormation();
		if(form.lastMatch(args) >= args.length - 2) {
			int type = form.getArgType(args.length - 1);
			switch(type) {
			case Formation.PLAYER:
				return sender instanceof Player ? form.getPlayer((Player) sender) : form.getPlayer();
			case Formation.NUMBER:
				return form.getNumber(args.length - 1);
			case Formation.LIST:
				return form.getList(args.length - 1);
			case Formation.STRING:
				return form.getString(args.length - 1);
			default:
				return null;
			}
		} else return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!info.permission().equals("") && !sender.hasPermission(info.permission())) {
			alertNoPermission(sender, info.permission());
			return true;
		}
		
		if(!validArgs(args.length)) {
			alertUsage(sender, info.usage());
			return true;
		}
		
		int lastMatchedArg = getFormation().lastMatch(args);
		if(lastMatchedArg < args.length - 1) {
			if(info.argMatch() <= lastMatchedArg)
				alertUsage(sender, info.usage());
			return true;
		}
		
		if(sender instanceof Player) 
			return onCommand((Player) sender, args);
		if(sender instanceof ConsoleCommandSender)
			return onCommand((ConsoleCommandSender) sender, args);
		return true;
	}
	
	protected void alertNoPermission(CommandSender sender, String permission) {
		sender.sendMessage(NO_PERMISSION.replaceAll("%perm%", permission));
	}
	
	protected void alertUsage(CommandSender sender, String usage) {
		sender.sendMessage(USAGE.replaceAll("%usage%", usage));
	}
	
	private boolean validArgs(int passed) {
		for(int a : info.length()) {
			if(a == -1)
				throw new UnsupportedOperationException("CommandInfo length property is not set!");	
			if(passed == a)
				return true;
		}
		
		return false;
	}
	
	protected boolean onCommand(Player sender, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Warn> " + ChatColor.RED + "This command may only be run by the console.");
		return true; 
	}
	
	protected boolean onCommand(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Warn> " + ChatColor.RED + "This command may only be run by players.");
		return true; 
	}
	
	protected abstract Formation getFormation();
	
	public CommandInfo getInfo() {
		return info;
	}
	
	public void registerCommand(JavaPlugin plugin) {
		if(info == null)
			throw new UnsupportedOperationException("CommandInfo annotation is missing!");	
		
		PluginCommand pc = plugin.getCommand(info.name());
		if(pc == null) {
			try {
				Constructor<PluginCommand> cons = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
				cons.setAccessible(true);
				pc = cons.newInstance(info.name(), plugin);
				
				pc.setAliases(Arrays.asList(info.aliases()));
				pc.setDescription(info.description());
				pc.setUsage(info.usage());
				
				Field cmdMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                cmdMap.setAccessible(true);
                CommandMap map = (CommandMap) cmdMap.get(Bukkit.getPluginManager());

                map.register(plugin.getName(), pc);
			} catch (NoSuchMethodException | IllegalAccessException |
                    InstantiationException | InvocationTargetException |
                    NoSuchFieldException e) {
                e.printStackTrace();
                plugin.getLogger().warning("Failed to load " + this.getClass().getName() + ": /" + info.name());
                return;
            }
			
			plugin.getLogger().info("Successfully loaded " + this.getClass().getName() + ": /" + info.name());
			for(String alias : info.aliases())
				plugin.getLogger().info("Successfully loaded " + this.getClass().getName() + " alias: /" + alias);
		} else plugin.getLogger().info("Successfully loaded " + this.getClass().getName() + " subcommand: /" + info.name());
			
		pc.setExecutor(this);
		pc.setTabCompleter(this);
	}
}
