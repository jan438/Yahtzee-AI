package com.mylab;

public class DiceSelection {

	private boolean[] selection = new boolean[5];
	private String name;
	private double evalue = 0.0;

	public DiceSelection(boolean[] arr) {
		selection = arr;
		name = createName();
	}

	private String createName() {
		String result = "";
		for (int i = 0; i < selection.length; i++) {
			if (selection[i]) {
				result += "1";
			}
			else {
				result += "0";
			}
		}
		return result;
	}

	public String getName() {
		return name;
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

}
