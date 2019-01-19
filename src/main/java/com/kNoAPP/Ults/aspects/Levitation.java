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
import org.bukkit.entity.FallingBlock;
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
	private ArmorStand as;
	private BlockData bd;
	private MaterialData md;
	private Material m;
	private volatile boolean valid = true;
	
	public Levitation(Player p, Block b) {
		this.p = p;
		this.m = b.getType();
		this.bd = b.getBlockData();
		this.md = b.getState().getData();
		
		levitate();
	}
	
	private void levitate() {
		as = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().clone().add(p.getLocation().getDirection().normalize().multiply(p.getInventory().getHeldItemSlot()+1)), EntityType.ARMOR_STAND);
		as.setVisible(false);
		as.setInvulnerable(true);
		as.setGravity(false);
		ItemStack is = new ItemStack(m);
		is.setData(md);
		as.setHelmet(is);
		as.setHeadPose(new EulerAngle(0, 0, 0));
		
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1F, 1.6F);
		
		levitators.put(p.getUniqueId(), this);
		
		new BukkitRunnable() {
			public void run() {
				if(!valid) {
					this.cancel();
					return;
				}
				
				as.teleport(p.getLocation().clone().add(p.getLocation().getDirection().normalize().multiply(p.getInventory().getHeldItemSlot()+1)));
			}
		}.runTaskTimer(Ultimates.getPlugin(), 0L, 1L);
	}
	
	public void launch() {
		FallingBlock fb = p.getWorld().spawnFallingBlock(as.getLocation().clone().add(0, 1, 0), bd);
		as.setHelmet(new ItemStack(Material.AIR));
		as.remove();
		as = null;
		
		fb.setVelocity(p.getLocation().clone().getDirection().normalize().multiply((12-p.getInventory().getHeldItemSlot())/4));
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 0.6F);
		
		stop();
		
		new BukkitRunnable() {
			List<LivingEntity> hitEntities = new ArrayList<LivingEntity>(); 
			public void run() {
				if(fb != null && fb.isValid() && !fb.isDead()) {
					for(Entity e : fb.getNearbyEntities(1.5, 1.5, 1.5)) {
						if(e instanceof LivingEntity) {
							LivingEntity le = (LivingEntity) e;
							if(hitEntities.contains(le)) continue;
							
							le.damage(fb.getVelocity().clone().length() * 2.5);
							le.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, le.getLocation().clone().add(0, 0.8, 0), 1, 0.2F, 0.2F, 0.2F, 0.01);
							le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1F, 1F);
							le.setVelocity(fb.getVelocity().clone().normalize());
							if(p != null && p.isValid() && p.isOnline()) {
								EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(p, le, DamageCause.MAGIC, fb.getVelocity().clone().length() * 2.5);
								le.setLastDamageCause(event);
								Bukkit.getServer().getPluginManager().callEvent(event);
							}
							hitEntities.add(le);
						}
					}
				}
			}
		}.runTaskTimer(Ultimates.getPlugin(), 0L, 2L);
	}
	
	public void drop() {
		p.getWorld().spawnFallingBlock(as.getLocation().clone().add(0, 1, 0), bd);
		as.setHelmet(new ItemStack(Material.AIR));
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
