package com.knoban.ultimates.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class Items {
	
	public static final ItemStack RESPAWN_ITEM = getRespawnItem();
	public static final ItemStack LEVITATION_ITEM = getLevitationItem();

	private static ItemStack getRespawnItem() {
		ItemStack is = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Respawn Token");
		List<String> lores = new ArrayList<String>();
		lores.add(ChatColor.RED + "Requires 30 levels to craft.");
		lores.add(ChatColor.GRAY + "Get your items back after death.");
		im.setLore(lores);
		im.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		is.setItemMeta(im);
		return is;
	}
	
	private static ItemStack getLevitationItem() {
		ItemStack is = new ItemStack(Material.TORCH, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Levitation Wand");
		List<String> lores = new ArrayList<String>();
		lores.add(ChatColor.GREEN + "Right-click " + ChatColor.GRAY + "to pick up a block.");
		lores.add(ChatColor.GREEN + "Right-click " + ChatColor.GRAY + "to drop up a block.");
		lores.add(ChatColor.YELLOW + "Switch item slots " + ChatColor.GRAY + "to adjust levitation distance.");
		lores.add(ChatColor.RED + "Left-click " + ChatColor.GRAY + "to launch your block.");
		im.setLore(lores);
		is.setItemMeta(im);
		return is;
	}
	
	public static ShapedRecipe getRespawnRecipe() {
		ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft("ultimates"), Items.getRespawnItem());
		sr.shape("AAA", "BCB", "AAA");
		sr.setIngredient('A', Material.GOLD_BLOCK);
		sr.setIngredient('B', Material.EMERALD);
		sr.setIngredient('C', Material.END_CRYSTAL);
		return sr;
	}
}
