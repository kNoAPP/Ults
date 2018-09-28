package com.kNoAPP.Ults.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kNoAPP.Ults.aspects.Message;
import com.kNoAPP.Ults.aspects.Scramble;

public class ScrambleCommand extends CommandHandler {
	
	public ScrambleCommand(boolean allowConsole, String usage, String permission, int argMin, GenericType... format) {
		super(allowConsole, usage, permission, argMin, format);
	}

	public ScrambleCommand(boolean allowConsole, String usage, String permission, GenericType... format) {
		super(allowConsole, usage, permission, format);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if(p.getName().equals("JayJay05")) p.sendMessage(Message.SCRAMBLE.getMessage("Screw off Jay. >:("));
			
		switch(args.length) {
		case 1:
			if(args[0].equalsIgnoreCase("off")) {
				Scramble s = Scramble.getScramble(p.getName());
				if(s != null) {
					s.destroy();
					p.sendMessage(Message.SCRAMBLE.getMessage("Scramble off."));
				} else p.sendMessage(Message.SCRAMBLE.getMessage("No scramble engaged!"));
				return true;
			}
			return false;
		case 3:
			if(args[0].equalsIgnoreCase("on") && Scramble.getScramble(p.getName()) == null) {
				int radius = Integer.parseInt(args[1]);
				long rate = Long.parseLong(args[2]);
				if(rate == 0) rate = 1L;
				new Scramble(p, radius, rate);
				p.sendMessage(Message.SCRAMBLE.getMessage("Scramble on with settings <" + radius + ", " + rate + ">."));
			} else p.sendMessage(Message.SCRAMBLE.getMessage("Scramble already active."));
			return true;
		}
		return false;
	}
}
