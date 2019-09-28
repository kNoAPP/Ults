package com.knoban.atlas.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A ULTILITY FOR GENERATING CONFIGURATION FILES FOR A PROGRAM
 * 
 * DataHandler is a simple class that can be used to generated, store,
 * and access configuration for a program. Currently, four file types
 * are supported: .yml, .properties, and .json, and generic files. 
 * Default configuration files are written and placed in src/main/resources. 
 * When the program runs and detects missing configuration files, the 
 * default set are written to a path written below starting from the 
 * program run location.
 * 
 * @author Alden Bansemer
 *
 */
public class DataHandler {
	
	protected String subtree, filename, outerPath;
	protected File file;
	protected boolean wasCreated;
	
	public DataHandler(Plugin pl, String filename) {
		this(pl, "/plugins/" + pl.getName(), filename);
	}
	
	/**
	 * A base constructor for each file type
	 * @param pl - A plugin instance to determine where to access data files from
	 * @param subtree - Where to drop/access the configuration file (starting from the program run location)
	 * @param filename - The name of a configuration file stored in src/main/resources usually preceded by a "/"
	 */
	public DataHandler(Plugin pl, String subtree, String filename) {
		this.subtree = subtree;
		this.filename = filename;
		this.file = new File(System.getProperty("user.dir") + subtree, filename);
		this.outerPath = file.getAbsolutePath();
		
		if(wasCreated = !file.exists()) {
			pl.getLogger().info(this.filename + " not found. Creating...");
			try {
				file.getParentFile().mkdirs();
				exportResource();
			} catch (Exception e) { e.printStackTrace(); }
		} else pl.getLogger().info(this.filename + " found. Loading...");
	}
	
	public File getFile() {
		return file;
	}
	
	/**
	 * @return True, if the default configuration file was generated on startup
	 */
	public boolean wasCreated() {
		return wasCreated;
	}
	
	/**
	 * Export a resource embedded into a Jar file to the local file path.
	 *
	 * @param resourcePath
	 *            ie.: "SmartLibrary.dll"
	 *            ie.: "data/SmartLibrary.dll"
	 * @throws Exception
	 */
	private void exportResource() throws Exception {
		InputStream stream = null;
		OutputStream resStreamOut = null;

		try {
			stream = DataHandler.class.getResourceAsStream(filename);
			if(stream == null) throw new Exception("Cannot get resource \"" + filename + "\" from Jar file.");

			int readBytes;
			byte[] buffer = new byte[4096];
			resStreamOut = new FileOutputStream(outerPath);
			while((readBytes = stream.read(buffer)) > 0) resStreamOut.write(buffer, 0, readBytes);
		} catch (Exception ex) { 
			throw ex;
		} finally {
			if(stream != null) stream.close();
			if(resStreamOut != null) resStreamOut.close();
		}
	}
	
	public static class JSON extends DataHandler {
		
		private String cached;
		private Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		public JSON(Plugin pl, String filename) {
			super(pl, filename);
		}
		
		/**
		 * A base constructor for each file type
		 * @param pl - A plugin instance to determine where to access data files from
		 * @param subtree - Where to drop/access the configuration file (starting from the program run location)
		 * @param filename - The name of a configuration file stored in src/main/resources usually preceded by a "/"
		 */
		public JSON(Plugin pl, String subtree, String filename) {
			super(pl, subtree, filename);
		}
		
		/**
		 * @param parse - The class to parse the Object as
		 * @return The current YML file (will overwrite cached file-- may cause data loss)
		 */
		public Object getJSON(Class<?> parse) {
			if(parse == null)
				throw new IllegalArgumentException("Class<?> cannot be null!");
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				StringBuilder sb = new StringBuilder();
				for(String line; (line = br.readLine()) != null; sb.append(line));
				br.close();
				cached = sb.toString();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			return gson.fromJson(cached, parse);
		}
		
		/**
		 * @param parse - The class to parse the Object as
		 * @return The cached YML file (saves computation time)
		 */
		public Object getCachedJSON(Class<?> parse) {
			if(parse == null)
				throw new IllegalArgumentException("Class<?> cannot be null!");
			
			if(cached == null) 
				return getJSON(parse);
			
			return gson.fromJson(cached, parse);
		}
		
		/**
		 * Save the given Object as a JSON to the system
		 * @param obj - Any JSON-Compatible Object
		 */
		public void saveJSON(Object obj) {
			cached = gson.toJson(obj);
			try {
				FileUtils.writeStringToFile(file, cached, Charset.forName("UTF-8"));
			} catch(IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static class YML extends DataHandler {

		private FileConfiguration cached;
		
		public YML(Plugin pl, String filename) {
			super(pl, filename);
		}
		
		/**
		 * A base constructor for each file type
		 * @param pl - A plugin instance to determine where to access data files from
		 * @param subtree - Where to drop/access the configuration file (starting from the program run location)
		 * @param filename - The name of a configuration file stored in src/main/resources usually preceded by a "/"
		 */
		public YML(Plugin pl, String subtree, String filename) {
			super(pl, subtree, filename);
		}
		
		/**
		 * @return The current YML file (will overwrite cached file-- may cause data loss)
		 */
		public FileConfiguration getYML() {
			cached = new YamlConfiguration();
			try {
				cached.load(file);
			} catch (Exception e) { e.printStackTrace(); }
			
			return cached;
		}
		
		/**
		 * @return The cached YML file (saves computation time)
		 */
		public FileConfiguration getCachedYML() {
			if(cached == null) return getYML();
			return cached;
		}
		
		/**
		 * Save the given YML file to the system
		 * @param fc - The FileConfiguration to save
		 */
		public void saveYML(FileConfiguration fc) {
			try {
				fc.save(file);
				cached = fc;
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public static class PROPS extends DataHandler {
		
		private Properties cached = new Properties();
		
		public PROPS(Plugin pl, String filename) {
			super(pl, filename);
		}
		
		/**
		 * A base constructor for each file type
		 * @param pl - A plugin instance to determine where to access data files from
		 * @param subtree - Where to drop/access the configuration file (starting from the program run location)
		 * @param filename - The name of a configuration file stored in src/main/resources usually preceded by a "/"
		 */
		public PROPS(Plugin pl, String subtree, String filename) {
			super(pl, subtree, filename);
		}
		
		/**
		 * @return The current properties file (will overwrite cached file-- may cause data loss)
		 */
		public Properties getProperties() {
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				cached.load(is);
			} catch(Exception e) { is = null; }
			return cached;
		}
		
		/**
		 * @return The cached properties file (saves computation time)
		 */
		public Properties getCachedProperties() {
			if(cached.isEmpty()) return getProperties();
			return cached;
		}
		
		/**
		 * Save the given properties file to the system
		 * @param props - A Properties file
		 */
		public void saveProperties(Properties props) {
			try {
				OutputStream out = new FileOutputStream(file);
				props.store(out, null);
			} catch(Exception e) { e.printStackTrace(); }
		}
	}
}