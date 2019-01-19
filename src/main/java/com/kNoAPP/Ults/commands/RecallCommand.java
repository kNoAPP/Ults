package com.kNoAPP.Ults.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Ults.Ultimates;
import com.kNoAPP.Ults.aspects.AFK;
import com.kNoAPP.Ults.aspects.Message;
import com.kNoAPP.Ults.data.Data;
import com.kNoAPP.Ults.utils.Tools;

public class RecallCommand extends CommandHandler {
	
	private static List<String> recalls = new ArrayList<String>();
	
	public RecallCommand(boolean allowConsole, String usage, String permission, int argMin, GenericType... format) {
		super(allowConsole, usage, permission, argMin, format);
	}

	public RecallCommand(boolean allowConsole, String usage, String permission, GenericType... format) {
		super(allowConsole, usage, permission, format);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		Location l = (Location) Data.CONFIG.getCachedYML().get("Player." + p.getUniqueId() + ".Recall");
		
		switch(args.length) {
		case 0:
			if(l != null) {
				if(AFK.getAFKs().get(p.getUniqueId()) <= 0) p.sendMessage(Message.RECALL.getMessage("You cannot recall while AFK."));
				else if(!recalls.contains(p.getName())) recall(p, l);
				else p.sendMessage(Message.RECALL.getMessage("Already recalling..."));
				return true;
			} else p.sendMessage(Message.RECALL.getMessage("You don't have recall location set."));
			return false;
		case 1:
			if(args[0].equalsIgnoreCase("set")) {
				p.sendMessage(Message.RECALL.getMessage("Your location has been saved."));
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 1F);
				FileConfiguration fc = Data.CONFIG.getCachedYML();
				fc.set("Player." + p.getUniqueId() + ".Recall", p.getLocation());
				Data.CONFIG.saveYML(fc);
				return true;
			} else if(args[0].equalsIgnoreCase("kill")) {
				p.sendMessage(Message.RECALL.getMessage("Your location has been removed."));
				p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 2F, 1F);
				FileConfiguration fc = Data.CONFIG.getCachedYML();
				fc.set("Player." + p.getUniqueId() + ".Recall", null);
				Data.CONFIG.saveYML(fc);
				return true;
			}
			return false;
		}
		return false;
	}
	
	private void recall(Player p, Location l) {
		recalls.add(p.getName());
		
		new BukkitRunnable() {
			int t = 0;
			public void run() {
				if(recalls.contains(p.getName())) {
					double tl = 8 - ((double)t/20);
					
					Tools.sendActionbar(p, Tools.generateWaitBar((((double)t/20)/8) * 100, 20, ChatColor.GOLD, ChatColor.GRAY) + " " + ChatColor.GREEN + Tools.round(tl, 1) + "s");
					p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, p.getLocation().clone().add(0, 0.5, 0), 1, 0.3F, 0.1F, 0.3F, 0.01);
					if(t%20 == 0) {
						p.playSound(p.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1F, 1F);
					}
					if(tl <= 0) {
						Entity m = p.getVehicle();
						List<Entity> passs = p.getPassengers();
						p.teleport(l);
						if(m != null) {
							m.teleport(l);
							m.addPassenger(p);
						}
						for(Entity pass : passs) {
							if(pass != null) {
								pass.teleport(l);
								p.addPassenger(pass);
							}
						}
						p.playSound(p.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1F, 1F);
						
						//Builds Particles
						for(double phi=0; phi<=Math.PI; phi+=Math.PI/15) {
							for(double theta=0; theta<=2*Math.PI; theta+=Math.PI/30) {
			    				double r = 1.5;
			    				double x = r*Math.cos(theta)*Math.sin(phi);
			    				double y = r*Math.cos(phi) + 1.5;
			    				double z = r*Math.sin(theta)*Math.sin(phi);
			    			
			    				l.add(x,y,z);
			    				p.getWorld().spawnParticle(Particle.DRIP_WATER, l, 1, 0F, 0F, 0F, 0.001);
			    				l.subtract(x, y, z);
			    			}
						}
						recalls.remove(p.getName());
						this.cancel();
					} else {
						t++;
					}
				} else {
					Tools.sendActionbar(p, ChatColor.RED + "Teleport Cancelled!");
					p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1F, 1F);
					this.cancel();
				}
			}
		}.runTaskTimer(Ultimates.getPlugin(), 0L, 1L);
	}
	
	public static void cancel(String name) {
		recalls.remove(name);
	}
}