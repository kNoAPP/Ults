package com.kNoAPP.atlas.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Formation {
	
	public static final int INVALID = -1, PLAYER = 0, NUMBER = 1, LIST = 2;

	private int[] args;
	private HashMap<Integer, Double[]> numbers;
	private HashMap<Integer, List<String>> lists;
	
	private Formation(int[] args, HashMap<Integer, Double[]> numbers, HashMap<Integer, List<String>> lists) {
		this.args = args;
		this.numbers = numbers;
		this.lists = lists;
	}
	
	public int getArgType(int index) {
		if(index >= args.length || 0 > index)
			return -1;
		
		return args[index];
	}
	
	public List<String> getPlayer(Player p) {
		return Bukkit.getOnlinePlayers().stream().filter(t -> p.canSee(t)).map(Player::getName).collect(Collectors.toList());
	}
	
	public List<String> getPlayer() {
		return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
	}
	
	public List<String> getNumber(int index) {
		Double[] params = numbers.get(index);
		if(args == null)
			return null;

		List<String> out = new ArrayList<String>();
		for(double d = params[0]; d < params[1]; d += params[2])
			out.add(d == Math.floor(d) ? String.valueOf((int) d): String.valueOf(d));
		return out;
	}
	
	public List<String> getList(int index) {
		return lists.get(index);
	}
	
	public static class FormationBuilder {
    	
    	private List<Integer> builder = new ArrayList<Integer>();
    	private HashMap<Integer, Double[]> numbers = new HashMap<Integer, Double[]>();
    	private HashMap<Integer, List<String>> lists = new HashMap<Integer, List<String>>();
    	
    	public FormationBuilder player() {
    		builder.add(0);
    		return this;
    	}
    	
    	public FormationBuilder number(double low, double high, double step) {
    		numbers.put(builder.size(), new Double[]{low, high, step});
    		builder.add(1);
    		return this;
    	}
    	
    	public FormationBuilder list(String... data) {
    		lists.put(builder.size(), Arrays.asList(data));
    		builder.add(2);
    		return this;
    	}
    	
    	public Formation build() {
    		int[] args = new int[builder.size()];
    		
    		for(int i=0; i<builder.size(); i++)
    			args[i] = builder.get(i);
    		
    		return new Formation(args, numbers, lists);
    	}
    }
}
