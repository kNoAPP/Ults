package com.knoban.ultimates.commands;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.knoban.ultimates.Ultimates;
import com.knoban.ultimates.aspects.Message;
import com.knoban.atlas.commands.AtlasCommand;
import com.knoban.atlas.commands.CommandInfo;
import com.knoban.atlas.commands.Formation;
import com.knoban.atlas.commands.Formation.FormationBuilder;
import com.knoban.atlas.data.DataHandler.JSON;

@CommandInfo(name = "chunk", description = "Freeze and unfreeze chunks", usage = "/chunk (freeze | unfreeze)", length = {0, 1})
public class ChunkLoaderCommand extends AtlasCommand {
	
	private final Formation FORM = new FormationBuilder().list("freeze", "unfreeze").build();
	
	private HashSet<ChunkLocation> frozen = new HashSet<ChunkLocation>();
	
	public ChunkLoaderCommand(JSON fromFile) {
		ChunkLocation[] chunks = (ChunkLocation[]) fromFile.getCachedJSON(ChunkLocation[].class);
		if(chunks != null) {
			for(ChunkLocation cl : chunks) {
				frozen.add(cl);
				World w = Bukkit.getWorld(cl.getWorld());
				if(w != null)
					w.getChunkAt(cl.getX(), cl.getZ()).setForceLoaded(true);
			}
		}
	}
	
	@Override
	public boolean onCommand(Player sender, String label, String[] args) {
		switch(args.length) {
		case 0:
			ChunkLocation testFor = new ChunkLocation(sender.getLocation().getChunk());
			if(frozen.contains(testFor)) sender.sendMessage(Message.CHUNK.getMessage("This chunk is frozen."));
			else sender.sendMessage(Message.CHUNK.getMessage("This chunk is normal."));
			return true;
		case 1:
			ChunkLocation cl = new ChunkLocation(sender.getLocation().getChunk());
			if(args[0].equalsIgnoreCase("freeze")) {
				if(!frozen.contains(cl)) {
					frozen.add(cl);
					sender.getLocation().getChunk().setForceLoaded(true);
					sender.sendMessage(Message.CHUNK.getMessage("Chunk(" + cl.getX() + ", " + cl.getZ() + ") has been frozen."));
				} else sender.sendMessage(Message.CHUNK.getMessage("Chunk already frozen."));
			} else if(args[0].equalsIgnoreCase("unfreeze")) {
				if(frozen.contains(cl)) {
					frozen.remove(cl);
					sender.getLocation().getChunk().setForceLoaded(false);
					
					sender.sendMessage(Message.CHUNK.getMessage("Chunk(" + cl.getX() + ", " + cl.getZ() + ") has been unfrozen."));
				} else sender.sendMessage(Message.CHUNK.getMessage("Chunk not frozen."));
			}
			return true;
		}
		return true;
	}
	
	public void save(JSON toFile) {
		toFile.saveJSON(frozen.toArray(new ChunkLocation[0]));
	}

	@Override
	protected Formation getFormation(CommandSender sender) {
		return FORM;
	}
	
	@EventHandler
	public void onUnload(ChunkUnloadEvent e) {
		if(frozen.contains(new ChunkLocation(e.getChunk())))
			Ultimates.getPlugin().getLogger().info("Chunk(" + e.getChunk().getX() + ", " + e.getChunk().getZ() + ") was forcefully unloaded somehow?");
	}
	
	public static class ChunkLocation {
		
		private String world;
		private int x, z;
		
		public ChunkLocation() {}
		
		public ChunkLocation(Chunk c) {
			this.x = c.getX();
			this.z = c.getZ();
			this.world = c.getWorld().getName();
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getZ() {
			return z;
		}

		public void setZ(int z) {
			this.z = z;
		}

		public String getWorld() {
			return world;
		}

		public void setWorld(String world) {
			this.world = world;
		}
		
		@Override
		public int hashCode() {
			int hash = 3;

			hash = 19 * hash + (this.world != null ? this.world.hashCode() : 0);
			hash = 19 * hash + (int) (Double.doubleToLongBits(this.x/16) ^ (Double.doubleToLongBits(this.x/16) >>> 32));
			hash = 19 * hash + (int) (Double.doubleToLongBits(this.z/16) ^ (Double.doubleToLongBits(this.z/16) >>> 32));
			return hash;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof ChunkLocation))
				return false;

			ChunkLocation c = (ChunkLocation) obj;
			return c.getX() == x && c.getZ() == z && c.getWorld().equals(world);
		}
	}
}
