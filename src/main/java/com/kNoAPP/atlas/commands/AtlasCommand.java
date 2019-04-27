package com.kNoAPP.atlas.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public abstract class AtlasCommand implements TabExecutor {

	private static final String NO_PERMISSION = ChatColor.GOLD + "Permission> " + ChatColor.GRAY + "You are missing Node [" + ChatColor.DARK_AQUA + "%perm%" + ChatColor.GRAY + "]!";
	private static final String USAGE = ChatColor.GOLD + "Usage> %usage%";
	
	private CommandInfo info = getClass().getAnnotation(CommandInfo.class);

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(!info.permission().equals("") && !sender.hasPermission(info.permission()))
			return null;
		
		Formation form = getFormation();
		int type = form.getArgType(args.length - 1);
		switch(type) {
		case Formation.PLAYER:
			return sender instanceof Player ? form.getPlayer((Player) sender) : form.getPlayer();
		case Formation.NUMBER:
			return form.getNumber(args.length - 1);
		case Formation.LIST:
			return form.getList(args.length - 1);
		default:
			return null;
		}
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
	
	protected boolean onCommand(Player sender, String[] args) { return true; }
	protected boolean onCommand(ConsoleCommandSender sender, String[] args) { return true; }
	
	protected abstract Formation getFormation();
	
	public CommandInfo getInfo() {
		return info;
	}
}
