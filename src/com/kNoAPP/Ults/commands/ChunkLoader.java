package com.kNoAPP.Ults.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.kNoAPP.Ults.Ultimates;
import com.kNoAPP.Ults.aspects.Message;
import com.kNoAPP.Ults.data.Data;
import com.kNoAPP.Ults.utils.Serializer;

public class ChunkLoader implements CommandExecutor, Listener {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("chunk")) {
				FileConfiguration fc = Data.MAIN.getFileConfig();
				List<String> chunksR = fc.getStringList("Chunk.Load");
				List<Chunk> chunks = convert(chunksR);
				if(args.length == 0) {
					if(isFrozen(chunks, p.getLocation().getChunk())) {
						p.sendMessage(Message.CHUNK.getMessage("This chunk is frozen."));
					} else {
						p.sendMessage(Message.CHUNK.getMessage("This chunk is unfrozen."));
					}
					return true;
				}
				if(args.length == 1) {
					Chunk pc = p.getLocation().getChunk();
					if(args[0].equalsIgnoreCase("freeze")) {
						if(!isFrozen(chunks, pc)) {
							chunksR.add(Serializer.compress(p.getLocation()));
							fc.set("Chunk.Load", chunksR);
							Data.MAIN.saveDataFile(fc);
							
							p.sendMessage(Message.CHUNK.getMessage("Chunk(" + pc.getX() + ", " + pc.getZ() + ") has been frozen."));
							return true;
						} else {
							p.sendMessage(Message.CHUNK.getMessage("Chunk already frozen."));
							return false;
						}
					}
					if(args[0].equalsIgnoreCase("unfreeze")) {
						if(isFrozen(chunks, pc)) {
							chunksR.remove(getEntry(chunksR, pc));
							fc.set("Chunk.Load", chunksR);
							Data.MAIN.saveDataFile(fc);
							
							p.sendMessage(Message.CHUNK.getMessage("Chunk(" + pc.getX() + ", " + pc.getZ() + ") has been unfrozen."));
							return true;
						} else {
							p.sendMessage(Message.CHUNK.getMessage("Chunk not frozen."));
							return false;
						}
					}
				}
				p.sendMessage(Message.USAGE.getMessage("/chunk [freeze/unfreeze]"));
				return false;
			}
		}
		return false;
	}
	
	public static List<Chunk> convert(List<String> raw) {
		List<Chunk> chunks = new ArrayList<Chunk>();
		for(String c : raw) {
			chunks.add(Serializer.expand(c).getChunk());
		}
		return chunks;
	}
	
	private boolean isSimilar(Chunk c1, Chunk c2) {
		return (c1.getX() == c2.getX() && c1.getZ() == c2.getZ() && c1.getWorld().getName().equals(c2.getWorld().getName()));
	}
	
	private boolean isFrozen(List<Chunk> chunks, Chunk cc) {
		for(Chunk c : chunks) {
			if(isSimilar(cc, c)) {
				return true;
			}
		}
		return false;
	}
	
	private String getEntry(List<String> raw, Chunk cc) {
		for(String s : raw) {
			Chunk c = Serializer.expand(s).getChunk();
			if(isSimilar(c, cc)) {
				return s;
			}
		}
		return null;
	}
	
	public static void load() {
		FileConfiguration fc = Data.MAIN.getFileConfig();
		List<String> chunksR = fc.getStringList("Chunk.Load");
		List<Chunk> chunks = convert(chunksR);
		
		for(Chunk c : chunks) {
			c.load();
			Ultimates.getPlugin().getLogger().info("Chunk(" + c.getX() + ", " + c.getZ() + ") has been loaded!");
		}
	}
	
	@EventHandler
	public void onUnload(ChunkUnloadEvent e) {
		FileConfiguration fc = Data.MAIN.getFileConfig();
		List<String> chunksR = fc.getStringList("Chunk.Load");
		List<Chunk> chunks = convert(chunksR);
		
		if(isFrozen(chunks, e.getChunk())) {
			e.setCancelled(true);
			Ultimates.getPlugin().getLogger().info("Chunk(" + e.getChunk().getX() + ", " + e.getChunk().getZ() + ") tried to unload!");
		}
	}
}
