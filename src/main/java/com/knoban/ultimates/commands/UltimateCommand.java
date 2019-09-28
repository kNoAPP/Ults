package com.knoban.ultimates.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.knoban.ultimates.events.Kindred;
import com.knoban.ultimates.utils.Tools;
import com.knoban.atlas.commands.AtlasCommand;
import com.knoban.atlas.commands.CommandInfo;
import com.knoban.atlas.commands.Formation;
import com.knoban.atlas.commands.Formation.FormationBuilder;

@CommandInfo(name = "ult", description = "Cool particle effects", usage = "/ult <kindred>", length = {1})
public class UltimateCommand extends AtlasCommand {
	
	private static final Formation FORM = new FormationBuilder().list("kindred").build();

	@Override
	public boolean onCommand(Player sender, String label, String[] args) {
		switch(args.length) {
		case 1:
			if(args[0].equalsIgnoreCase("kindred")) 
				new Kindred(Tools.floor(Tools.getTargetBlock(sender, 5)).getLocation());
			return true;
		}
		return true;
	}

	@Override
	protected Formation getFormation(CommandSender sender) {
		return FORM;
	}
}
