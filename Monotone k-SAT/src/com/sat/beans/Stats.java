package com.sat.beans;

public class Stats {
private boolean solved;
private int conflicts;
private int decisions;
private int propagations;
	private double time;

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}

	public int getConflicts() {
		return conflicts;
	}

	public void setConflicts(int conflicts) {
		this.conflicts = conflicts;
	}

	public int getDecisions() {
		return decisions;
	}

	public void setDecisions(int decisions) {
		this.decisions = decisions;
	}

	public int getPropagations() {
		return propagations;
	}

	public void setPropagations(int propagations) {
		this.propagations = propagations;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
}
