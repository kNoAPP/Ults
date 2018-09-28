package com.kNoAPP.Ults.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class HelpCommand extends CommandHandler {

	public HelpCommand(boolean allowConsole, String usage, String permission, int argMin, GenericType... format) {
		super(allowConsole, usage, permission, argMin, format);
	}

	public HelpCommand(boolean allowConsole, String usage, String permission, GenericType... format) {
		super(allowConsole, usage, permission, format);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Bukkit.dispatchCommand(sender, "ult");
		return true;
	}
}
