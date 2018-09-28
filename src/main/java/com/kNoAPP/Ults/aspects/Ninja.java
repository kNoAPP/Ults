package com.kNoAPP.Ults.aspects;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle.DustOptions;

import com.kNoAPP.Ults.Ultimates;

public class Ninja {
	
	public Ninja() {
		new BukkitRunnable() {
			public void run() {
				for(Player pl : Bukkit.getOnlinePlayers()) if(isNinja(pl)) vanish(pl);
			}
		}.runTaskTimer(Ultimates.getPlugin(), 0L, 60L);
	}
	
	public void vanish(final Player p) {
		new BukkitRunnable() {
			int i = 0;
			public void run() {
				if(p != null && i < 12 && isNinja(p)) {
					if(i == 0) {
						p.removePotionEffect(PotionEffectType.INVISIBILITY);
						p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 65, 0));
					}
					for(int a=0; a<3; a++)
						p.getWorld().spawnParticle(Particle.REDSTONE, 
								p.getLocation().clone().add(0, 0.5, 0), 1, 0.25F, 0.25F, 0.25F, 0.01, new DustOptions(Color.BLACK, 1F));
					i++;
				} else this.cancel();
			}
		}.runTaskTimer(Ultimates.getPlugin(), 0L, 5L);
	}
	
	public boolean isNinja(Player p) {
		if(p.getInventory().getHelmet() != null &&
			p.getInventory().getChestplate() != null &&
			p.getInventory().getLeggings() != null &&
			p.getInventory().getBoots() != null &&
			p.getInventory().getHelmet().getType() == Material.LEATHER_HELMET &&
			p.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE &&
			p.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS &&
			p.getInventory().getBoots().getType() == Material.LEATHER_BOOTS &&
			p.getInventory().getHelmet().hasItemMeta() &&
			p.getInventory().getChestplate().hasItemMeta() &&
			p.getInventory().getLeggings().hasItemMeta() &&
			p.getInventory().getBoots().hasItemMeta()) {
				LeatherArmorMeta lmh = (LeatherArmorMeta) p.getInventory().getHelmet().getItemMeta();
				LeatherArmorMeta lmc = (LeatherArmorMeta) p.getInventory().getChestplate().getItemMeta();
				LeatherArmorMeta lml = (LeatherArmorMeta) p.getInventory().getLeggings().getItemMeta();
				LeatherArmorMeta lmb = (LeatherArmorMeta) p.getInventory().getBoots().getItemMeta();
				Color black = Color.fromRGB(29, 29, 33);
				
				return lmh.getColor().equals(black) &&
						lmc.getColor().equals(black) &&
						lml.getColor().equals(black) &&
						lmb.getColor().equals(black);
			}
		return false;
	}
}
