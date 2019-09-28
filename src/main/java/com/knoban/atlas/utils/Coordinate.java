package com.knoban.atlas.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Coordinate {
	
	private String wn;
	private double x, y, z;
	private float yaw, pitch;
	
	/**
	 * Create a Coordinate from an unparsed String.
	 * @param unparsed - ex. world, 2.0, 3.0, 3.5, 0.1f, 0.2f
	 */
	public Coordinate(String[] args) {
		this(args[0], Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
	}
	
	public Coordinate(String wn, double x, double y, double z) {
		this(wn, x, y, z, 0F, 0F);
	}
	
	public Coordinate(String wn, double x, double y, double z, float yaw, float pitch) {
		this.wn = wn;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public Coordinate(Location l) {
		this.wn = l.getWorld().getName();
		this.x = l.getX();
		this.y = l.getY();
		this.z = l.getZ();
		this.yaw = l.getYaw();
		this.pitch = l.getPitch();
	}
	
	public World getWorld() {
		return Bukkit.getWorld(wn);
	}
	
	public String getWorldName() {
		return wn;
	}
	
	public double getX() {
		return x;
	}
	
	public int getBlockX() {
		return (int) x;
	}
	
	public Coordinate setX(double x) {
		this.x = x;
		return this;
	}
	
	public double getY() {
		return y;
	}
	
	public int getBlockY() {
		return (int) y;
	}
	
	public Coordinate setY(double y) {
		this.y = y;
		return this;
	}
	
	public double getZ() {
		return z;
	}
	
	public int getBlockZ() {
		return (int) z;
	}
	
	public Coordinate setZ(double z) {
		this.z = z;
		return this;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public Coordinate add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Coordinate toBlock() {
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
		this.yaw = 0F;
		this.pitch = 0F;
		return this;
	}
	
	public Location getLocation() {
		World w = getWorld();
		if(w == null) return null;
		
		Location l = new Location(w, x, y, z);
		l.setYaw(yaw);
		l.setPitch(pitch);
		return l;
	}
	
	public Coordinate clone() {
		return new Coordinate(wn, x, y, z, yaw, pitch);
	}
	
	public String serialize() {
		return wn + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
	}
	
	public static Coordinate deserialize(String s) {
		if(s == null) return null;
		
		String[] args = s.split(";");
		return new Coordinate(args[0], Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
	}
}
