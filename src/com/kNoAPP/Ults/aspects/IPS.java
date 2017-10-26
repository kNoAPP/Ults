package com.kNoAPP.Ults.aspects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class IPS {
	
	public static List<IPS> ips = new ArrayList<IPS>();

	private UUID uuid;
	private long time;
	private int clicks;
	
	public IPS(Player p) {
		this.uuid = p.getUniqueId();
		this.time = System.currentTimeMillis();
		
		ips.add(this);
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public void remove() {
		ips.remove(this);
	}
	
	public void start() {
		time = System.currentTimeMillis();
		clicks = 0;
	}
	
	private long total() {
		return System.currentTimeMillis() - time;
	}
	
	public void add() {
		clicks++;
	}
	
	public double cps() {
		return (double)clicks/((double)total()/1000.0);
	}
	
	public static IPS getIPS(UUID uuid) {
		for(IPS uips : ips) if(uips.getUUID() == uuid) return uips;
		return null;
	}
}
