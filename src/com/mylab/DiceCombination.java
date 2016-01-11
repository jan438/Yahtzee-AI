package com.mylab;

import java.util.*;

public class DiceCombination {

	private int[] combination = new int[5];
	private int category;
	private int score;
	private double probability;
	private double eValue;

	public DiceCombination(int[] dice) {
		combination = dice;
	}

	public void updateCombination(int cat, int sc, boolean[] selectedDice) {
		category = cat;
		score = sc;
		int diceRerolled = 0;
		for (int i = 0; i < selectedDice.length; i++) {
			if (selectedDice[i] == true)
				diceRerolled++;
		}
		probability = Math.pow(1.0 / 6.0, diceRerolled);
		eValue = probability * (double) score;
	}

	public boolean[] getNonmatchingDiceForReroll(int[] dice) {
		boolean[] diceSelections = new boolean[5];
		for (int i = 0; i < diceSelections.length; i++) {
			diceSelections[i] = true;
		}
		List<Integer> diceList = new ArrayList<Integer>();
		for (int i = 0; i < dice.length; i++) {
			diceList.add(dice[i]);
		}
		for (int i = 0; i < combination.length; i++) {
			Integer die = combination[i];
			int index = diceList.indexOf(die);
			if (index != -1) {
				diceList.set(index, 0);
				diceSelections[index] = false;
			} else {

			}
		}
		return diceSelections;
	}

	public int[] getCombination() {
		return combination;
	}

	public int getCategory() {
		return category;
	}

	public int getScore() {
		return score;
	}

	public double getEValue() {
		return eValue;
	}

	public double getProbability() {
		return probability;
	}

	public String toString() {
		String result = "[ ";
		for (int i = 0; i < combination.length; i++) {
			result += combination[i] + " ";
		}
		result += "]";
		return result;
	}

}
