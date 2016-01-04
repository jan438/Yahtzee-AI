package com.mylab;

public class DiceSelection {

	private boolean[] selection = new boolean[5];
	private double evalue = 0.0;

	public DiceSelection(boolean[] arr) {
		selection = arr;
	}

	public boolean[] getDiceSelection() {
		return selection;
	}

	public void resetEValue() {
		evalue = 0.0;
	}

	public void addEValue(double eval) {
		evalue += eval;
	}

	public double getEValue() {
		return evalue;
	}
	
	public String toString() {
		String s = "";
		for (int i = 0; i < selection.length; i++) {
			if (selection[i]) {
				s = s + "1";
			}
			else {
				s = s + "0";
			}
		}
		return s;
	}
}
