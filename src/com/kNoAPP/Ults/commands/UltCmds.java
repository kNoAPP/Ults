package com.kNoAPP.Ults.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kNoAPP.Ults.events.Kindred;
import com.kNoAPP.Ults.utils.Tools;

public class UltCmds implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("ults")) {
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("kindred")) {
						new Kindred(Tools.floor(Tools.getTargetBlock(p, 5)).getLocation());
					}
				}
			}
		}
		return false;
	}
}
