package com.kNoAPP.Ults.commands;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Ults.Ultimates;
import com.kNoAPP.Ults.aspects.AFK;
import com.kNoAPP.Ults.aspects.Message;
import com.kNoAPP.Ults.utils.Tools;
import com.kNoAPP.atlas.commands.AtlasCommand;
import com.kNoAPP.atlas.commands.CommandInfo;
import com.kNoAPP.atlas.commands.Formation;
import com.kNoAPP.atlas.commands.Formation.FormationBuilder;

@CommandInfo(name = "recall", description = "Recall to a previously set location", usage = "/recall (set | kill)", length = {0, 1})
public class RecallCommand extends AtlasCommand {
	
	private static final Formation FORM = new FormationBuilder().list("set", "kill").build();
	
	private HashSet<UUID> recalls = new HashSet<UUID>();

	@Override
	public boolean onCommand(Player sender, String label, String[] args) {
		Location l = (Location) Ultimates.CONFIG.getCachedYML().get("Player." + sender.getUniqueId() + ".Recall");
		
		switch(args.length) {
		case 0:
			if(l != null) {
				if(AFK.getAFKs().get(sender.getUniqueId()) <= 0) sender.sendMessage(Message.RECALL.getMessage("You cannot recall while AFK."));
				else if(!recalls.contains(sender.getUniqueId())) recall(sender, l);
				else sender.sendMessage(Message.RECALL.getMessage("Already recalling..."));
			} else sender.sendMessage(Message.RECALL.getMessage("You don't have recall location set."));
			return true;
		case 1:
			if(args[0].equalsIgnoreCase("set")) {
				sender.sendMessage(Message.RECALL.getMessage("Your location has been saved."));
				sender.playSound(sender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 1F);
				FileConfiguration fc = Ultimates.CONFIG.getCachedYML();
				fc.set("Player." + sender.getUniqueId() + ".Recall", sender.getLocation());
				Ultimates.CONFIG.saveYML(fc);
			} else if(args[0].equalsIgnoreCase("kill")) {
				sender.sendMessage(Message.RECALL.getMessage("Your location has been removed."));
				sender.playSound(sender.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 2F, 1F);
				FileConfiguration fc = Ultimates.CONFIG.getCachedYML();
				fc.set("Player." + sender.getUniqueId() + ".Recall", null);
				Ultimates.CONFIG.saveYML(fc);
			}
			return true;
		}
		return true;
	}
	
	private void recall(Player p, Location l) {
		recalls.add(p.getUniqueId());
		
		new BukkitRunnable() {
			int t = 0;
			public void run() {
				if(recalls.contains(p.getUniqueId())) {
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
						recalls.remove(p.getUniqueId());
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
	
	public void cancel(UUID uuid) {
		recalls.remove(uuid);
	}

	@Override
	protected Formation getFormation(CommandSender sender) {
		return FORM;
	}
	
	@EventHandler
	public void onleave(PlayerQuitEvent e) {
		cancel(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			cancel(p.getUniqueId());
		}
	}
	
	@EventHandler
	public void move(PlayerMoveEvent e) {
		if(e.getTo().distance(e.getFrom()) > 0.05)
			cancel(e.getPlayer().getUniqueId());
	}
}