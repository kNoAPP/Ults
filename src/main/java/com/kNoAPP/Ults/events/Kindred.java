package com.kNoAPP.Ults.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import com.kNoAPP.Ults.Ultimates;

public class Kindred {

	private Location loc;
	private World w;
	
	private int ticks;
	
	private ArmorStand as;
	
	public Kindred(Location loc) {
		this.loc = loc;
		w = loc.getWorld();
		
		ticks = 0;
		
		init();
	}
	
	private void init() {
		if(ticks == 0) {
			as = w.spawn(loc.clone().add(0, 5, 0), ArmorStand.class);
			as.setVisible(false);
			as.setGravity(false);
			as.setHelmet(new ItemStack(Material.QUARTZ_BLOCK));
			as.setHeadPose(new EulerAngle(0, 0, 0));
			
			w.playSound(as.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1F, 0.7F);
			w.playSound(as.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1F, 1.4F);
			w.playSound(as.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 5F, 1.5F);
			
			w.spawnParticle(Particle.FLAME, as.getLocation().clone().add(0, 1.75, 0), 20, 1F, 0.5F, 0.5F, 0.5);
		}
		if(ticks <= 10) {
			as.teleport(as.getLocation().clone().add(0, -0.5, 0));
			as.setHeadPose(as.getHeadPose().add(0, 0, 0.4));
			
			w.spawnParticle(Particle.FIREWORKS_SPARK, as.getLocation().clone().add(0, 1.9, 0), 3, 0.1F, 0.1F, 0.1F, 0.1);
		}
		if(ticks == 10) {
			as.remove();
			w.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1F, 0.2F);
		}
		if(10 <= ticks && ticks <= 90) {
			/*
			for(float x=-1F; x<1F; x=x+0.08F) {
				for(float z=-1F; z<1F; z=z+0.08F) {
					w.spawnParticle(Particle.CLOUD, loc.clone().add(0, 1, 0), 0, x, 0F, z, 0.4);
				}
			}
			*/
			for(double x=0; x<2*Math.PI; x+=Math.PI/24) {
				w.spawnParticle(Particle.CLOUD, loc.clone().add(0, 1, 0), 0, Math.cos(x), 0F, Math.sin(x), 0.4);
			}
		}
		
		ticks++;
		new BukkitRunnable() {
			public void run() {
				init();
			}
		}.runTaskLater(Ultimates.getPlugin(), 1L);
	}
}
