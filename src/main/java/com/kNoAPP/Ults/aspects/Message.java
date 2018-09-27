package com.kNoAPP.Ults.aspects;

import org.bukkit.ChatColor;

public enum Message {

	MISSING(ChatColor.GOLD + "Permission> "),
	ARGS(ChatColor.GOLD + "Missing Args> "),
	USAGE(ChatColor.GOLD + "Usage> "),
	
	RECALL(ChatColor.GOLD + "Recall> "),
	CHUNK(ChatColor.GOLD + "Chunk> "),
	RESPAWN(ChatColor.GOLD + "Respawn> "),
	
	SCRAMBLE(ChatColor.GOLD + "Scramble> "),
	SOUNDGEN(ChatColor.GOLD + "SoundGen> ");
	
	private String prefix;
	
	private Message(String prefix) {
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getMessage(String s) {
		if(this == MISSING) {
			return prefix + ChatColor.GRAY + "You are missing Node [" + ChatColor.DARK_AQUA + s + ChatColor.GRAY + "]!";
		}
		if(this == ARGS || this == USAGE || this == RECALL || this == CHUNK || this == RESPAWN || this == SCRAMBLE || this == SOUNDGEN) {
			return prefix + ChatColor.GRAY + s;
		}
		return null;
	}
}
