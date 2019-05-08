package com.kNoAPP.Ults.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Ults.Ultimates;
import com.kNoAPP.Ults.aspects.Message;
import com.kNoAPP.Ults.utils.Tools;
import com.kNoAPP.atlas.commands.AtlasCommand;
import com.kNoAPP.atlas.commands.CommandInfo;
import com.kNoAPP.atlas.commands.Formation;
import com.kNoAPP.atlas.commands.Formation.FormationBuilder;

@CommandInfo(name = "soundgen", aliases = {"sg"}, description = "Generate custom sounds", usage = "/soundgen (on | off)", length = {0, 1})
public class SoundGenCommand extends AtlasCommand {
	
	private static final Formation FORM = new FormationBuilder().list("on", "off").build();
	private List<UUID> soundgen = new ArrayList<UUID>();

	@Override
	public boolean onCommand(Player sender, String label, String[] args) {
		switch(args.length) {
		case 0:
			sender.sendMessage(Message.SOUNDGEN.getMessage("Flushing sounds..."));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound " + sender.getName());
			return true;
		case 1:
			if(args[0].equalsIgnoreCase("on")) {
				if(!soundgen.contains(sender.getUniqueId())) {
					soundgen.add(sender.getUniqueId());
					sender.sendMessage(Message.SOUNDGEN.getMessage("On..."));
					new BukkitRunnable() {
						public void run() {
							if(sender != null && soundgen.contains(sender.getUniqueId()) && sender.isOnline()) {
								Sound s = Sound.values()[Tools.randomNumber(0, Sound.values().length-1)];
								while(s.name().contains("RECORD")) s = Sound.values()[Tools.randomNumber(0, Sound.values().length-1)];
								float pitch = (float) Tools.randomNumber(0.5, 2.0);
								
								sender.playSound(sender.getLocation(), s, 1F, pitch);
								sender.sendMessage(Message.SOUNDGEN.getMessage(s.name() + " - " + pitch));
							} else {
								soundgen.remove(sender.getUniqueId());
								this.cancel();
							}
						}
					}.runTaskTimer(Ultimates.getPlugin(), 20L, 30L);
				} else sender.sendMessage(Message.SOUNDGEN.getMessage("On already..."));
			} else if(args[0].equalsIgnoreCase("off")) {
				soundgen.remove(sender.getUniqueId());
				sender.sendMessage(Message.SOUNDGEN.getMessage("Off..."));
			}
		}
		return true;
	}

	@Override
	protected Formation getFormation(CommandSender sender) {
		return FORM;
	}
}
