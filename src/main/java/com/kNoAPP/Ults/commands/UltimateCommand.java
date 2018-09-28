package com.kNoAPP.Ults.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kNoAPP.Ults.events.Kindred;
import com.kNoAPP.Ults.utils.Tools;

public class UltimateCommand extends CommandHandler {
	
	public UltimateCommand(boolean allowConsole, String usage, String permission, int argMin, GenericType... format) {
		super(allowConsole, usage, permission, argMin, format);
	}

	public UltimateCommand(boolean allowConsole, String usage, String permission, GenericType... format) {
		super(allowConsole, usage, permission, format);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		switch(args.length) {
		case 1:
			if(args[0].equalsIgnoreCase("kindred")) new Kindred(Tools.floor(Tools.getTargetBlock(p, 5)).getLocation());
			return true;
		}
		return false;
	}
}
