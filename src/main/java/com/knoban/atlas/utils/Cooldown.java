package com.knoban.atlas.utils;

public class Cooldown {

	private double sec;
	private long start, finish;
	
	public Cooldown(double sec) {
		this.sec = sec;
		this.start = System.currentTimeMillis();
		this.finish = System.currentTimeMillis() + (long)(1000*sec);
	}
	
	public double getTotal() {
		return sec;
	}
	
	public long getStart() {
		return start;
	}
	
	public long getFinish() {
		return finish;
	}
	
	public double getPercentCompleted() {
		return getRemainingTime()/sec;
	}
	
	public boolean isFinished() {
		return System.currentTimeMillis() >= finish;
	}
	
	public long getLongRemainingTime() {
		return finish - System.currentTimeMillis();
	}
	
	public double getRemainingTime() {
		return Math.round((finish - System.currentTimeMillis())/100.0)/10.0;
	}
	
	public void add(double sec) {
		this.finish += (long)(1000*sec);
		this.sec += sec;
	}
}
