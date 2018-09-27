package com.kNoAPP.Ults;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Ults.aspects.Actions;
import com.kNoAPP.Ults.commands.ChunkLoader;
import com.kNoAPP.Ults.commands.RecallCMD;
import com.kNoAPP.Ults.commands.ScrambleCmds;
import com.kNoAPP.Ults.commands.UltCmds;
import com.kNoAPP.Ults.data.Data;
import com.kNoAPP.Ults.data.HikariMedium;
import com.kNoAPP.Ults.utils.Items;
import com.kNoAPP.Ults.utils.Tools;

public class Ultimates extends JavaPlugin implements Listener {
	
	private HikariMedium medium;
	private static Ultimates plugin;
	
	@Override
	public void onEnable() {
		long tStart = System.currentTimeMillis();
		plugin = this;
		register();
		importData();
		importAspects();
		
		long tEnd = System.currentTimeMillis();
		getPlugin().getLogger().info("Successfully Enabled! (" + (tEnd - tStart) + " ms)");
	}
	
	@Override
	public void onDisable() {
		long tStart = System.currentTimeMillis();
		exportAspects();
		exportData();
		long tEnd = System.currentTimeMillis();
		getPlugin().getLogger().info("Successfully Disabled! (" + (tEnd - tStart) + " ms)");
	}
	
	private void register() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new RecallCMD(), this);
		getServer().getPluginManager().registerEvents(new ChunkLoader(), this);
		getServer().getPluginManager().registerEvents(new Actions(), this);
		
		getCommand("ults").setExecutor(new UltCmds());
		getCommand("soundgen").setExecutor(new UltCmds());
		getCommand("recall").setExecutor(new RecallCMD());
		getCommand("chunk").setExecutor(new ChunkLoader());
		getCommand("scramble").setExecutor(new ScrambleCmds());
		
		addRecipies();
	}
	
	private void addRecipies() {
		getServer().addRecipe(Items.getRespawnRecipe());
	}
	
	private void importData() {
		getPlugin().getLogger().info("Importing .yml Files...");
		FileConfiguration fc = Data.CONFIG.getCachedYML();
		medium = new HikariMedium(fc.getString("MySQL.host"), fc.getInt("MySQL.port"), fc.getString("MySQL.database"), fc.getString("MySQL.username"), fc.getString("MySQL.password"));
	}
	
	private void importAspects() {
		
	}
	
	public void exportData() {
		getPlugin().getLogger().info("Exporting .yml Files...");
	}
	
	private void exportAspects() {
		
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
						p.getWorld().spawnParticle(Particle.FALLING_DUST, p.getLocation().clone().add(Tools.randomNumber(-0.1, 0.1), 0.5+Tools.randomNumber(-0.25, 0.25), Tools.randomNumber(-0.1, 0.1)), 0, 0F, 0F, 0F, 0);
					i++;
				} else this.cancel();
			}
		}.runTaskTimer(plugin, 0L, 5L);
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
	
	public HikariMedium getMedium() {
		return medium;
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
}
