package com.kNoAPP.Ults;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.kNoAPP.Ults.aspects.Actions;
import com.kNoAPP.Ults.aspects.Ninja;
import com.kNoAPP.Ults.commands.Executor;
import com.kNoAPP.Ults.data.Data;
import com.kNoAPP.Ults.data.HikariMedium;
import com.kNoAPP.Ults.utils.Items;

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
		getServer().getPluginManager().registerEvents(new Actions(), this);
		
		getCommand("ult").setExecutor(new Executor());
		
		addRecipies();
	}
	
	private void addRecipies() {
		getServer().addRecipe(Items.getRespawnRecipe());
	}
	
	private void importData() {
		getPlugin().getLogger().info("Importing data files...");
		FileConfiguration fc = Data.CONFIG.getCachedYML();
		medium = new HikariMedium(fc.getString("MySQL.host"), fc.getInt("MySQL.port"), fc.getString("MySQL.database"), fc.getString("MySQL.username"), fc.getString("MySQL.password"));
	}
	
	private void importAspects() {
		getPlugin().getLogger().info("Importing aspects...");
		Actions.load();
		new Ninja();
	}
	
	public void exportData() {
		getPlugin().getLogger().info("Exporting data files...");
	}
	
	private void exportAspects() {
		getPlugin().getLogger().info("Exporting aspects...");
	}
	
	public HikariMedium getMedium() {
		return medium;
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
}
