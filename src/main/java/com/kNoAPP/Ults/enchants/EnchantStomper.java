package com.kNoAPP.Ults.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class EnchantStomper extends CustomEnchant {
	
	public static Enchantment STOMPER = new EnchantStomper();

	public EnchantStomper() {
		super("stomper");
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "Stomper";
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR_FEET;
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return item.getType() == Material.LEATHER_BOOTS
				|| item.getType() == Material.GOLDEN_BOOTS
				|| item.getType() == Material.CHAINMAIL_BOOTS
				|| item.getType() == Material.IRON_BOOTS
				|| item.getType() == Material.DIAMOND_BOOTS;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getEnchantmentChance() {
		return 0;
	}
	
	public boolean conflictsWith(Enchantment other) {
		return other instanceof EnchantStomper;
	}
}
