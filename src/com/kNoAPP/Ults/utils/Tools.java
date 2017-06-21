package com.kNoAPP.Ults.utils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class Tools {

	public static Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }
	
	public static Block floor(Block b) {
		while(b.getType() == Material.AIR) {
			b = b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ());
		}
		return b;
	}
	
	public static void sendActionbar(Player player, String msg) {
		try {
			Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), getNMSClass("ChatMessageType"));
		       
		    Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + msg + "\"}");
		    Object packet = constructor.newInstance(icbc, getNMSClass("ChatMessageType").getEnumConstants()[2]);
		    Object entityPlayer= player.getClass().getMethod("getHandle").invoke(player);
		    Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

		    playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			  e.printStackTrace();
		}
	}

	public static Class<?> getNMSClass(String name) {
		try {
		    return Class.forName("net.minecraft.server." + getVersion() + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
		 
	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
	
	public static String generateWaitBar(double prct, int total, ChatColor left, ChatColor done) {
		String s = "";
		for(int i=1; i<total; i++) {
			if(((double)i/(double)total) * 100 >= prct) {
				s += done + "☕";
			} else {
				s += left + "☕"; 
			}
		}
		return s;
	}
	
	public static double round(double value, int precision) {
	    int scale = (int) Math.pow(10, precision);
	    return (double) Math.round(value * scale) / scale;
	}
	
	public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                   blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
	
	public static int randomNumber(int min, int max) {
		Random rand = new Random();
		int val = rand.nextInt(max - min + 1) + min;
		return val;
	}
	
	public static Vector subtractVectors(Location from, Location to) {
		Vector fromV = new Vector(from.getX(), from.getY(), from.getZ());
		Vector toV  = new Vector(to.getX(), to.getY(), to.getZ());
		 
		Vector vector = toV.subtract(fromV);
		return vector;
	}
	
	public static void removeAllEffects(LivingEntity le) {
		for(PotionEffect pe : le.getActivePotionEffects()) {
			le.removePotionEffect(pe.getType());
		}
	}
	
	public static void addAllEffects(LivingEntity le, Collection<PotionEffect> pes) {
		for(PotionEffect pe : pes) {
			le.addPotionEffect(pe);
		}
	}
}
