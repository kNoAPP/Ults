package com.kNoAPP.atlas.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	private List<AtlasCommand> extensions = new ArrayList<AtlasCommand>();
	private boolean root = false;

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> suggestions = new ArrayList<String>();
		if(root) {
			for(AtlasCommand ac : extensions) {
				List<String> ret = ac.onTabComplete(sender, command, alias, args);
				if(ret != null)
					suggestions.addAll(ret);
			}
		}
		
		if(!info.permission().equals("") && !sender.hasPermission(info.permission()))
			return suggestions.size() > 0 ? suggestions : null;
		
		Formation form = getFormation();
		if(form.lastMatch(args) >= args.length - 2) {
			int type = form.getArgType(args.length - 1);
			switch(type) {
			case Formation.PLAYER:
				suggestions.addAll((sender instanceof Player ? form.getPlayer((Player) sender) : form.getPlayer()).stream().filter(s -> s.startsWith(args[args.length-1])).collect(Collectors.toList()));
				break;
			case Formation.NUMBER:
				suggestions.addAll(form.getNumber(args.length - 1).stream().filter(s -> s.startsWith(args[args.length-1])).collect(Collectors.toList()));
				break;
			case Formation.LIST:
				suggestions.addAll(form.getList(args.length - 1).stream().filter(s -> s.startsWith(args[args.length-1])).collect(Collectors.toList()));
				break;
			case Formation.STRING:
				suggestions.addAll(form.getString(args.length - 1).stream().filter(s -> s.startsWith(args[args.length-1])).collect(Collectors.toList()));
				break;
			default:
				break;
			}
		}
		return suggestions.size() > 0 ? suggestions : null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(root)
			for(AtlasCommand ac : extensions)
				if(ac.onCommand(sender, command, label, args))
					return true;
		
		boolean permission = info.permission().equals("") || sender.hasPermission(info.permission());
		int lastMatchedArg = getFormation().lastMatch(args);
		if(lastMatchedArg < args.length - 1) {
			if(info.argMatch() <= lastMatchedArg && permission)
				alertUsage(sender, info.usage());
			return root;
		}
		
		if(!permission) {
			alertNoPermission(sender, info.permission());
			return root;
		}
		
		if(!validArgs(args.length)) {
			alertUsage(sender, info.usage());
			return root;
		}
		
		if(sender instanceof Player) {
			onCommand((Player) sender, args);
			return true;
		} else if(sender instanceof ConsoleCommandSender) {
			onCommand((ConsoleCommandSender) sender, args);
			return true;
		} else return root;
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
			
			pc.setExecutor(this);
			pc.setTabCompleter(this);
			root = true;
		} else if(pc.getExecutor() instanceof AtlasCommand) {
			plugin.getLogger().info("Successfully loaded " + this.getClass().getName() + " subcommand: /" + info.name());
			((AtlasCommand) pc.getExecutor()).extensions.add(this);
		} else plugin.getLogger().warning("Command " + info.name() + " has been registered by a non-Atlas command! Cannot register.");
	}
}
