package com.knoban.ultimates;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.knoban.ultimates.aspects.AFK;
import com.knoban.ultimates.aspects.Actions;
import com.knoban.ultimates.aspects.Ninja;
import com.knoban.ultimates.commands.ChunkLoaderCommand;
import com.knoban.ultimates.commands.HelpCommand;
import com.knoban.ultimates.commands.RecallCommand;
import com.knoban.ultimates.commands.ScrambleCommand;
import com.knoban.ultimates.commands.SoundGenCommand;
import com.knoban.ultimates.commands.UltimateCommand;
import com.knoban.ultimates.enchants.CustomEnchant;
import com.knoban.ultimates.utils.Items;
import com.knoban.atlas.data.DataHandler.JSON;
import com.knoban.atlas.data.DataHandler.YML;
import com.knoban.atlas.data.HikariMedium;

public class Ultimates extends JavaPlugin {
	
	public static YML CONFIG;
	public static JSON FROZEN_CHUNKS;
	
	private HikariMedium medium;
	private ChunkLoaderCommand clc;
	private static Ultimates plugin;
	
	private boolean failed = false;
	
	@Override
	public void onEnable() {
		long tStart = System.currentTimeMillis();
		plugin = this;
		register();
		importData();
		importAspects();
		long tEnd = System.currentTimeMillis();
		getPlugin().getLogger().info("Successfully Enabled! (" + (tEnd - tStart) + " ms)");
		
		if(failed) getPlugin().getPluginLoader().disablePlugin(this);
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
		CONFIG = new YML(this, "/config.yml");
		FROZEN_CHUNKS = new JSON(this, "/frozenchunks.json");

		getServer().getPluginManager().registerEvents(new Actions(), this);
		
		clc = new ChunkLoaderCommand(FROZEN_CHUNKS);
		clc.registerCommandWithListener(this);
		new HelpCommand().registerCommand(this);
		new RecallCommand().registerCommandWithListener(this);
		new ScrambleCommand().registerCommand(this);
		new SoundGenCommand().registerCommand(this);
		new UltimateCommand().registerCommand(this);
		
		addRecipies();
	}
	
	private void addRecipies() {
		getServer().addRecipe(Items.getRespawnRecipe());
	}
	
	private void importData() {
		getPlugin().getLogger().info("Importing data files...");
		FileConfiguration fc = CONFIG.getCachedYML();
		try {
			medium = new HikariMedium(fc.getString("MySQL.host"), fc.getInt("MySQL.port"), fc.getString("MySQL.database"), fc.getString("MySQL.username"), fc.getString("MySQL.password"));
		} catch(Exception e) {
			e.printStackTrace();
			failed = true;
		}
	}
	
	private void importAspects() {
		if(failed)
			return;
		
		getPlugin().getLogger().info("Importing aspects...");
		CustomEnchant.registerEnchantments();
		AFK.afkLoop();
		new Ninja();
		
		for(Player pl : Bukkit.getOnlinePlayers()) Actions.join(pl);
	}
	
	public void exportData() {
		if(failed)
			return;
		
		clc.save(FROZEN_CHUNKS);
		getPlugin().getLogger().info("Exporting data files...");
	}
	
	private void exportAspects() {
		if(failed)
			return;
		
		getPlugin().getLogger().info("Exporting aspects...");
		for(Player pl : Bukkit.getOnlinePlayers()) Actions.leave(pl);
	}
	
	public HikariMedium getMedium() {
		return medium;
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
}
