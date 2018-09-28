package com.kNoAPP.Ults.commands;

import org.bukkit.command.CommandSender;

public abstract class CommandHandler {

	protected boolean allowConsole;
	protected String usage, permission;
	protected int argMin;
	protected GenericType[] format;
	
	/**
	 * Creates a command handler.
	 * @param allowConsole - Allow the console to execute this command?
	 * @param usage - How should the command be used?
	 * @param permission - null if none needed
	 * @param argMin - How many args required (not including args[0])
	 * @param format - Use GenericType and specify args if any
	 */
	public CommandHandler(boolean allowConsole, String usage, String permission, int argMin, GenericType... format) {
		this.allowConsole = allowConsole;
		this.usage = usage;
		this.permission = permission;
		this.argMin = argMin;
		this.format = format;
	}
	
	/**
	 * Creates a command handler.
	 * @param allowConsole - Allow the console to execute this command?
	 * @param usage - How should the command be used?
	 * @param permission - null if none needed
	 * @param format - Use GenericType and specify args if any
	 */
	public CommandHandler(boolean allowConsole, String usage, String permission, GenericType... format) {
		this(allowConsole, usage, permission, format.length, format);
	}
	
	public boolean validArgs(String[] args) {
		if(args.length < argMin) return false;
		for(int i=0; i<format.length && i<args.length; i++) {
			if(format[i] == GenericType.INTEGER) {
				try {
					Integer.parseInt(args[i]);
				} catch(NumberFormatException e) { return false; }
			} else if(format[i] == GenericType.DOUBLE) {
				try {
					Double.parseDouble(args[i]);
				} catch(NumberFormatException e) { return false; }
			}
		}
		return true;
	}
	
	public boolean allowConsole() {
		return allowConsole;
	}
	
	public String getUsage() {
		return usage;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public boolean hasPermission(CommandSender sender) {
		return permission == null || sender.hasPermission(permission);
	}
	
	public int getMinimumArgs() {
		return argMin;
	}
	
	public GenericType[] getFormat() {
		return format;
	}
	
	public abstract boolean execute(CommandSender sender, String[] args);
	
	public static enum GenericType {
		STRING, INTEGER, DOUBLE;
	}
}
