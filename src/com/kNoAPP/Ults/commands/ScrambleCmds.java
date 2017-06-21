package com.kNoAPP.Ults.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kNoAPP.Ults.aspects.Message;
import com.kNoAPP.Ults.aspects.Scramble;

public class ScrambleCmds implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("scramble")) {
				if(!p.getName().equals("JayJay05")) {
					if(args.length < 1) {
						p.sendMessage(Message.USAGE.getMessage("/scramble <radius> <ticks>"));
						p.sendMessage(Message.USAGE.getMessage("/scramble off"));
						return false;
					}
					if(args.length == 1) {
						if(args[0].equalsIgnoreCase("off")) {
							Scramble s = Scramble.getScramble(p.getName());
							if(s != null) {
								s.destroy();
								p.sendMessage(Message.SCRAMBLE.getMessage("Scramble off."));
								return true;
							} else {
								p.sendMessage(Message.SCRAMBLE.getMessage("No scramble engaged!"));
								return false;
							}
						}
					}
					if(args.length == 2) {
						if(Scramble.getScramble(p.getName()) == null) {
							try {
								int radius = Integer.parseInt(args[0]);
								long rate = Long.parseLong(args[1]);
								if(rate == 0) rate = 1L;
								new Scramble(p, radius, rate);
								p.sendMessage(Message.SCRAMBLE.getMessage("Scramble on with settings <" + radius + ", " + rate + ">."));
								return true;
							} catch(Exception ex) {
								p.sendMessage(Message.USAGE.getMessage("/scramble <radius> <ticks>"));
								p.sendMessage(Message.USAGE.getMessage("/scramble off"));
								return false;
							}
						} else {
							p.sendMessage(Message.SCRAMBLE.getMessage("Scramble already active."));
							return false;
						}
					}
				} else {
					p.sendMessage(Message.SCRAMBLE.getMessage("Screw off Jay. >:("));
					return false;
				}
			}
		}
		return false;
	}
}
