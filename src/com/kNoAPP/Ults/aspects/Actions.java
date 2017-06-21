package com.kNoAPP.Ults.aspects;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.kNoAPP.Ults.data.Data;
import com.kNoAPP.Ults.utils.Items;

public class Actions implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setGravity(true);
		FileConfiguration fc = Data.MAIN.getFileConfig();
		if(!fc.isSet("Player." + p.getUniqueId() + ".Respawns")) {
			fc.set("Player." + p.getUniqueId() + ".Respawns", 1);
			Data.MAIN.saveDataFile(fc);
		}
		
		int r = fc.getInt("Player." + p.getUniqueId() + ".Respawns");
		if(r > 0) {
			p.sendMessage(Message.RESPAWN.getMessage("You have " + r + " respawn(s) left!"));
		}
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
				}
			}
		}
	}
}
