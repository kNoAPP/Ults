package com.kNoAPP.Ults.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Ults.Ultimates;
import com.kNoAPP.Ults.aspects.Message;
import com.kNoAPP.Ults.events.Kindred;
import com.kNoAPP.Ults.utils.Tools;

public class UltCmds implements CommandExecutor {
	
	private static List<UUID> soundgen = new ArrayList<UUID>();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			final Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("ults")) {
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("kindred")) {
						new Kindred(Tools.floor(Tools.getTargetBlock(p, 5)).getLocation());
					}
				}
			}
			if(cmd.getName().equalsIgnoreCase("soundgen")) {
				if(args.length == 0) {
					p.sendMessage(Message.SOUNDGEN.getMessage("Flushing sounds..."));
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound " + p.getName());
				}
				if(args.length == 1) {
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
					}
					if(args[0].equalsIgnoreCase("off")) {
						soundgen.remove(p.getUniqueId());
						p.sendMessage(Message.SOUNDGEN.getMessage("Off..."));
					}
				}
			}
			/*
			if(cmd.getName().equalsIgnoreCase("snball")) {
				if(args.length == 1) {
					Object[] isDouble = Tools.canParseToDouble(args[0]);
					if((boolean)isDouble[0]) {
						double power = (double) isDouble[1];
						p.getInventory().addItem(Items.getSnowballGun(power));
					}
				}
			}
			*/
		}
		return false;
	}
}
