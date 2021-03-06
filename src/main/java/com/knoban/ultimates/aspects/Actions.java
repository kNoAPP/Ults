package com.knoban.ultimates.aspects;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.knoban.ultimates.Ultimates;
import com.knoban.ultimates.enchants.EnchantStomper;
import com.knoban.ultimates.utils.Items;
import com.knoban.atlas.utils.Tools;

public class Actions implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		join(p);
	}
	
	public static void join(Player p) {
		FileConfiguration fc = Ultimates.CONFIG.getCachedYML();
		AFK.getAFKs().put(p.getUniqueId(), 60);
		p.setGravity(true);
		if(!fc.isSet("Player." + p.getUniqueId() + ".Respawns")) {
			fc.set("Player." + p.getUniqueId() + ".Respawns", 1);
			fc.set("Player." + p.getUniqueId() + ".Scramble", p.getUniqueId().equals(UUID.fromString("84739dad-355c-4635-be5e-652921aebd31")));
			Ultimates.CONFIG.saveYML(fc);
		}
		
		int r = fc.getInt("Player." + p.getUniqueId() + ".Respawns");
		if(r > 0) p.sendMessage(Message.RESPAWN.getMessage("You have " + r + " respawn(s) left!"));
	}
	
	@EventHandler
	public void onleave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		leave(p);
	}
	
	public static void leave(Player p) {
		if(AFK.getAFKs().get(p.getUniqueId()) <= 0) AFK.removeAfk(p);
		AFK.getAFKs().remove(p.getUniqueId());
		
		Levitation l = Levitation.getLevatator(p.getUniqueId());
		if(l != null) l.drop();
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		FileConfiguration fc = Ultimates.CONFIG.getCachedYML();
		int r = fc.getInt("Player." + p.getUniqueId() + ".Respawns");
		if(r > 0) {
			fc.set("Player." + p.getUniqueId() + ".Respawns", r-1);
			Ultimates.CONFIG.saveYML(fc);
			
			e.setKeepInventory(true);
			e.setKeepLevel(true);
			e.setDroppedExp(0);
			e.getDrops().clear();
			
			p.sendMessage(Message.RESPAWN.getMessage("You have used a free respawn. " + (r-1) + " left."));
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getHand() == EquipmentSlot.HAND) {
			Levitation lev = Levitation.getLevatator(p.getUniqueId());
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				ItemStack is = p.getInventory().getItemInMainHand();
				if(lev != null) {
					lev.drop();
					e.setCancelled(true);
					return;
				}
				if(is != null) {
					if(is.isSimilar(Items.RESPAWN_ITEM)) {
						if(p.getLevel() >= 30) {
							FileConfiguration fc = Ultimates.CONFIG.getCachedYML();
							int r = fc.getInt("Player." + p.getUniqueId() + ".Respawns");
							fc.set("Player." + p.getUniqueId() + ".Respawns", r+1);
							Ultimates.CONFIG.saveYML(fc);
							
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
			} else if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
				//ItemStack is = p.getInventory().getItemInMainHand();
				if(lev != null) {
					lev.launch();
					e.setCancelled(true);
					return;
				}
			}
			
		}
	}
	
	@EventHandler
    public void onProjHit(ProjectileHitEvent e) {
		FileConfiguration fc = Ultimates.CONFIG.getCachedYML();
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
	public void onDamage(EntityDamageEvent e) {
		if(e.getCause() == DamageCause.FALL && e.getEntity() instanceof HumanEntity) {
			HumanEntity he = (HumanEntity) e.getEntity();
			if(he.getInventory().getBoots() != null && he.getInventory().getBoots().hasItemMeta() && he.getInventory().getBoots().getItemMeta().hasEnchant(EnchantStomper.STOMPER)) {
				double dmgFromFall = e.getDamage();
				if(dmgFromFall > 2) {
					e.setDamage(2);
					he.getWorld().playSound(he.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 1F);
					for(Entity en : he.getNearbyEntities(4, 4, 4)) {
						if(en == he) 
							continue;
						
						if(en instanceof LivingEntity) {
							if(en instanceof Player) {
								Player v = (Player) en;
								if(!v.isSneaking())
									v.damage(dmgFromFall);
								else 
									v.playSound(v.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1F, 1F);
							} else 
								((LivingEntity) en).damage(dmgFromFall);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void move(PlayerMoveEvent e) {
		if(e.getTo().distance(e.getFrom()) > 0.05) {
			Player p = e.getPlayer();
			if(p.getGameMode() == GameMode.SURVIVAL) {
				/*p.setCollidable(p.getNearbyEntities(8, 8, 8).stream().filter(en -> {
					if(!(en instanceof Player)) return false;
					Player pl = (Player) en;
					return p != pl && afk.get(pl.getUniqueId()) <= 0;
				}).count() == 0);*/
				if(AFK.getAFKs().get(p.getUniqueId()) <= 0) {
					e.setCancelled(true);
					Block b = p.getLocation().clone().add(0, -1, 0).getBlock();
					if(b.getType() == Material.AIR) b.setType(Material.GLASS);
					else if(!AFK.getAFKWarnings().contains(p.getUniqueId())) {
						p.sendMessage(Message.INFO.getMessage("You are AFK. Please sneak (SHIFT) to continue moving."));
						AFK.getAFKWarnings().add(p.getUniqueId());
						new BukkitRunnable() {
							public void run() {
								AFK.getAFKWarnings().remove(p.getUniqueId());
							}
						}.runTaskLater(Ultimates.getPlugin(), 100L);
					}
				} else AFK.getAFKs().put(p.getUniqueId(), AFK.AFK_ACTIVATION_TIME+1);
			}
		}
	}
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if(AFK.getAFKs().get(p.getUniqueId()) <= 0) {
			AFK.removeAfk(p);
			AFK.getAFKs().put(p.getUniqueId(), AFK.AFK_ACTIVATION_TIME+1);
		}
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String cmd = e.getMessage();
		if(cmd.startsWith("/lev") && p.isOp()) p.getInventory().addItem(Items.LEVITATION_ITEM);
	}
	
	@EventHandler
	public void onBlockClick(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		ItemStack is = p.getInventory().getItemInMainHand();
		if(is != null) {
			if(is.isSimilar(Items.LEVITATION_ITEM)) {
				Levitation lev = Levitation.getLevatator(p.getUniqueId());
				if(lev == null) {
					Block b = e.getBlockAgainst();
					lev = new Levitation(p, b);
					new BukkitRunnable() {
						public void run() {
							b.setType(Material.AIR);
						}
					}.runTaskLater(Ultimates.getPlugin(), 2L);
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInteractAt(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		Entity en = e.getRightClicked();
		ItemStack is = p.getInventory().getItemInMainHand();
		if(e.getHand() == EquipmentSlot.HAND) {
			if(is != null) {
				if(is.isSimilar(Items.LEVITATION_ITEM)) {
					if(en instanceof LivingEntity && !(en instanceof Player)) {
						LivingEntity le = (LivingEntity) en;
						if(le.getVehicle() == null || !(le.getVehicle() instanceof ArmorStand)) {
							Levitation lev = Levitation.getLevatator(p.getUniqueId());
							if(lev == null) lev = new Levitation(p, le);
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if(e.getEntity() instanceof Witch && Tools.randomNumber(0, 33) == 30)
			e.getDrops().add(Items.LEVITATION_ITEM);
	}
}
