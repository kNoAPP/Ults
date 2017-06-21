package com.kNoAPP.Ults;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.kNoAPP.Ults.aspects.Actions;
import com.kNoAPP.Ults.commands.ChunkLoader;
import com.kNoAPP.Ults.commands.RecallCMD;
import com.kNoAPP.Ults.commands.ScrambleCmds;
import com.kNoAPP.Ults.commands.UltCmds;
import com.kNoAPP.Ults.data.Data;
import com.kNoAPP.Ults.utils.Items;

public class Ultimates extends JavaPlugin implements Listener {
	
	@Override
	public void onEnable() {
		long tStart = System.currentTimeMillis();
		register();
		addRecipies();
		importData();
		ChunkLoader.load(); //Load Chunks
		
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
	
	public static Plugin getPlugin() {
		return Bukkit.getPluginManager().getPlugin("Ultimates");
	}
}
