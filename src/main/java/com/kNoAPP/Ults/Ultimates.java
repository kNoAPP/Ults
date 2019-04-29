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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.kNoAPP.Ults.aspects.AFK;
import com.kNoAPP.Ults.aspects.Actions;
import com.kNoAPP.Ults.aspects.Ninja;
import com.kNoAPP.Ults.commands.ChunkLoaderCommand;
import com.kNoAPP.Ults.commands.HelpCommand;
import com.kNoAPP.Ults.commands.RecallCommand;
import com.kNoAPP.Ults.commands.ScrambleCommand;
import com.kNoAPP.Ults.commands.SoundGenCommand;
import com.kNoAPP.Ults.commands.UltimateCommand;
import com.kNoAPP.Ults.enchants.CustomEnchant;
import com.kNoAPP.Ults.utils.Items;
import com.kNoAPP.atlas.commands.AtlasCommand;
import com.kNoAPP.atlas.commands.CommandInfo;
import com.kNoAPP.atlas.data.HikariMedium;
import com.kNoAPP.atlas.data.DataHandler.JSON;
import com.kNoAPP.atlas.data.DataHandler.YML;

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
		
		clc = new ChunkLoaderCommand(FROZEN_CHUNKS);
		RecallCommand rc = new RecallCommand();
		
		getServer().getPluginManager().registerEvents(clc, this);
		getServer().getPluginManager().registerEvents(rc, this);
		getServer().getPluginManager().registerEvents(new Actions(), this);
		
		registerCommand(clc);
		registerCommand(new HelpCommand());
		registerCommand(rc);
		registerCommand(new ScrambleCommand());
		registerCommand(new SoundGenCommand());
		registerCommand(new UltimateCommand());
		
		addRecipies();
	}
	
	private void registerCommand(AtlasCommand dc) {
		CommandInfo ci = dc.getInfo();
		if(ci == null)
			throw new UnsupportedOperationException("CommandInfo annotation is missing!");	
		
		PluginCommand pc = getCommand(ci.name());
		if(pc == null) {
			try {
				Constructor<PluginCommand> cons = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
				cons.setAccessible(true);
				pc = cons.newInstance(ci.name(), this);
				
				pc.setAliases(Arrays.asList(ci.aliases()));
				pc.setDescription(ci.description());
				pc.setUsage(ci.usage());
				
				Field cmdMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                cmdMap.setAccessible(true);
                CommandMap map = (CommandMap) cmdMap.get(Bukkit.getPluginManager());

                map.register(getPlugin().getName(), pc);
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
