package com.knoban.atlas.commands;

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
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AtlasCommand implements TabExecutor, Listener {

	private static final String NO_PERMISSION = ChatColor.GOLD + "Permission> " + ChatColor.GRAY + "You are missing permission " + ChatColor.DARK_AQUA + "%perm%" + ChatColor.GRAY + "!";
	private static final String USAGE = ChatColor.GOLD + "Try> " + ChatColor.GRAY + "%usage%";
	
	private CommandInfo info = getClass().getAnnotation(CommandInfo.class);
	private List<AtlasCommand> extensions = new ArrayList<AtlasCommand>();
	private boolean root = false;

	/**
	 * Base logic, not recommended to Override.
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(!(sender instanceof Player || sender instanceof ConsoleCommandSender))
			return null;
		
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
		
		
		Formation form = getFormation((Player) sender);
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

	/**
	 * Base logic, not recommended to Override.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player || sender instanceof ConsoleCommandSender))
			return true;
		
		if(root)
			for(AtlasCommand ac : extensions)
				if(ac.onCommand(sender, command, label, args))
					return true;
		
		boolean permission = info.permission().equals("") || sender.hasPermission(info.permission());
		int lastMatchedArg = getFormation(sender).lastMatch(args);
		if(lastMatchedArg < args.length - 1 || args.length - 1 < info.argMatch()) {
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
			onCommand((Player) sender, label, args);
			return true;
		} else if(sender instanceof ConsoleCommandSender) {
			onCommand((ConsoleCommandSender) sender, label, args);
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
	
	/**
	 * Passed when a Player types a valid command that matches Formation and desired arg length.
	 * @param sender - The Player who sent the command
	 * @param args - The args of the command (guaranteed length by CommandInfo argMatch)
	 * @return Doesn't matter, gets ignored
	 */
	protected boolean onCommand(Player sender, String label, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Warn> " + ChatColor.RED + "This command may only be run by the console.");
		return true; 
	}
	
	/**
	 * Passed when a ConsoleCommandSender types a valid command that matches Formation and desired arg length.
	 * @param sender - The ConsoleCommandSender who sent the command
	 * @param args - The args of the command (guaranteed length by CommandInfo argMatch)
	 * @return Doesn't matter, gets ignored
	 */
	protected boolean onCommand(ConsoleCommandSender sender, String label, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Warn> " + ChatColor.RED + "This command may only be run by players.");
		return true; 
	}
	
	/**
	 * Use Formation.FormationBuilder to build a Formation. For constant Formations, use a private static final Formation.
	 * 
	 * Ex. new FormationBuilder().list("foo", "bar").player().number(5.5, 10, 0.5).string("potato", "tomato").build();
	 * list - REQUIRED to have one of these Strings.
	 * player - REQUIRED to include a player name. However, since a player may be offline, no argument checking occurs on the inputed String 
	 *          (Online players that the sender Player#canSee() will be recommended)
	 * number - REQUIRED to include a number (Double). Will create suggestions based on #number(low, high, step).
	 * string - OPTIONAL to have one of these Strings. (Strings other than suggestions may be passed)
	 * 
	 * @param sender - A Player or ConsoleCommandSender who tab-completed or ran the command.
	 * @return A Formation with proper command structure.
	 */
	protected abstract Formation getFormation(CommandSender sender);
	
	public CommandInfo getInfo() {
		return info;
	}
	
	/**
	 * Registers the command to the plugin with an included Event Listener.
	 * 
	 * Command execution priority follows the following set of rules
	 *      1. Zero or ONLY ONE AtlasCommand instance will be executed (or "passed") per command request.
	 *      1. The first time an AtlasCommand is registered with a specific command label (e.g. /foobar),
	 *         it becomes a "command"
	 *      2. Following registrations of AtlasCommands with the same command label become "subcommands"
	 *      3. When subcommands are present, the parent command will have the lowest execution priority
	 *      4. Subcommand priority is determined by the order the commands were registered. First come, first served.
	 * @param plugin - Plugin main class (the class instance with onEnable/onDisable, commonly passed as "this")
	 */
	public void registerCommandWithListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		registerCommand(plugin);
	}
	
	/**
	 * Registers the command to the plugin.
	 * 
	 * Command execution priority follows the following set of rules
	 *      1. Zero or ONLY ONE AtlasCommand instance will be executed (or "passed") per command request.
	 *      2. The first time an AtlasCommand is registered with a specific command label (e.g. /foobar),
	 *         it becomes a "command"
	 *      3. Following registrations of AtlasCommands with the same command label become "subcommands"
	 *      4. When subcommands are present, the parent command will have the lowest execution priority
	 *      5. Subcommand priority is determined by the order the commands were registered. First come, first served.
	 * @param plugin - Plugin main class (the class instance with onEnable/onDisable, commonly passed as "this")
	 */
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
			
			//Recursive head-call to potentially register an alias as a command
			String[] aliases = info.aliases();
			if(aliases.length > 1)
				registerCommand(aliases[0], Arrays.copyOfRange(aliases, 1, aliases.length), plugin);
		} else plugin.getLogger().warning("Command " + info.name() + " has been registered by a non-Atlas command! Cannot register.");
	}
	
	private void registerCommand(String cmd, String[] aliases, JavaPlugin plugin) {
		if(info == null)
			throw new UnsupportedOperationException("CommandInfo annotation is missing!");	
		
		PluginCommand pc = plugin.getCommand(cmd);
		if(pc == null) {
			try {
				Constructor<PluginCommand> cons = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
				cons.setAccessible(true);
				pc = cons.newInstance(cmd, plugin);
				
				pc.setAliases(Arrays.asList(aliases));
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
                plugin.getLogger().warning("Failed to load " + this.getClass().getName() + ": /" + cmd);
                return;
            }
			
			plugin.getLogger().info("Successfully loaded " + this.getClass().getName() + ": /" + cmd);
			for(String alias : aliases)
				plugin.getLogger().info("Successfully loaded " + this.getClass().getName() + " alias: /" + alias);
			
			pc.setExecutor(this);
			pc.setTabCompleter(this);
			root = true;
		} else if(pc.getExecutor() instanceof AtlasCommand) {
			plugin.getLogger().info("Successfully loaded " + this.getClass().getName() + " subcommand: /" + cmd);
			((AtlasCommand) pc.getExecutor()).extensions.add(this);
			
			//Recursive call to potentially register an alias as a command
			if(aliases.length > 1)
				registerCommand(aliases[0], Arrays.copyOfRange(aliases, 1, aliases.length), plugin);
		} else plugin.getLogger().warning("Command " + cmd + " has been registered by a non-Atlas command! Cannot register.");
	}
}
