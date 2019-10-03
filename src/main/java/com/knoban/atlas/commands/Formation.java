package com.knoban.atlas.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * A Formation to properly define the arguments of a command.
 * @author Alden Bansemer (kNoAPP)
 */
public class Formation {
	
	public static final int INVALID = -1, PLAYER = 0, NUMBER = 1, LIST = 2, STRING = 3;

	private int[] args;
	private ArrayList<Double[]> numbers;
	private ArrayList<List<String>> lists;
	
	private Formation(int[] args, ArrayList<Double[]> numbers, ArrayList<List<String>> lists) {
		this.args = args;
		this.numbers = numbers;
		this.lists = lists;
	}
	
	/**
	 * Used in conjunction with CommandInfo#aliasIgnore to throw out a passed
	 * number of args from the formation. Args are removed from the beginning and
	 * remaining ones are shifted over.
	 * @param firstArgs - How many of the firstArgs to remove
	 * @return A new Formation shifted left.
	 */
	public Formation ignoreFirst(int firstArgs) {
		if(firstArgs > args.length)
			throw new IndexOutOfBoundsException("Param firstArgs cannot be greater than the Formation args! "
					+ firstArgs + "<=" + args.length);
		if(firstArgs < 0)
			throw new IndexOutOfBoundsException("Param firstArgs cannot be less than 0!");
		return new Formation(Arrays.copyOfRange(args, firstArgs, args.length),
				new ArrayList<Double[]>(numbers.subList(firstArgs, numbers.size())),
				new ArrayList<List<String>>(lists.subList(firstArgs, lists.size())));
	}
	
	/**
	 * Finds the last argument to match the Formation. 
	 * -1 - if no match
	 * 0 - if first arg matches
	 * etc...
	 * @param args - The args to check against the Formation.
	 * @return The last argument to match the Formation (-1 to (args.length-1))
	 */
	public int lastMatch(String[] args) {
		int lastMatch = -1;
		for(int i=0; i<args.length; i++) {
			int type = getArgType(i);
			switch(type) {
			case Formation.PLAYER:
				//Could check if valid player, but also what if offline player?
				break;
			case Formation.NUMBER:
				try {
					Double.parseDouble(args[i]);
				} catch(NumberFormatException e) {
					return lastMatch;
				}
				break;
			case Formation.LIST:
				if(!getList(i).stream().anyMatch(args[i]::equalsIgnoreCase))
					return lastMatch;
				break;
			case Formation.STRING:
				//Can be any string
				break;
			default:
				return lastMatch;
			}
			lastMatch++;
		}
		return lastMatch;
	}
	
	/**
	 * Do these args match the Formation?
	 * This does not check if the args length is less than the Formation's.
	 * @param args - The args to check
	 * @return True, if the args match the Formation perfectly.
	 */
	public boolean isMatch(String[] args) {
		return lastMatch(args) == args.length - 1;
	}
	
	/**
	 * Returns an integer representing the requested ArgType for an index
	 * -1 - Invalid
	 * 0 - Player
	 * 1 - Number (Double)
	 * 2 - List (Required String)
	 * 3 - String (Recommended/Optional String)
	 * @param index
	 * @return
	 */
	public int getArgType(int index) {
		if(index < 0 || args.length <= index)
			return -1;
		
		return args[index];
	}
	
	/**
	 * Gets a list off all Online Player's names the passed Player can see.
	 * @param p - A Player
	 * @return List of Player's names
	 */
	public List<String> getPlayer(Player p) {
		return Bukkit.getOnlinePlayers().stream().filter(t -> p.canSee(t)).map(Player::getName).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list off all Online Player's names.
	 * @return List of Player's names
	 */
	public List<String> getPlayer() {
		return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
	}
	
	/**
	 * Gets a list of numbers based on the FormationBuilder's min, max, and step.
	 * @param index - The index of the command (matches FormationBuilder's order)
	 * @return List of Strings (all parsable to Double)
	 */
	public List<String> getNumber(int index) {
		Double[] params = numbers.get(index);
		if(args == null)
			return null;

		List<String> out = new ArrayList<String>();
		for(double d = params[0]; d <= params[1]; d += params[2])
			out.add(d == Math.floor(d) ? String.valueOf((int) d): String.valueOf(d));
		return out;
	}
	
	/**
	 * Gets a list of required Strings based on the FormationBuilder
	 * @param index - The index of the command (matches FormationBuilder's order)
	 * @return List of required Strings
	 */
	public List<String> getList(int index) {
		return lists.get(index);
	}
	
	/**
	 * Gets a list of recommended Strings based on the FormationBuilder
	 * @param index - The index of the command (matches FormationBuilder's order)
	 * @return List of recommended Strings
	 */
	public List<String> getString(int index) {
		return lists.get(index);
	}
	
	/**
	 * Used to build a Formation.
	 * @author Alden Bansemer (kNoAPP)
	 */
	public static class FormationBuilder {
    	
    	private List<Integer> builder = new ArrayList<Integer>();
    	private ArrayList<Double[]> numbers = new ArrayList<Double[]>();
    	private ArrayList<List<String>> lists = new ArrayList<List<String>>();
    	
    	/**
    	 * Adds an argument that expects an Online or Offline player.
    	 * No String validation occurs.
    	 */
    	public FormationBuilder player() {
    		builder.add(PLAYER);
    		return this;
    	}
    	
    	/**
    	 * Adds an argument that expects a number (Double).
    	 * Double validation occurs.
    	 */
    	public FormationBuilder number(double low, double high, double step) {
			builder.add(NUMBER);
    		for(int i=numbers.size(); i<builder.size()-1; i++)
    			numbers.add(null);
    		numbers.add(new Double[]{low, high, step});
    		return this;
    	}
    	
    	/**
    	 * Adds an argument that expects one of the passed Strings.
    	 * String validation occurs.
    	 */
    	public FormationBuilder list(String... data) {
    		builder.add(LIST);
			for(int i=lists.size(); i<builder.size()-1; i++)
				numbers.add(null);
			lists.add(Arrays.asList(data)); //Shared ArrayList
    		return this;
    	}
    	
    	/**
    	 * Adds an argument that recommends the passed Strings.
    	 * No String validation occurs.
    	 */
    	public FormationBuilder string(String... data) {
			builder.add(STRING);
			for(int i=lists.size(); i<builder.size()-1; i++)
				numbers.add(null);
    		lists.add(Arrays.asList(data)); //Shared ArrayList
    		return this;
    	}
    	
    	/**
    	 * Call this last to build FormationBuilder into a Formation
    	 * @return Formation for a command
    	 */
    	public Formation build() {
    		int[] args = new int[builder.size()];
    		
    		for(int i=0; i<builder.size(); i++)
    			args[i] = builder.get(i);
    		
    		return new Formation(args, numbers, lists);
    	}
    }
}
