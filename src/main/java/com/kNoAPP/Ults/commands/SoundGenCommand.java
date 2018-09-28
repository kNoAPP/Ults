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

public class SoundGenCommand extends CommandHandler {
	
	private static List<UUID> soundgen = new ArrayList<UUID>();

	public SoundGenCommand(boolean allowConsole, String usage, String permission, int argMin, GenericType... format) {
		super(allowConsole, usage, permission, argMin, format);
	}

	public SoundGenCommand(boolean allowConsole, String usage, String permission, GenericType... format) {
		super(allowConsole, usage, permission, format);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		switch(args.length) {
		case 0:
			p.sendMessage(Message.SOUNDGEN.getMessage("Flushing sounds..."));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound " + p.getName());
			return true;
		case 1:
			if(args[0].equalsIgnoreCase("on")) {
				if(!soundgen.contains(p.getUniqueId())) {
					soundgen.add(p.getUniqueId());
					p.sendMessage(Message.SOUNDGEN.getMessage("On..."));
					new BukkitRunnable() {
						public void run() {
							if(p != null && soundgen.contains(p.getUniqueId()) && p.isOnline()) {
								Sound s = Sound.values()[Tools.randomNumber(0, Sound.values().length-1)];
								while(s.name().contains("RECORD")) s = Sound.values()[Tools.randomNumber(0, Sound.values().length-1)];
								float pitch = (float) Tools.randomNumber(0.5, 2.0);
								
								p.playSound(p.getLocation(), s, 1F, pitch);
								p.sendMessage(Message.SOUNDGEN.getMessage(s.name() + " - " + pitch));
							} else {
								soundgen.remove(p.getUniqueId());
								this.cancel();
							}
						}
					}.runTaskTimer(Ultimates.getPlugin(), 20L, 30L);
				} else p.sendMessage(Message.SOUNDGEN.getMessage("On already..."));
				return true;
			} else if(args[0].equalsIgnoreCase("off")) {
				soundgen.remove(p.getUniqueId());
				p.sendMessage(Message.SOUNDGEN.getMessage("Off..."));
				return true;
			}
		}
		return false;
	}
}
