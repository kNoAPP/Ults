package com.knoban.ultimates.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.knoban.ultimates.aspects.Message;
import com.knoban.atlas.commands.AtlasCommand;
import com.knoban.atlas.commands.CommandInfo;
import com.knoban.atlas.commands.Formation;
import com.knoban.atlas.commands.Formation.FormationBuilder;

@CommandInfo(name = "ultimates", aliases = {"ults"}, description = "Show all commands from Ultimates", usage = "/ultimates", length = {0})
public class HelpCommand extends AtlasCommand {

	private static final Formation FORM = new FormationBuilder().build();

	@Override
	public boolean onCommand(Player sender, String label, String[] args) {
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
	public boolean onCommand(ConsoleCommandSender sender, String label, String[] args) {
		sender.sendMessage(Message.INFO.getMessage("Ultimates - By kNoAPP"));
		sender.sendMessage(ChatColor.DARK_GREEN + "------------------");
		sender.sendMessage(Message.HELP.getMessage("/ultimates - Show all commands from Ultimates"));
		return true;
	}

	@Override
	protected Formation getFormation(CommandSender sender) {
		return FORM;
	}
}
