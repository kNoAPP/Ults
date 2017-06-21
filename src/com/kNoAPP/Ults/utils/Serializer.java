package com.kNoAPP.Ults.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Serializer {

	public static String compress(Location l) {
		return l.getWorld().getName() + ";" + l.getX() + ";" + l.getY() + ";" + l.getZ();
	}
	
	public static Location expand(String l) {
		String[] peal = l.split(";");
		return new Location(Bukkit.getWorld(peal[0]), Double.parseDouble(peal[1]), Double.parseDouble(peal[2]), Double.parseDouble(peal[3]));
	}
}
