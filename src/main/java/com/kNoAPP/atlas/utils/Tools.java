package com.kNoAPP.atlas.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;

public class Tools {

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
    	List<Block> blocks = new ArrayList<Block>();
    	if(loc1.getWorld() == null || loc2.getWorld() == null) {
    		return blocks;
    	}
    	//if(!loc1.getChunk().isLoaded()) loc1.getChunk().load();
    	//if(!loc2.getChunk().isLoaded()) loc2.getChunk().load();
    	
        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
 
        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
 
        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
 
        for(int x = bottomBlockX; x <= topBlockX; x++) {
            for(int z = bottomBlockZ; z <= topBlockZ; z++) {
                for(int y = bottomBlockY; y <= topBlockY; y++) {
                	//Location l = new Location(loc1.getWorld(), x, y, z);
                	//if(!l.getChunk().isLoaded()) l.getChunk().load();
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }
    
    public static boolean isWithin(Location t, Location loc1, Location loc2) {
    	return t.getWorld() != null && loc1.getWorld() != null && loc2.getWorld() != null &&
    			t.getWorld().getName().equals(loc1.getWorld().getName()) &&
    			t.getWorld().getName().equals(loc2.getWorld().getName()) &&
    			Math.min(loc1.getX(), loc2.getX()) <= t.getX() && t.getX() <= Math.max(loc1.getX(), loc2.getX()) &&
    			Math.min(loc1.getY(), loc2.getY()) <= t.getY() && t.getY() <= Math.max(loc1.getY(), loc2.getY()) &&
    			Math.min(loc1.getZ(), loc2.getZ()) <= t.getZ() && t.getZ() <= Math.max(loc1.getZ(), loc2.getZ());
    }
    
    public static void broadcastSound(Sound s, Float v, Float p) {
    	for(Player pl : Bukkit.getOnlinePlayers()) {
    		pl.playSound(pl.getLocation(), s, v, p);
    	}
    }
    
    /**
     * Ignores Y-Axis
     */
	public static boolean intersects(Coordinate b1, Coordinate t1, Coordinate b2, Coordinate t2) {		
		return b1.getWorldName().equals(b2.getWorldName()) && t1.getZ() >= b2.getZ() && t1.getX() >= b2.getX() && b1.getZ() <= t2.getZ() && b1.getX() <= t2.getX();
	}
    
	public static void clearFullInv(Player p) {
		p.getInventory().clear();
		p.getInventory().setBoots(new ItemStack(Material.AIR, 1));
		p.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
		p.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
		p.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
		
		for(PotionEffect pe : p.getActivePotionEffects()) p.removePotionEffect(pe.getType());
	}
	
	public static int randomNumber(int min, int max) {
		Random rand = new Random();
		int val = rand.nextInt(max - min + 1) + min;
		return val;
	}
	
	public static String timeOutput(int timeInSeconds) {
	    int secondsLeft = timeInSeconds;
	    int minutes = secondsLeft / 60;
	    secondsLeft = secondsLeft - minutes * 60;
	    int seconds = secondsLeft;

	    String formattedTime = "";
	    //if(minutes < 10)
	       //formattedTime += "0";
	    formattedTime += minutes + ":";

	    if(seconds < 10)
	        formattedTime += "0";
	    formattedTime += seconds ;

	    return formattedTime;
	}
	
	public static String longTimeOutput(int timeInSeconds) {
	    int secondsLeft = timeInSeconds;
	    int hours = secondsLeft / (60*60);
	    secondsLeft = secondsLeft - hours * (60*60);
	    int minutes = secondsLeft / 60;
	    secondsLeft = secondsLeft - minutes * 60;
	    int seconds = secondsLeft;

	    String formattedTime = "";
	    if(hours < 10)
	       formattedTime += "0";
	    formattedTime += hours + "h ";
	    if(minutes < 10)
	       formattedTime += "0";
	    formattedTime += minutes + "m ";

	    if(seconds < 10)
	        formattedTime += "0";
	    formattedTime += seconds + "s";

	    return formattedTime;
	}
	
	public static Firework launchFirework(Location l, Color c, int power) {
		Firework fw = (Firework) l.getWorld().spawn(l, Firework.class);
		FireworkMeta data = fw.getFireworkMeta();
		data.setPower(power);
		data.addEffects(new FireworkEffect[]{FireworkEffect.builder().withColor(c).withColor(c).withColor(c).with(FireworkEffect.Type.BALL_LARGE).build()});
		fw.setFireworkMeta(data);
		return fw;
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
	
	public static Vector subtractVectors(Location from, Location to) {
		Vector fromV = new Vector(from.getX(), from.getY(), from.getZ());
		Vector toV  = new Vector(to.getX(), to.getY(), to.getZ());
		 
		Vector vector = toV.subtract(fromV);
		return vector;
	}
	
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
	
	public static boolean convertBoolean(int i) {
		if(i == 0) return false;
		else return true;
	}
	
	public static int convertInt(boolean b) {
		if(b) return 1;
		else return 0;
	}
	
	public static FileConfiguration getYML(File f) {
		FileConfiguration fc = new YamlConfiguration();
		try {
			fc.load(f);
		} catch (Exception e) {
			return null;
		}
		return fc;
	}
	
	public static boolean saveYML(FileConfiguration fc, File f) {
		try {
			fc.save(f);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static void sendTitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
		try {
			Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
					.invoke(null, "{\"text\": \"" + title + "\"}");
			Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
					getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
					int.class, int.class, int.class);
			Object packet = titleConstructor.newInstance(
					getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
					fadeInTime, showTime, fadeOutTime);

			Object chatsTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
					.invoke(null, "{\"text\": \"" + subtitle + "\"}");
			Constructor<?> stitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
					getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
					int.class, int.class, int.class);
			Object spacket = stitleConstructor.newInstance(
					getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null),
					chatsTitle, fadeInTime, showTime, fadeOutTime);

			sendPacket(player, packet);
			sendPacket(player, spacket);
		} catch (Exception ex) {
		}
	}

	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception ex) {}
	}

	public static void actionbarMessage(Player player, String msg) {
		try {
			Constructor<?> constructor = getNMSClass("PacketPlayOutChat")
					.getConstructor(getNMSClass("IChatBaseComponent"), getNMSClass("ChatMessageType"));

			Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
					.invoke(null, "{\"text\":\"" + msg + "\"}");
			Object packet = constructor.newInstance(icbc, getNMSClass("ChatMessageType").getEnumConstants()[2]);
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void adjustDamage(LivingEntity le, float dmg) {
		if(le instanceof Player) {
			Player p = (Player) le;
			try {
				//((EntityPlayer)p).damageEntity(DamageSource.GENERIC, dmg);
				Object handle = p.getClass().getMethod("getHandle").invoke(p);
				handle.getClass().getMethod("damageEntity", getNMSClass("DamageSource"), float.class).invoke(handle, getNMSClass("DamageSource").getField("GENERIC").get(null), dmg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else le.damage(dmg);
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
	
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch(NumberFormatException ex) {
			return false;
		}
		return true;
	}
	
	/**
	 * Build a hover/cmd component for Player.spigot().sendMessage(this.create());
	 * @param base - Base message
	 * @param hover - Hover message
	 * @param cmd - Command
	 * @return ComponentBuilder
	 */
	public static ComponentBuilder buildHoverWithCMD(String base, String hover, String cmd) {
		base = ChatColor.WHITE + base;
		hover = ChatColor.WHITE + hover;
		
		String[] bC = base.split("§");
		String[] hC = hover.split("§");
		
		ComponentBuilder bCB = new ComponentBuilder("");
		for(int bI=0; bI<bC.length; bI++) {
			String b = bC[bI];
			if(b.equals(bC[0])) continue;
			bCB.append(b.substring(1));
			
			int bBack = 0;
			boolean[] bdidModify = new boolean[]{false, false, false, false, false, false};
			bCB.reset();
			//bCB.bold(false); bCB.italic(false); bCB.strikethrough(false); bCB.underlined(false); bCB.obfuscated(false); bCB.color(ChatColor.WHITE.asBungee());
			top: while(bI-bBack > 0 && bC[bI-bBack] != null) {
				if(!bC[bI-bBack].substring(1).equals("") && bBack != 0) break top;
				ChatColor bBackCC = ChatColor.getByChar(bC[bI-bBack].charAt(0));
				
				if(bBackCC.asBungee() == ChatColor.BOLD.asBungee() && !bdidModify[0])  {
					bCB.bold(true);
					bdidModify[0] = true;
				}
				else if(bBackCC.asBungee() == ChatColor.ITALIC.asBungee() && !bdidModify[1]) {
					bCB.italic(true);
					bdidModify[1] = true;
				}
				else if(bBackCC.asBungee() == ChatColor.STRIKETHROUGH.asBungee() && !bdidModify[2]) {
					bCB.strikethrough(true);
					bdidModify[2] = true;
				}
				else if(bBackCC.asBungee() == ChatColor.UNDERLINE.asBungee() && !bdidModify[3]) {
					bCB.underlined(true);
					bdidModify[3] = true;
				}
				else if(bBackCC.asBungee() == ChatColor.MAGIC.asBungee() && !bdidModify[4]) {
					bCB.obfuscated(true);
					bdidModify[4] = true;
				}
				else if(!bdidModify[5]){
					bCB.color(bBackCC.asBungee());
					bdidModify[5] = true;
				}
				
				bBack++;
			}
			
			ComponentBuilder hCB = new ComponentBuilder("");
			for(int hI=0; hI<hC.length; hI++) {
				String h = hC[hI];
				if(h.equals(hC[0])) continue;
				hCB.append(h.substring(1));
				
				int hBack = 0;
				boolean[] hdidModify = new boolean[]{false, false, false, false, false, false};
				hCB.reset();
				//hCB.bold(false); hCB.italic(false); hCB.strikethrough(false); hCB.underlined(false); hCB.obfuscated(false); hCB.color(ChatColor.WHITE.asBungee());
				top: while(hI-hBack > 0 && hC[hI-hBack] != null) {
					if(!hC[hI-hBack].substring(1).equals("") && hBack != 0) break top;
					ChatColor hBackCC = ChatColor.getByChar(hC[hI-hBack].charAt(0));
					
					if(hBackCC.asBungee() == ChatColor.BOLD.asBungee() && !hdidModify[0])  {
						hCB.bold(true);
						hdidModify[0] = true;
					}
					else if(hBackCC.asBungee() == ChatColor.ITALIC.asBungee() && !hdidModify[1]) {
						hCB.italic(true);
						hdidModify[1] = true;
					}
					else if(hBackCC.asBungee() == ChatColor.STRIKETHROUGH.asBungee() && !hdidModify[2]) {
						hCB.strikethrough(true);
						hdidModify[2] = true;
					}
					else if(hBackCC.asBungee() == ChatColor.UNDERLINE.asBungee() && !hdidModify[3]) {
						hCB.underlined(true);
						hdidModify[3] = true;
					}
					else if(hBackCC.asBungee() == ChatColor.MAGIC.asBungee() && !hdidModify[4]) {
						hCB.obfuscated(true);
						hdidModify[4] = true;
					}
					else if(!hdidModify[5]){
						hCB.color(hBackCC.asBungee());
						hdidModify[5] = true;
					}
					
					hBack++;
				}
			}
			bCB.event(new HoverEvent(Action.SHOW_TEXT, hCB.create()));
			bCB.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
		}
		return bCB;
	}
	
	public static double round(double value, int precision) {
	    int scale = (int)Math.pow(10, precision);
	    return (double)Math.round(value * scale)/scale;
	}
	
	public static Object[] canParseToInteger(String s) {
		try {
			int i = Integer.parseInt(s);
			return new Object[]{true, i};
		} catch(NumberFormatException ex) {
			return new Object[]{false, 0};
		}
	}
	
	public static Object[] canParseToDouble(String s) {
		try {
			double d = Double.parseDouble(s);
			return new Object[]{true, d};
		} catch(NumberFormatException ex) {
			return new Object[]{false, 0.0};
		}
	}
	
	public static double randomNumber(double min, double max) {
	    Random r = new Random();
	    return (r.nextInt((int)((max-min)*10+1))+min*10) / 10.0;
	}
	
	public static Firework instantFirework(FireworkEffect fe, Location loc) {
        Firework f = (Firework) loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(fe);
        f.setFireworkMeta(fm);
        try {
            Class<?> entityFireworkClass = getClass("net.minecraft.server.", "EntityFireworks");
            Class<?> craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
            Object firework = craftFireworkClass.cast(f);
            Method handle = firework.getClass().getMethod("getHandle");
            Object entityFirework = handle.invoke(firework);
            Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
            Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return f;
    }
   
    private static Class<?> getClass(String prefix, String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        Class<?> nmsClass = Class.forName(name);
        return nmsClass;
    }
    
    public static void projTrail(Plugin pl, Projectile proj, Particle[] particles) {
		new BukkitRunnable() {
			public void run() {
				if(proj != null && proj.isValid() && !proj.isDead()) 
					for(Particle particle : particles) 
						proj.getWorld().spawnParticle(particle, proj.getLocation(), 1, 0F, 0F, 0F, 0.01);
				else this.cancel();
			}
		}.runTaskTimer(pl, 0L, 3L);
	}
    
    /*
	@SuppressWarnings("deprecation")
	public static ItemStack createPotion(PotionType pt, int level) {
		Potion pot = new Potion(pt, level, false);
		pot.setSplash(false);
		return pot.toItemStack(1);
	}
	*/
    
	/**
	 * A generic version of the selection sort algorithm. Here we can sort any type T that
	 * implements the Comparable interface.
	 */
	public static <T extends Comparable<T>> void selectionSort(T[] arr) {
		for(int i=0; i<arr.length-1; ++i) {
			int minIndex = i;
			for(int j=i+1; j<arr.length; ++j) if(arr[j].compareTo(arr[minIndex]) < 0) minIndex = j;
			// String changed to T
			T temp = arr[i];
			arr[i] = arr[minIndex];
			arr[minIndex] = temp;
		}
	}
	
	public static ItemStack buildPotion(Material m, PotionEffectType type, int ticks, int pow) {
		ItemStack is = new ItemStack(m);
		PotionMeta pm = (PotionMeta) is.getItemMeta();
		pm.addCustomEffect(new PotionEffect(type, ticks, pow), true);
		pm.setColor(type.getColor());
		if(m == Material.POTION) pm.setDisplayName(ChatColor.YELLOW + "Potion");
		if(m == Material.SPLASH_POTION) pm.setDisplayName(ChatColor.YELLOW + "Splash Potion");
		is.setItemMeta(pm);
		return is;
	}
	
	public static ItemStack buildEnchantment(Enchantment ench, int level, boolean ignoreLevelRestriction) {
		ItemStack is = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta esm = (EnchantmentStorageMeta) is.getItemMeta();
		esm.addStoredEnchant(ench, level, ignoreLevelRestriction);
		is.setItemMeta(esm);
		return is;
	}
	
	/**
	 * 75% - [+++++++++++++++-----] where + is left and - is right.
	 * @param prct
	 * @param total
	 * @param c
	 * @param left
	 * @param right
	 * @return
	 */
	public static String generateWaitBar(double prct, int total, String lc, String rc) {
		String s = "";
		for(double i=1; i<total; i++) {
			if(i/(double)total <= prct) s += lc;
			else s += rc; 
		}
		return s;
	}
}
