package me.senseiwells.arucas.utils.impl;

public class ArucasThread extends Thread {
	private long startTime;
	private boolean controlledStop;

	public ArucasThread(ThreadGroup threadGroup, Runnable runnable, String name) {
		super(threadGroup, runnable, name);
		this.controlledStop = false;
		this.setDaemon(true);
	}

	public synchronized void controlledStop() {
		if (!this.controlledStop) {
			this.controlledStop = true;
			this.interrupt();
		}
	}

	public boolean isStopControlled() {
		return this.controlledStop;
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
