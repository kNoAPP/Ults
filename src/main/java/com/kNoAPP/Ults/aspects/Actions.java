package com.kNoAPP.Ults.aspects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.kNoAPP.Ults.Ultimates;
import com.kNoAPP.Ults.data.Data;
import com.kNoAPP.Ults.utils.Items;

public class Actions implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		FileConfiguration fc = Data.CONFIG.getCachedYML();
		p.setGravity(true);
		if(!fc.isSet("Player." + p.getUniqueId() + ".Respawns")) {
			fc.set("Player." + p.getUniqueId() + ".Respawns", 1);
			Data.CONFIG.saveYML(fc);
		}
		
		int r = fc.getInt("Player." + p.getUniqueId() + ".Respawns");
		if(r > 0) p.sendMessage(Message.RESPAWN.getMessage("You have " + r + " respawn(s) left!"));
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		FileConfiguration fc = Data.CONFIG.getCachedYML();
		int r = fc.getInt("Player." + p.getUniqueId() + ".Respawns");
		if(r > 0) {
			fc.set("Player." + p.getUniqueId() + ".Respawns", r-1);
			Data.CONFIG.saveYML(fc);
			
			e.setKeepInventory(true);
			e.setKeepLevel(true);
			
			p.sendMessage(Message.RESPAWN.getMessage("You have used a free respawn. " + (r-1) + " left."));
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getHand() == EquipmentSlot.HAND) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				ItemStack is = p.getInventory().getItemInMainHand();
				if(is != null) {
					if(is.isSimilar(Items.getRespawnItem())) {
						if(p.getLevel() >= 30) {
							FileConfiguration fc = Data.CONFIG.getCachedYML();
							int r = fc.getInt("Player." + p.getUniqueId() + ".Respawns");
							fc.set("Player." + p.getUniqueId() + ".Respawns", r+1);
							Data.CONFIG.saveYML(fc);
							
							if(is.getAmount() > 1) {
								is.setAmount(is.getAmount() - 1);
							} else {
								p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
							}
							p.setLevel(p.getLevel()-30);
							p.updateInventory();
							
							p.sendMessage(Message.RESPAWN.getMessage("Respawn Token active!"));
							p.sendMessage(Message.RESPAWN.getMessage("You have " + (r+1) + " respawn(s) left!"));
							p.getWorld().playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 2F, 0.8F);
							p.getWorld().spawnParticle(Particle.TOTEM, p.getLocation().clone().add(0, 0.5, 0), 30, 0.5F, 0.5F, 0.5F, 1);
						} else {
							p.sendMessage(Message.RESPAWN.getMessage("This requires 30 levels of xp!"));
							p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_HURT, 1F, 1F);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
    public void onProjHit(ProjectileHitEvent e) {
		FileConfiguration fc = Data.CONFIG.getCachedYML();
		if(fc.getBoolean("Enable.Bouncing-Projectiles")) {
	        Projectile proj = e.getEntity();
	
	        int bounces = 5;
	        if(!proj.hasMetadata("bouncesLeft")) proj.setMetadata("bouncesLeft", new FixedMetadataValue(Ultimates.getPlugin(), 5));
	        else bounces = (Integer) proj.getMetadata("bouncesLeft").get(0).value();
	
	        if(bounces == 0) return;
	        
	        Vector vel = proj.getVelocity();
	        Location loc = proj.getLocation();
	        Block hitBlock = loc.getBlock();
	        BlockFace blockFace = null;
	        BlockIterator blockIterator = new BlockIterator(loc.getWorld(), loc.toVector(), vel, 0.0D, 3);
	        Block previousBlock = hitBlock;
	        Block nextBlock = blockIterator.next();
	        while(blockIterator.hasNext() && (nextBlock.getType() == Material.AIR || nextBlock.isLiquid() || nextBlock.equals(hitBlock))) {
	            previousBlock = nextBlock;
	            nextBlock = blockIterator.next();
	        }
	        blockFace = nextBlock.getFace(previousBlock);
	        if(blockFace != null) {
	            if(blockFace == BlockFace.SELF) {
	                blockFace = BlockFace.UP;
	            }
	            Vector mirrorDirection = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
	            double dotProduct = vel.dot(mirrorDirection);
	            mirrorDirection = mirrorDirection.multiply(dotProduct).multiply(2.0D);
	            Projectile newProjectile = (Projectile) proj.getWorld().spawnEntity(loc, proj.getType());
	            newProjectile.setVelocity(vel.subtract(mirrorDirection).normalize().multiply(1)); //Changeable
	            newProjectile.setShooter(proj.getShooter());
	            newProjectile.setFireTicks(proj.getFireTicks());
	            newProjectile.setMetadata("bouncesLeft", new FixedMetadataValue(Ultimates.getPlugin(), --bounces));
	            
	            proj.remove();
	        }
		}
    }
}
