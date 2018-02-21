package com.kNoAPP.Ults;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import com.kNoAPP.Ults.utils.Items;
import com.kNoAPP.Ults.utils.Tools;

public class Ultimates extends JavaPlugin implements Listener {
	
	private static Plugin plugin;
	
	@Override
	public void onEnable() {
		long tStart = System.currentTimeMillis();
		plugin = this;
		register();
		addRecipies();
		importData();
		ChunkLoader.load(); //Load Chunks
		ninja();
		
		long tEnd = System.currentTimeMillis();
		getPlugin().getLogger().info("Successfully Enabled! (" + (tEnd - tStart) + " ms)");
	}
	
	@Override
	public void onDisable() {
		long tStart = System.currentTimeMillis();
		exportData();
		long tEnd = System.currentTimeMillis();
		getPlugin().getLogger().info("Successfully Disabled! (" + (tEnd - tStart) + " ms)");
	}
	
	private void register() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new RecallCMD(), this);
		this.getServer().getPluginManager().registerEvents(new ChunkLoader(), this);
		this.getServer().getPluginManager().registerEvents(new Actions(), this);
		
		this.getCommand("ults").setExecutor(new UltCmds());
		this.getCommand("soundgen").setExecutor(new UltCmds());
		//this.getCommand("snball").setExecutor(new UltCmds());
		
		this.getCommand("recall").setExecutor(new RecallCMD());
		
		this.getCommand("chunk").setExecutor(new ChunkLoader());
		
		this.getCommand("scramble").setExecutor(new ScrambleCmds());
	}
	
	private void addRecipies() {
		//Respawn Item
		this.getServer().addRecipe(Items.getRespawnRecipe());
	}
	
	public static void importData() {
		getPlugin().getLogger().info("Importing .yml Files...");
		for(Data d : Data.values()) {
			if(d != Data.CONFIG) {
				if(Data.CONFIG.getFileConfig().getBoolean("UseMainFolder") == true) {
					d.setFile("");
				} else {
					d.setFile(Data.CONFIG.getFileConfig().getString("UseCustomFolder"));
				}
			}
			d.createDataFile();
		}
	}
	
	public static void exportData() {
		getPlugin().getLogger().info("Exporting .yml Files...");
		for(Data d : Data.values()) {
			d.logDataFile();
		}
	}
	
	public void ninja() {
		new BukkitRunnable() {
			public void run() {
				for(Player pl : Bukkit.getOnlinePlayers()) {
					if(isNinja(pl)) vanish(pl);
				}
			}
		}.runTaskTimer(plugin, 0L, 60L);
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
				p.getInventory().getBoots() != null) {
			if(p.getInventory().getHelmet().getType() == Material.LEATHER_HELMET &&
					p.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE &&
					p.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS &&
					p.getInventory().getBoots().getType() == Material.LEATHER_BOOTS) {
				if(p.getInventory().getHelmet().hasItemMeta() &&
						p.getInventory().getChestplate().hasItemMeta() &&
						p.getInventory().getLeggings().hasItemMeta() &&
						p.getInventory().getBoots().hasItemMeta()) {
					LeatherArmorMeta lmh = (LeatherArmorMeta) p.getInventory().getHelmet().getItemMeta();
					LeatherArmorMeta lmc = (LeatherArmorMeta) p.getInventory().getChestplate().getItemMeta();
					LeatherArmorMeta lml = (LeatherArmorMeta) p.getInventory().getLeggings().getItemMeta();
					LeatherArmorMeta lmb = (LeatherArmorMeta) p.getInventory().getBoots().getItemMeta();
					return lmh.getColor().getRed() == 29 &&
							lmh.getColor().getGreen() == 29 &&
							lmh.getColor().getBlue() == 33 &&
									lmc.getColor().getRed() == 29 &&
									lmc.getColor().getGreen() == 29 &&
									lmc.getColor().getBlue() == 33 &&
											lml.getColor().getRed() == 29 &&
											lml.getColor().getGreen() == 29 &&
											lml.getColor().getBlue() == 33 &&
													lmb.getColor().getRed() == 29 &&
													lmb.getColor().getGreen() == 29 &&
													lmb.getColor().getBlue() == 33;
				}
			}
		}
		return false;
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
}
