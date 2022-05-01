package me.senseiwells.arucas.utils.impl;

import me.senseiwells.arucas.utils.Context;

public class ArucasThread extends Thread {
	private long startTime;
	private boolean controlledStop;

	public ArucasThread(ThreadGroup threadGroup, Runnable runnable, String name) {
		super(threadGroup, runnable, name);
		this.controlledStop = false;
		this.setDaemon(true);
	}

	public synchronized void controlledStop(Context context) {
		if (!this.controlledStop) {
			if (context.isDebug()) {
				context.getOutput().log("Manually Stopping Thread: " + this.getName());
			}
			this.controlledStop = true;
			this.interrupt();
		}
	}

	public boolean isStopControlled() {
		return this.controlledStop;
	}

	@Deprecated
	@Override
	public synchronized void start() {
		super.start();
		this.startTime = System.currentTimeMillis();
	}

	public synchronized ArucasThread start(Context context) {
		if (context.isDebug()) {
			context.getOutput().log("Starting Thread: " + this.getName() + "\n");
		}
		this.start();
		return this;
	}

	public synchronized long getStartTime() {
		return this.startTime;
	}
}
