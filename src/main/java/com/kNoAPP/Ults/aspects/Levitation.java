package com.kNoAPP.Ults.aspects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import com.kNoAPP.Ults.Ultimates;

@SuppressWarnings("deprecation")
public class Levitation {

	private static HashMap<UUID, Levitation> levitators = new HashMap<UUID, Levitation>();
	
	private Player p;
	private LivingEntity le;
	private ArmorStand as;
	private BlockData bd;
	private MaterialData md;
	private Material m;
	private int type;
	
	private static int BLOCK = 0;
	private static int ENTITY = 1;
	
	private boolean ready = false;
	private volatile boolean valid = true;
	
	public Levitation(Player p, Block b) {
		this.p = p;
		this.m = b.getType();
		this.bd = b.getBlockData();
		this.md = b.getState().getData();
		this.type = BLOCK;
		
		levitate();
	}
	
	public Levitation(Player p, LivingEntity le) {
		this.p = p;
		this.le = le;
		this.type = ENTITY;
		
		levitate();
	}
	
	private void levitate() {
		as = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().clone().add(p.getLocation().getDirection().normalize().multiply(p.getInventory().getHeldItemSlot()*1.5+1)), EntityType.ARMOR_STAND);
		as.setVisible(false);
		as.setInvulnerable(true);
		as.setGravity(false);
		
		if(type == BLOCK) {
			ItemStack is = new ItemStack(m);
			is.setData(md);
			as.setHelmet(is);
		} else as.addPassenger(le);
		
		as.setHeadPose(new EulerAngle(0, 0, 0));
		
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1F, 1.6F);
		
		levitators.put(p.getUniqueId(), this);
		
		new BukkitRunnable() {
			public void run() {
				ready = true;
				
				if(!valid) {
					this.cancel();
					return;
				}
				
				if(type == ENTITY && (le == null || !le.isValid() || le.isDead())) {
					drop();
					this.cancel();
					return;
				}
				
				if(type == ENTITY) {
					if(p.getVehicle() != null && p.getVehicle().equals(le)) le.removePassenger(p);
					if(as.getPassengers().contains(p)) as.removePassenger(p);
					as.removePassenger(le);
				}
				as.teleport(p.getLocation().clone().add(p.getLocation().getDirection().normalize().multiply(p.getInventory().getHeldItemSlot()*1.5+1)));
				if(type == ENTITY) as.addPassenger(le);
			}
		}.runTaskTimer(Ultimates.getPlugin(), 1L, 1L);
	}
	
	public void launch() {
		if(!ready) return;
		
		Entity e;
		if(type == BLOCK) {
			e = p.getWorld().spawnFallingBlock(as.getLocation().clone().add(0, 1, 0), bd);
			as.setHelmet(new ItemStack(Material.AIR));
		} else {
			e = le;
			as.removePassenger(le);
		}
		as.remove();
		as = null;
		
		e.setVelocity(p.getLocation().clone().getDirection().normalize().multiply((12-p.getInventory().getHeldItemSlot())/4));
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 0.6F);
		
		stop();
		
		new BukkitRunnable() {
			List<LivingEntity> hitEntities = new ArrayList<LivingEntity>(); 
			public void run() {
				if(e != null && e.isValid() && !e.isDead() && (!e.isOnGround() && e.getLocation().clone().add(0, -1, 0).getBlock().getType() != Material.WATER)) {
					for(Entity te : e.getNearbyEntities(1.5, 1.5, 1.5)) {
						if(te instanceof LivingEntity && e != te) {
							LivingEntity tle = (LivingEntity) te;
							if(hitEntities.contains(tle)) continue;
							else hitEntities.add(tle);
							
							tle.damage(e.getVelocity().clone().length() * 2.5);
							tle.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, tle.getLocation().clone().add(0, 0.8, 0), 1, 0.2F, 0.2F, 0.2F, 0.01);
							tle.getWorld().playSound(tle.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1F, 1F);
							try {
								tle.setVelocity(e.getVelocity().clone().normalize());
							} catch(IllegalArgumentException e) {} //Launching a player riding a horse
							if(p != null && p.isValid() && p.isOnline()) {
								EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(p, tle, DamageCause.MAGIC, e.getVelocity().clone().length() * 2.5);
								tle.setLastDamageCause(event);
								Bukkit.getServer().getPluginManager().callEvent(event);
							}
						}
					}
				} else this.cancel();
			}
		}.runTaskTimer(Ultimates.getPlugin(), 0L, 2L);
		
	}
	
	public void drop() {
		if(!ready) return;
		
		if(type == BLOCK) {
			p.getWorld().spawnFallingBlock(as.getLocation().clone().add(0, 1, 0), bd);
			as.setHelmet(new ItemStack(Material.AIR));
		} else if(le != null && le.isValid() && !le.isDead()) as.removePassenger(le);
			
		as.remove();
		as = null;
		
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT_LAND, 1F, 1.4F);
		
		stop();
	}
	
	private void stop() {
		levitators.remove(p.getUniqueId());
		valid = false;
	}
	
	public static Levitation getLevatator(UUID uuid) {
		return levitators.get(uuid);
	}
}
