package com.kNoAPP.Ults.commands;

import org.bukkit.entity.Player;

import com.kNoAPP.Ults.events.Kindred;
import com.kNoAPP.Ults.utils.Tools;
import com.kNoAPP.atlas.commands.AtlasCommand;
import com.kNoAPP.atlas.commands.CommandInfo;
import com.kNoAPP.atlas.commands.Formation;
import com.kNoAPP.atlas.commands.Formation.FormationBuilder;

@CommandInfo(name = "ult", description = "Cool particle effects", usage = "/ult <kindred>", length = {1})
public class UltimateCommand extends AtlasCommand {
	
	private static final Formation FORM = new FormationBuilder().list("kindred").build();

	@Override
	public boolean onCommand(Player sender, String[] args) {
		switch(args.length) {
		case 1:
			if(args[0].equalsIgnoreCase("kindred")) 
				new Kindred(Tools.floor(Tools.getTargetBlock(sender, 5)).getLocation());
			return true;
		}
		return true;
	}

	@Override
	protected Formation getFormation() {
		return FORM;
	}
}
