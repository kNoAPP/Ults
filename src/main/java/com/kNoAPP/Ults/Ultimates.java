package com.kNoAPP.Ults;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.kNoAPP.Ults.aspects.AFK;
import com.kNoAPP.Ults.aspects.Actions;
import com.kNoAPP.Ults.aspects.Ninja;
import com.kNoAPP.Ults.commands.Executor;
import com.kNoAPP.Ults.data.DataHandler;
import com.kNoAPP.Ults.data.HikariMedium;
import com.kNoAPP.Ults.utils.Items;

public class Ultimates extends JavaPlugin implements Listener {
	
	private HikariMedium medium;
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
		FileConfiguration fc = DataHandler.CONFIG.getCachedYML();
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
		Actions.load();
		AFK.afkLoop();
		new Ninja();
		
		for(Player pl : Bukkit.getOnlinePlayers()) Actions.join(pl);
	}
	
	public void exportData() {
		if(failed)
			return;
		
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
