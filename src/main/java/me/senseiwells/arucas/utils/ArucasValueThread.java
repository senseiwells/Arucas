package me.senseiwells.arucas.utils;

public class ArucasValueThread extends Thread {
	private long startTime;

	public ArucasValueThread(ThreadGroup threadGroup, Runnable runnable, String name) {
		super(threadGroup, runnable, name);
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
