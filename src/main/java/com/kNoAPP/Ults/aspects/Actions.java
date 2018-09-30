package com.kNoAPP.Ults.aspects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.kNoAPP.Ults.Ultimates;
import com.kNoAPP.Ults.commands.RecallCommand;
import com.kNoAPP.Ults.data.Data;
import com.kNoAPP.Ults.utils.Items;
import com.kNoAPP.Ults.utils.Serializer;

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
	
	@EventHandler
	public void damage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			RecallCommand.cancel(p.getName());
		}
	}
	
	@EventHandler
	public void move(PlayerMoveEvent e) {
		if(e.getTo().distance(e.getFrom()) > 0.05) {
			RecallCommand.cancel(e.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void leave(PlayerQuitEvent e) {
		RecallCommand.cancel(e.getPlayer().getName());
	}
	
	@EventHandler
	public void onUnload(ChunkUnloadEvent e) {
		FileConfiguration fc = Data.CONFIG.getCachedYML();
		List<String> chunksR = fc.getStringList("Chunk.Load");
		List<Chunk> chunks = convert(chunksR);
		
		if(isFrozen(chunks, e.getChunk())) {
			e.setCancelled(true);
			Ultimates.getPlugin().getLogger().info("Chunk(" + e.getChunk().getX() + ", " + e.getChunk().getZ() + ") tried to unload!");
		}
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		String cmd = e.getMessage();
		if(cmd.startsWith("/recall")) e.setMessage(e.getMessage().replaceFirst("/recall", "/ult recall"));
		if(cmd.startsWith("/chunk")) e.setMessage(e.getMessage().replaceFirst("/chunk", "/ult chunk"));
		if(cmd.startsWith("/scramble")) e.setMessage(e.getMessage().replaceFirst("/scramble", "/ult scramble"));
		if(cmd.startsWith("/ults")) e.setMessage(e.getMessage().replaceFirst("/ults", "/ult ults"));
		if(cmd.startsWith("/soundgen")) e.setMessage(e.getMessage().replaceFirst("/soundgen", "/ult soundgen"));
	}
	
	public static List<Chunk> convert(List<String> raw) {
		List<Chunk> chunks = new ArrayList<Chunk>();
		for(String c : raw) chunks.add(Serializer.expand(c).getChunk());
		return chunks;
	}
	
	public static boolean isSimilar(Chunk c1, Chunk c2) {
		return (c1.getX() == c2.getX() && c1.getZ() == c2.getZ() && c1.getWorld().getName().equals(c2.getWorld().getName()));
	}
	
	public static boolean isFrozen(List<Chunk> chunks, Chunk cc) {
		for(Chunk c : chunks) if(isSimilar(cc, c)) return true;
		return false;
	}
	
	public static void load() {
		FileConfiguration fc = Data.CONFIG.getCachedYML();
		List<String> chunksR = fc.getStringList("Chunk.Load");
		List<Chunk> chunks = convert(chunksR);
		
		for(Chunk c : chunks) {
			c.load();
			Ultimates.getPlugin().getLogger().info("Chunk(" + c.getX() + ", " + c.getZ() + ") has been loaded!");
		}
	}
}
