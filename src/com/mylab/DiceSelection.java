package com.mylab;

import java.util.*;

public class DiceSelection {

	private boolean[] selection = new boolean[5];
	private String name;
	private List<DiceCombination> allDiceCombinations = new ArrayList<DiceCombination>();
	private double evalue = 0.0;

	public DiceSelection(boolean[] arr) {
		selection = arr;
		name = createName();
	}

	private String createName() {
		String result = "";
		for (int i = 0; i < selection.length; i++) {
			result += selection[i];
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

	public void setDiceCombinations(List<DiceCombination> list) {
		allDiceCombinations = list;
	}

	public void addEValue(double eval) {
		evalue += eval;
	}

	public double getEValue() {
		return evalue;
	}

	public Iterator<DiceCombination> getDiceCombinationsIterator() {
		Iterator<DiceCombination> it = allDiceCombinations.iterator();
		return it;
	}

}
