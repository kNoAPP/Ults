package com.kNoAPP.Ults.aspects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Ults.Ultimates;
import com.kNoAPP.Ults.utils.Tools;

public class Scramble {

	public static List<Scramble> active = new ArrayList<Scramble>();
	
	private Player p;
	private int radius;
	private long rate;
	
	public Scramble(Player p, int radius, long rate) {
		this.p = p;
		this.radius = radius;
		this.rate = rate;
		
		active.add(this);
		init();
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public long getRate() {
		return rate;
	}
	
	public void destroy() {
		active.remove(this);
	}
	
	private void init() {
		if(p != null && active.contains(this)) {
			List<Block> bl = Tools.getNearbyBlocks(p.getLocation(), radius);
			Block b1 = bl.get(Tools.randomNumber(0, bl.size() - 1));
			Block bt = b1;
			Block b2 = bl.get(Tools.randomNumber(0, bl.size() - 1));
			
			b1.setType(b2.getType());
			b1.setBlockData(b2.getBlockData());
			b1.setBiome(b2.getBiome());
			b1.getWorld().playEffect(b1.getLocation().clone().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, b2.getType());
			
			b2.setType(bt.getType());
			b2.setBlockData(bt.getBlockData());
			b2.setBiome(bt.getBiome());
			b2.getWorld().playEffect(b2.getLocation().clone().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, bt.getType());
			
			new BukkitRunnable() {
				public void run() {
					init();
				}
			}.runTaskLater(Ultimates.getPlugin(), rate);
		} else {
			destroy();
		}
	}
	
	public static Scramble getScramble(String p) {
		for(Scramble s : active) {
			if(s.getPlayer().getName().equals(p)) {
				return s;
			}
		}
		return null;
	}
}
