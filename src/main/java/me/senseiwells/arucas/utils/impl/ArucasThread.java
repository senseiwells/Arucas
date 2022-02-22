package me.senseiwells.arucas.utils.impl;

public class ArucasThread extends Thread {
	private long startTime;

	public ArucasThread(ThreadGroup threadGroup, Runnable runnable, String name) {
		super(threadGroup, runnable, name);
		this.setDaemon(true);
	}

	@Override
	public synchronized void start() {
		super.start();
		this.startTime = System.currentTimeMillis();
	}

	public synchronized long getStartTime() {
		return this.startTime;
	}
}
