package com.kNoAPP.Ults;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
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
import com.kNoAPP.Ults.enchants.CustomEnchant;
import com.kNoAPP.Ults.utils.Items;
import com.kNoAPP.atlas.commands.AtlasCommand;
import com.kNoAPP.atlas.commands.CommandInfo;

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
	
	private void registerCommand(AtlasCommand dc) {
		CommandInfo ci = dc.getInfo();
		PluginCommand pc = getCommand(ci.name());
		if(pc == null) {
			try {
				Constructor<PluginCommand> cons = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
				cons.setAccessible(true);
				pc = cons.newInstance(ci.name(), this);
				
				pc.setAliases(Arrays.asList(ci.aliases()));
				pc.setDescription(ci.description());
				pc.setDescription(ci.description());
				
				Field cmdMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                cmdMap.setAccessible(true);
                CommandMap map = (CommandMap) cmdMap.get(Bukkit.getPluginManager());

                map.register(ci.name(), pc);
			} catch (NoSuchMethodException | IllegalAccessException |
                    InstantiationException | InvocationTargetException |
                    NoSuchFieldException e) {
                e.printStackTrace();
                getPlugin().getLogger().warning("Failed to load command: /" + ci.name() + "! Command is not active.");
                return;
            }
			
			pc.setExecutor(dc);
			pc.setTabCompleter(dc);
			
			getPlugin().getLogger().info("Successfully loaded command: /" + ci.name() + ".");
			for(String alias : ci.aliases())
				getPlugin().getLogger().info("Successfully loaded alias: /" + alias + ".");
		}
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
		CustomEnchant.registerEnchantments();
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
		CustomEnchant.unregisterEnchantments();
	}
	
	public HikariMedium getMedium() {
		return medium;
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
}
