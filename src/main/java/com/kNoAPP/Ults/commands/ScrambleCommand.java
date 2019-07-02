package com.kNoAPP.Ults.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.kNoAPP.Ults.Ultimates;
import com.kNoAPP.Ults.aspects.Message;
import com.kNoAPP.Ults.aspects.Scramble;
import com.kNoAPP.atlas.commands.AtlasCommand;
import com.kNoAPP.atlas.commands.CommandInfo;
import com.kNoAPP.atlas.commands.Formation;
import com.kNoAPP.atlas.commands.Formation.FormationBuilder;

@CommandInfo(name = "scramble", description = "Screw JayJay05", usage = "/scramble <on | off> (radius) (ticks)", length = {1, 3})
public class ScrambleCommand extends AtlasCommand {
	
	private static final Formation FORM = new FormationBuilder().list("on", "off").number(5, 20, 5).number(2, 10, 2).build();

	@Override
	public boolean onCommand(Player sender, String label, String[] args) {
		FileConfiguration config = Ultimates.CONFIG.getCachedYML();
		if(!config.getBoolean("Player." + sender.getUniqueId() + ".Scramble")) {
			sender.sendMessage(Message.SCRAMBLE.getMessage("Permission is now required due to randos joining and griefing."));
			return true;
		}
		
		switch(args.length) {
		case 1:
			if(args[0].equalsIgnoreCase("off")) {
				Scramble s = Scramble.getScramble(sender.getUniqueId());
				if(s != null) {
					s.destroy();
					sender.sendMessage(Message.SCRAMBLE.getMessage("Scramble off."));
				} else sender.sendMessage(Message.SCRAMBLE.getMessage("No scramble engaged!"));
			}
			return true;
		case 3:
			if(args[0].equalsIgnoreCase("on") && Scramble.getScramble(sender.getUniqueId()) == null) {
				int radius = (int) Double.parseDouble(args[1]);
				long rate = (long) Double.parseDouble(args[2]);
				if(rate == 0) rate = 1L;
				new Scramble(sender, radius, rate);
				sender.sendMessage(Message.SCRAMBLE.getMessage("Scramble on with settings <" + radius + ", " + rate + ">."));
			} else sender.sendMessage(Message.SCRAMBLE.getMessage("Scramble already active."));
		}
		return true;
	}

	@Override
	protected Formation getFormation(CommandSender sender) {
		return FORM;
	}
}
