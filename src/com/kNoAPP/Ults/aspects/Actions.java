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
		FileConfiguration fc = Data.MAIN.getFileConfig();
		p.setGravity(true);
		if(!fc.isSet("Player." + p.getUniqueId() + ".Respawns")) {
			fc.set("Player." + p.getUniqueId() + ".Respawns", 1);
			Data.MAIN.saveDataFile(fc);
		}
		
		int r = fc.getInt("Player." + p.getUniqueId() + ".Respawns");
		if(r > 0) p.sendMessage(Message.RESPAWN.getMessage("You have " + r + " respawn(s) left!"));
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		FileConfiguration fc = Data.MAIN.getFileConfig();
		int r = fc.getInt("Player." + p.getUniqueId() + ".Respawns");
		if(r > 0) {
			fc.set("Player." + p.getUniqueId() + ".Respawns", r-1);
			Data.MAIN.saveDataFile(fc);
			
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
							FileConfiguration fc = Data.MAIN.getFileConfig();
							int r = fc.getInt("Player." + p.getUniqueId() + ".Respawns");
							fc.set("Player." + p.getUniqueId() + ".Respawns", r+1);
							Data.MAIN.saveDataFile(fc);
							
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
					/*
					if(is.hasItemMeta()) {
						ItemMeta im = is.getItemMeta();
						String name = im.getDisplayName();
						if(name != null && name.startsWith(ChatColor.GOLD + "Snowball Gun") && is.getType() == Material.DIAMOND_HOE) {
							Snowball sb = p.getWorld().spawn(p.getLocation().clone().add(0, 1.5, 0).add(p.getLocation().getDirection().normalize()), Snowball.class);
							sb.setFireTicks(100);
							Object[] isDouble = Tools.canParseToDouble(name.replaceFirst(ChatColor.GOLD + "Snowball Gun ", ""));
							Vector v = p.getLocation().getDirection().normalize();
							double mag = (double) isDouble[1];
							sb.setVelocity(v.multiply(mag));
							graphTrail(p, p.getLocation().clone(), sb, mag);
							
							p.sendMessage(ChatColor.GOLD + "Starting Location:");
							p.sendMessage(ChatColor.GRAY + " X: " + p.getLocation().getX() + " Y: " + p.getLocation().getY() + " Z: " + p.getLocation().getZ());
							p.sendMessage(ChatColor.GOLD + "Starting Vector:");
							p.sendMessage(ChatColor.GRAY + "M: " + mag + " X: " + v.getX() + " Y: " + v.getY() + " Z: " + v.getZ());
						}
					}
					*/
				}
			}
		}
	}
	
	/*
	private void graphTrail(Player p, Location l, Projectile proj, double mag) {
		new BukkitRunnable() {
			long start = System.currentTimeMillis();
			public void run() {
				if(proj != null && proj.isValid() && !proj.isDead()) proj.getWorld().spawnParticle(Particle.DRIP_LAVA, proj.getLocation(), 1, 0F, 0F, 0F, 0.01);
				else {
					double time = (System.currentTimeMillis()-start)/1000.0;
					double dx = Math.abs(proj.getLocation().getX() - l.getX());
					double dy = l.getY() - proj.getLocation().getY();
					double dz = Math.abs(proj.getLocation().getZ() - l.getZ());
					double vYInit = mag*l.getDirection().normalize().getY();
					double gravity = (2.0*(vYInit*time+dy))/Math.pow(time, 2);
					
					p.sendMessage(ChatColor.GOLD + "Time: " + ChatColor.GRAY + time);
					p.sendMessage(ChatColor.GOLD + "Finishing Location:");
					p.sendMessage(ChatColor.GRAY + "X: " + proj.getLocation().getX()
							+ " Y: " + proj.getLocation().getY()
							+ " Z: " + proj.getLocation().getZ());
					
					p.sendMessage(ChatColor.YELLOW + "Change (X): " + ChatColor.GRAY + dx);
					p.sendMessage(ChatColor.YELLOW + "Change (Y): " + ChatColor.GRAY + dy);
					p.sendMessage(ChatColor.YELLOW + "Change (Z): " + ChatColor.GRAY + dz);
					p.sendMessage(ChatColor.DARK_GREEN + "Gravity: " + ChatColor.GRAY + gravity);
					this.cancel();
				}
			}
		}.runTaskTimer(Ultimates.getPlugin(), 0L, 2L);
	}
	*/
	
	@EventHandler
    public void onProjHit(ProjectileHitEvent e) {
		FileConfiguration fc = Data.MAIN.getFileConfig();
		if(fc.getBoolean("Enable.Bouncing-Projectiles")) {
	        Projectile proj = e.getEntity();
	
	        int bounces = 5;
	        if(!proj.hasMetadata("bouncesLeft")) proj.setMetadata("bouncesLeft", new FixedMetadataValue(Ultimates.getPlugin(), 5));
	        else bounces = (int) proj.getMetadata("bouncesLeft").get(0).value();
	
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
