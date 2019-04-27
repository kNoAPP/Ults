package com.kNoAPP.Ults.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.kNoAPP.Ults.aspects.Message;
import com.kNoAPP.atlas.commands.AtlasCommand;
import com.kNoAPP.atlas.commands.CommandInfo;
import com.kNoAPP.atlas.commands.Formation;
import com.kNoAPP.atlas.commands.Formation.FormationBuilder;

@CommandInfo(name = "ultimates", aliases = {"ults"}, description = "Show all commands from Ultimates", usage = "/ultimates", length = {0})
public class HelpCommand extends AtlasCommand {

	private static final Formation FORM = new FormationBuilder().build();

	@Override
	public boolean onCommand(Player sender, String[] args) {
		sender.sendMessage(Message.INFO.getMessage("Ultimates - By kNoAPP"));
		sender.sendMessage(ChatColor.DARK_GREEN + "------------------");
		sender.sendMessage(Message.HELP.getMessage("/ultimates - Show all commands from Ultimates"));
		sender.sendMessage(Message.HELP.getMessage("/recall (set | kill) - Recall to a location"));
		sender.sendMessage(Message.HELP.getMessage("/chunk (freeze | unfreeze) - Prevent a chunk from dying"));
		sender.sendMessage(Message.HELP.getMessage("/scramble <on | off> (radius) (ticks) - Screw JayJay05"));
		sender.sendMessage(Message.HELP.getMessage("/soundgen (on | off) - Generate custom sounds"));
		sender.sendMessage(Message.HELP.getMessage("/ult <kindred> - Cool particle effects"));
		return true;
	}
	
	@Override
	public boolean onCommand(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage(Message.INFO.getMessage("Ultimates - By kNoAPP"));
		sender.sendMessage(ChatColor.DARK_GREEN + "------------------");
		sender.sendMessage(Message.HELP.getMessage("/ultimates - Show all commands from Ultimates"));
		return true;
	}

	@Override
	protected Formation getFormation() {
		return FORM;
	}
}
