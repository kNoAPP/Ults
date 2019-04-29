package com.kNoAPP.atlas.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EpicBorder {
	
	private Plugin pl;
	private Location center;
	private List<Player> players;
	private double curSize, toSize, rate;
	private boolean remove;
	
	public EpicBorder(Plugin pl, Location center, Player... players) {
		this.pl = pl;
		this.center = center;
		for(Player p : players) 
			this.players.add(p);
		this.curSize = 10;
		this.toSize = 10;
		this.rate = 0;
		this.remove = false;
		
		clock();
	}
	
	/**
	 * Designed specifically for OnTheRun
	 */
	private void clock() {
		new BukkitRunnable() {
			int a = 0;
			public void run() {
				if(a < Integer.MAX_VALUE) a++;
				else a = 0;
				
				if(a%4==0 && curSize != toSize) curSize += rate;
				
				World w = center.getWorld();
				int max = w.getMaxHeight();
				int count = (int)((double)max*0.45);
				
				for(Player pl : players) {
					double distanceToBorder = distanceToBorder(pl.getLocation());
					
					if(a%8==0 && -10 <= distanceToBorder && distanceToBorder <= 10) pl.playSound(pl.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 0.5F, 0.1F); 
					if(a%2==0 && distanceToBorder < 30) {
						if(distanceToFrontZ(pl.getLocation()) < 30) {
							for(double x=getBackX(); x<=getFrontX(); x=x+2) {
								for(int i=0; i<count; i++) {
									Location b = new Location(w, x, Tools.randomNumber(0, max), getFrontZ());
									if(pl.getLocation().distance(b) < 30) pl.spawnParticle(Particle.FLAME, b, 1, 0F, 0F, 0F, 0.05);
								}
							}
						}
						if(distanceToBackZ(pl.getLocation()) < 30) {
							for(double x=getBackX(); x<=getFrontX(); x=x+2) {
								for(int i=0; i<count; i++) {
									Location a = new Location(w, x, Tools.randomNumber(0, max), getBackZ());
									if(pl.getLocation().distance(a) < 30) pl.spawnParticle(Particle.FLAME, a, 1, 0F, 0F, 0F, 0.05);
								}
							}
						}
						if(distanceToFrontX(pl.getLocation()) < 30) {
							for(double z=getBackZ(); z<=getFrontZ(); z=z+2) {
								for(int i=0; i<count; i++) {
									Location b = new Location(w, getFrontX(), Tools.randomNumber(0, max), z);
									if(pl.getLocation().distance(b) < 30) pl.spawnParticle(Particle.FLAME, b, 1, 0F, 0F, 0F, 0.05);
								}
							}
						}
						if(distanceToBackX(pl.getLocation()) < 30) {
							for(double z=getBackZ(); z<=getFrontZ(); z=z+2) {
								for(int i=0; i<count; i++) {
									Location a = new Location(w, getBackX(), Tools.randomNumber(0, max), z);
									if(pl.getLocation().distance(a) < 30) pl.spawnParticle(Particle.FLAME, a, 1, 0F, 0F, 0F, 0.05);
								}
							}
						}
					}
					
					/*
					//MAX: distanceToBorder -> curSize = 6000+0 = 6000 (midday)
					//MID: distanceToBorder -> 0 = 6000+6000 = 12000 (sunset)
					//MIN: distanceToBorder -> -20 = 6000+12000 = 18000 = (midnight)
					if(distanceToBorder < 40) p.setPlayerTime((long)(distanceToBorder > 0 ? (10000+(3000*(40.0-distanceToBorder)/40.0)) : 13000), false);
					else if(!p.isPlayerTimeRelative()) p.resetPlayerTime();
					if(distanceToBorder <= 0 && p.getPlayerWeather() != WeatherType.DOWNFALL) {
						p.setPlayerWeather(WeatherType.DOWNFALL);
						p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 5F, 0.5F);
						if(!pd.isSpectating()) p.sendMessage(Message.OTR.getMessage(ChatColor.RED + "You're out of bounds! Run!"));
					}
					if(distanceToBorder > 0 && p.getPlayerWeather() != WeatherType.CLEAR) p.setPlayerWeather(WeatherType.CLEAR);
					if(a%16==0 && !pd.isSpectating() && !isInside(p.getLocation())) {
						p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 1F, 1F);
						p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation().clone().add(0, 0.5, 0), 4, 0.25F, 0.25F, 0.25F, 0.2);
						p.damage(2+(-0.1*distanceToBorder));
						if(a%64==0) p.sendMessage(Message.OTR.getMessage(ChatColor.RED + "You're out of bounds! Run!"));
					}
					*/
				}
				
				if(remove) this.cancel();
			}
		}.runTaskTimer(pl, 0L, 5L);
	}
	
	public Location getCenter() {
		return center;
	}
	
	public void setCenter(Location center) {
		this.center = center;
	}
	
	public boolean isInside(Location l) {
		return l.getWorld() != null && center.getWorld() != null &&
    			l.getWorld().getName().equals(center.getWorld().getName()) &&
    			getBackX() <= l.getX() && l.getX() <= getFrontX() &&
    			getBackZ() <= l.getZ() && l.getZ() <= getFrontZ();
	}
	
	/*
	 * Not accurate for outside corners.
	 */
	public double distanceToBorder(Location l) {
		if(l.getWorld() != null && center.getWorld() != null &&
    			l.getWorld().getName().equals(center.getWorld().getName())) {
			return Math.min(
					Math.min(l.getX()-getBackX(), getFrontX()-l.getX()), 
					Math.min(l.getZ()-getBackZ(), getFrontZ()-l.getZ()));
		}
		return 500;
	}
	
	public double getFrontX() {
		return center.getX() + (curSize/2.0);
	}
	
	public double distanceToFrontX(Location l) {
		if(l.getWorld() != null && center.getWorld() != null &&
    			l.getWorld().getName().equals(center.getWorld().getName())) {
			return getFrontX()-l.getX();
		}
		return 500;
	}
	
	public double getBackX() {
		return center.getX() - (curSize/2.0);
	}
	
	public double distanceToBackX(Location l) {
		if(l.getWorld() != null && center.getWorld() != null &&
    			l.getWorld().getName().equals(center.getWorld().getName())) {
			return l.getX()-getBackX();
		}
		return 500;
	}
	
	public double getFrontZ() {
		return center.getZ() + (curSize/2.0);
	}
	
	public double distanceToFrontZ(Location l) {
		if(l.getWorld() != null && center.getWorld() != null &&
    			l.getWorld().getName().equals(center.getWorld().getName())) {
			return getFrontZ()-l.getZ();
		}
		return 500;
	}
	
	public double getBackZ() {
		return center.getZ() - (curSize/2.0);
	}
	
	public double distanceToBackZ(Location l) {
		if(l.getWorld() != null && center.getWorld() != null &&
    			l.getWorld().getName().equals(center.getWorld().getName())) {
			return l.getZ()-getBackZ();
		}
		return 500;
	}
	
	public double getRate() {
		return rate;
	}
	
	public double getSize() {
		return curSize;
	}
	
	public void setSize(double curSize) {
		this.curSize = curSize;
		this.toSize = curSize;
		this.rate = 0;
	}
	
	public void setSize(double toSize, double sec) {
		this.toSize = toSize;
		this.rate = (toSize-curSize)/sec;
	}
	
	public void remove() {
		remove = true;
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.resetPlayerTime();
			p.resetPlayerWeather();
		}
	}
}
