package com.kNoAPP.enchants;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import com.kNoAPP.Ults.Ultimates;

public abstract class CustomEnchant extends Enchantment {
	
	private static boolean registered = false;

	public CustomEnchant(String name) {
		super(new NamespacedKey(Ultimates.getPlugin(), name));
	}

	public abstract String getName();
	
	public abstract EnchantmentTarget getItemTarget();
	
	public abstract boolean canEnchantItem(ItemStack item);
	
	public abstract int getMaxLevel();
	
	public abstract int getEnchantmentChance();
	
	public abstract boolean conflictsWith(Enchantment other);

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public boolean isCursed() {
		return false;
	}
	
	public static void registerEnchantments() {
	    if(registered) return;
	    
	    try {
	        Field f = Enchantment.class.getDeclaredField("acceptingNew");
	        f.setAccessible(true);
	        f.set(null, true);
	        Enchantment.registerEnchantment(EnchantStomper.STOMPER);
	        registered = true;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@SuppressWarnings("unchecked")
	public static void unregisterEnchantments() {
		try {
		    Field keyField = Enchantment.class.getDeclaredField("byKey");
		 
		    keyField.setAccessible(true);
		    HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);
		    byKey.remove(EnchantStomper.STOMPER.getKey());
		} catch (Exception ignored) { }
	}
}
