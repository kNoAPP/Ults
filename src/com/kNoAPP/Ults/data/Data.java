package com.kNoAPP.Ults.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.kNoAPP.Ults.Ultimates;

public enum Data {
	
	CONFIG(new File(Ultimates.getPlugin().getDataFolder(), "config.yml"), new YamlConfiguration(), "config.yml"),
	MAIN(null, new YamlConfiguration(), "main.yml");
	
	private File file;
	private FileConfiguration fc;
	private String fileName;
	
	private Data(File file, FileConfiguration fc, String fileName) {
		this.file = file;
		this.fc = fc;
		this.fileName = fileName;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(String path) {
		if(path == "") {
			this.file = new File(Ultimates.getPlugin().getDataFolder(), this.getFileName());
		} else {
			this.file = new File(path, this.getFileName());
		}
	}
	
	public FileConfiguration getFileConfig() {
		return fc;
	}
	
	public void saveDataFile(FileConfiguration fc) {
		this.fc = fc;
	}
	
	public void logDataFile() {
		try {
			this.fc.save(this.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getPath() {
		return this.getFile().getAbsolutePath();
	}
	
	public void createDataFile() {
		if(!this.getFile().exists()) {
			Ultimates.getPlugin().getLogger().info(this.getFileName() + " not found. Creating...");
			try {
				this.getFile().createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			FileConfiguration fc = this.fc;
			if(this == CONFIG) {
				fc.set("Version", "1.0.0");
				fc.set("UseMainFolder", true);
				fc.set("UseCustomFolder", "/example/path/");
			}
			if(this == MAIN) {
				fc.set("Version", "1.0.0");
				fc.set("Enable.Bouncing-Projectiles", false);
				fc.set("Enable.IPS", false);
			}
			this.saveDataFile(fc);
			this.logDataFile();
        }
	
		try {
			this.fc.load(this.getFile());
		} catch (FileNotFoundException e) {
			 e.printStackTrace();
	    } catch (IOException e) {
	         e.printStackTrace();
	    } catch (InvalidConfigurationException e) {
	    	 e.printStackTrace();
	    }
	}
}
