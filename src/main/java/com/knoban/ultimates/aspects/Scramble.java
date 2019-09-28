package com.knoban.ultimates.aspects;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.knoban.ultimates.Ultimates;
import com.knoban.ultimates.utils.Tools;

public class Scramble {

	public static HashMap<UUID, Scramble> active = new HashMap<UUID, Scramble>();
	
	private UUID uuid;
	private int radius;
	private long rate;
	
	public Scramble(Player p, int radius, long rate) {
		this.uuid = p.getUniqueId();
		this.radius = radius;
		this.rate = rate;
		
		active.put(uuid, this);
		init();
	}
	
	public void destroy() {
		active.remove(uuid);
	}
	
	private void init() {
		Player p = Bukkit.getPlayer(uuid);
		if(p != null && active.containsKey(uuid)) {
			List<Block> bl = Tools.getNearbyBlocks(p.getLocation(), radius);
			Block b1 = bl.get(Tools.randomNumber(0, bl.size() - 1));
			Block b2 = bl.get(Tools.randomNumber(0, bl.size() - 1));
			
			Material t_type = b1.getType();
			BlockData t_bd = b1.getBlockData().clone();
			Biome t_b = b1.getBiome();
			
			b1.setType(b2.getType());
			b1.setBlockData(b2.getBlockData());
			b1.setBiome(b2.getBiome());
			b1.getWorld().playEffect(b1.getLocation().clone().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, b2.getType());
			
			b2.setType(t_type);
			b2.setBlockData(t_bd);
			b2.setBiome(t_b);
			b2.getWorld().playEffect(b2.getLocation().clone().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, t_type);
			
			new BukkitRunnable() {
				public void run() {
					init();
				}
			}.runTaskLater(Ultimates.getPlugin(), rate);
		} else {
			destroy();
		}
	}
	
	public static Scramble getScramble(UUID uuid) {
		return active.get(uuid);
	}
}
