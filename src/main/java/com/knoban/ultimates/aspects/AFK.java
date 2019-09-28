package com.knoban.ultimates.aspects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.knoban.ultimates.Ultimates;

public class AFK {
	
	public static Team collision;
	private static HashMap<UUID, Integer> afk = new HashMap<UUID, Integer>();
	private static HashSet<UUID> afkWarning = new HashSet<UUID>();
	
	public static final int AFK_ACTIVATION_TIME = 45;
	
	public static void removeAfk(Player p) {
		p.setInvulnerable(false);
		p.setFallDistance(0F);
		collision.removeEntry(p.getName());
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, 1F, 0.8F);
		p.removePotionEffect(PotionEffectType.GLOWING);
		p.sendMessage(Message.INFO.getMessage("You are no longer AFK."));
	}
	
	public static void afkLoop() {
		for(Player pl : Bukkit.getOnlinePlayers()) AFK.getAFKs().put(pl.getUniqueId(), AFK.AFK_ACTIVATION_TIME);
		
		Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
		collision = sb.getTeam("no_collisions") != null ? sb.getTeam("no_collisions") : sb.registerNewTeam("no_collisions");
		collision.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
		
		new BukkitRunnable() {
			public void run() {
				for(UUID uuid : afk.keySet()) {
					Player p = Bukkit.getPlayer(uuid);
					if(p.getGameMode() != GameMode.SURVIVAL || p.getVehicle() != null) continue;
					int afkTime = afk.get(uuid) - 1;
					if(afkTime == 0) {
						p.sendMessage(Message.INFO.getMessage("You are now AFK and cannot be damaged."));
						p.setInvulnerable(true);
						collision.addEntry(p.getName());
						p.getWorld().playSound(p.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1F, 0.8F);
						p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 1));
						afk.put(uuid, afkTime);
					} else if(afkTime == -1) {
						p.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, p.getLocation().clone().add(0, 0.8, 0), 4, 0.3, 0.3, 0.3, 0.01);
						p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CAT_PURR, 0.05F, 1.2F);
					} else afk.put(uuid, afkTime);
				}
			}
		}.runTaskTimer(Ultimates.getPlugin(), 0L, 20L);
	}
	
	public static HashMap<UUID, Integer> getAFKs() {
		return afk;
	}
	
	public static HashSet<UUID> getAFKWarnings() {
		return afkWarning;
	}

}
