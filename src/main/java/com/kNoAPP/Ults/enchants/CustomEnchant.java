package com.kNoAPP.Ults.enchants;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

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
	
	// ItemMeta#hasEnchant() may also work for this function
	public static boolean hasEnchantment(ItemStack item, Enchantment enchant){
	    if(item.getItemMeta() != null && item.getItemMeta().getEnchants() != null && item.getItemMeta().getEnchants().size() > 0){
	        for (Iterator<java.util.Map.Entry<Enchantment, Integer>> it = item.getItemMeta().getEnchants().entrySet().iterator(); it.hasNext();) {
	            java.util.Map.Entry<Enchantment, Integer> e = it.next();
	            if(e.getKey().equals(enchant)) {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	// ItemMeta#getEnchantLevel() may also work for this function
	public static int getLevel(ItemStack item, Enchantment enchant){
	    if(item.getItemMeta() != null && item.getItemMeta().getEnchants() != null && item.getItemMeta().getEnchants().size() > 0){
	        for (Iterator<java.util.Map.Entry<Enchantment, Integer>> it = item.getItemMeta().getEnchants().entrySet().iterator(); it.hasNext();) {
	            java.util.Map.Entry<Enchantment, Integer> e = it.next();
	            if(e.getKey().equals(enchant)) {
	                return e.getValue();
	            }
	        }
	    }
	    return 0;
	}
	
	private static final String[] NUMERALS = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X" };
	@SuppressWarnings("deprecation")
	public static String returnEnchantmentName(Enchantment ench, int enchLevel){
	    if(enchLevel == 1 && ench.getMaxLevel() == 1)
	        return ench.getName();
	    
	    if(enchLevel > 10 || enchLevel <= 0)
	        return ench.getName() + " enchantment.level." + enchLevel;

	    return ench.getName() + " " + NUMERALS[enchLevel- 1];
	}
}
