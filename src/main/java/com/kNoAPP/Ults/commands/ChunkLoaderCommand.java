package com.kNoAPP.Ults.commands;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.kNoAPP.Ults.aspects.Actions;
import com.kNoAPP.Ults.aspects.Message;
import com.kNoAPP.Ults.data.Data;
import com.kNoAPP.Ults.utils.Serializer;

public class ChunkLoaderCommand extends CommandHandler {
	
	public ChunkLoaderCommand(boolean allowConsole, String usage, String permission, int argMin, GenericType... format) {
		super(allowConsole, usage, permission, argMin, format);
	}

	public ChunkLoaderCommand(boolean allowConsole, String usage, String permission, GenericType... format) {
		super(allowConsole, usage, permission, format);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		FileConfiguration fc = Data.CONFIG.getCachedYML();
		List<String> chunksR = fc.getStringList("Chunk.Load");
		List<Chunk> chunks = Actions.convert(chunksR);
		switch(args.length) {
		case 0:
			if(Actions.isFrozen(chunks, p.getLocation().getChunk())) p.sendMessage(Message.CHUNK.getMessage("This chunk is frozen."));
			else p.sendMessage(Message.CHUNK.getMessage("This chunk is normal."));
			return true;
		case 1:
			Chunk pc = p.getLocation().getChunk();
			if(args[0].equalsIgnoreCase("freeze")) {
				if(!Actions.isFrozen(chunks, pc)) {
					chunksR.add(Serializer.compress(p.getLocation()));
					fc.set("Chunk.Load", chunksR);
					Data.CONFIG.saveYML(fc);
					
					p.sendMessage(Message.CHUNK.getMessage("Chunk(" + pc.getX() + ", " + pc.getZ() + ") has been frozen."));
				} else p.sendMessage(Message.CHUNK.getMessage("Chunk already frozen."));
				return true;
			} else if(args[0].equalsIgnoreCase("unfreeze")) {
				if(Actions.isFrozen(chunks, pc)) {
					chunksR.remove(getEntry(chunksR, pc));
					fc.set("Chunk.Load", chunksR);
					Data.CONFIG.saveYML(fc);
					
					p.sendMessage(Message.CHUNK.getMessage("Chunk(" + pc.getX() + ", " + pc.getZ() + ") has been unfrozen."));
				} else p.sendMessage(Message.CHUNK.getMessage("Chunk not frozen."));
				return true;
			}
			return false;
		}
		return false;
	}
	
	private String getEntry(List<String> raw, Chunk cc) {
		for(String s : raw) {
			Chunk c = Serializer.expand(s).getChunk();
			if(Actions.isSimilar(c, cc)) return s;
		}
		return null;
	}
}
