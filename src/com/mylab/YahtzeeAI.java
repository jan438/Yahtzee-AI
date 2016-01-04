package com.mylab;

import java.util.*;

public class YahtzeeAI {

	public static final int N_CATEGORIES = 17;

	public static final int N_SCORING_CATEGORIES = 13;

	public static final int ONES = 1;
	public static final int TWOS = 2;
	public static final int THREES = 3;
	public static final int FOURS = 4;
	public static final int FIVES = 5;
	public static final int SIXES = 6;
	public static final int UPPER_SCORE = 7;
	public static final int UPPER_BONUS = 8;
	public static final int THREE_OF_A_KIND = 9;
	public static final int FOUR_OF_A_KIND = 10;
	public static final int FULL_HOUSE = 11;
	public static final int SMALL_STRAIGHT = 12;
	public static final int LARGE_STRAIGHT = 13;
	public static final int YAHTZEE = 14;
	public static final int CHANCE = 15;
	public static final int LOWER_SCORE = 16;
	public static final int TOTAL = 17;

	public static final int FULL_HOUSE_SCORE = 25;
	public static final int SMALL_STRAIGHT_SCORE = 30;
	public static final int LARGE_STRAIGHT_SCORE = 40;
	public static final int YAHTZEE_SCORE = 50;
	public static final int UPPER_BONUS_SCORE = 35;

	private static int scorecard[];
	private static boolean[] categoryHasBeenChosen;
	private static int delay = 500;
	private final static Map<String, DiceSelection> allSelections = new HashMap<String, DiceSelection>();
	private final static List<String> categories = new ArrayList<String>();
	private static int SMALL_STRAIGHT_MASK1 = (1 << 0) + (1 << 1) + (1 << 2) + (1 << 3);
	private static int SMALL_STRAIGHT_MASK2 = (1 << 1) + (1 << 2) + (1 << 3) + (1 << 4);
	private static int SMALL_STRAIGHT_MASK3 = (1 << 2) + (1 << 3) + (1 << 4) + (1 << 5);
	private static int LARGE_STRAIGHT_MASK1 = (1 << 0) + (1 << 1) + (1 << 2) + (1 << 3) + (1 << 4);
	private static int LARGE_STRAIGHT_MASK2 = (1 << 1) + (1 << 2) + (1 << 3) + (1 << 4) + (1 << 5);

	static Random rgen = new Random();
	static int countDiceCombinations = 0;

	public static void main(String[] args) {
		System.out.println("Hello World!");
		categories.add("ONE");
		categories.add("TWO");
		categories.add("THREE");
		categories.add("FOUR");
		categories.add("FIVE");
		categories.add("SIX");
		categories.add("UPPERSCORE");
		categories.add("UPPERBONUS");
		categories.add("THREEOFAKIND");
		categories.add("CARRE");
		categories.add("FULLHOUSE");
		categories.add("SMALLSTRAIGHT");
		categories.add("LARGESTRAIGHT");
		categories.add("YAHTZEE");
		categories.add("CHANCE");
		categories.add("LOWERSCORE");
		categories.add("TOTAL");
		playGame();
		System.out.println("Goodbye World!");
	}

	private static void playGame() {
		boolean gameOver = false;
		scorecard = new int[N_CATEGORIES + 1];
		categoryHasBeenChosen = new boolean[N_CATEGORIES + 1];
		int round = 1;
		while (!gameOver) {
			playRound(round);
			if (round == N_SCORING_CATEGORIES)
				gameOver = true;
			round++;
		}
		System.out.println("Congratulations, you are the winner with a total score of " + scorecard[TOTAL] + "!");
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void playRound(int round) {
		playTurn(round);
		evaluateTotalScores(round);
		printScorecard();
	}

	public static void playTurn(int round) {
		System.out.println("Playing round " + round);
		int[] dice = new int[5];
		generateAllDiceSelections();
		boolean[] selectedDice = new boolean[5];
		for (int rolls = 0; rolls < 2; rolls++) {
			System.out.println("Rolling dice...");
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			rollDice(rolls, dice, selectedDice);
			System.out.println("Dice for roll " + rolls + ": " + diceToString(dice));
			DiceSelection bestSelection = null;
			double bestEValue = -1.0;
			for (String name : allSelections.keySet()) {
				DiceSelection selectionCombo = allSelections.get(name);
				selectedDice = selectionCombo.getDiceSelection();
				selectionCombo.resetEValue();
				List<DiceCombination> listDiceCombinations = generateDiceCombinations(selectedDice, dice);		
				Iterator<DiceCombination> it = listDiceCombinations.iterator();
				while (it.hasNext()) {
					DiceCombination diceCombo = it.next();
					int[] comboDice = diceCombo.getCombination();
					int category = chooseBestCategory(comboDice);
					boolean isValid = isDiceValidForCategory(comboDice, category);
					int score = calculateCategoryScore(category, isValid, comboDice);
					diceCombo.updateCombination(dice, category, score, selectedDice);
					double eValue = diceCombo.getEValue();
					selectionCombo.addEValue(eValue);
				}
				if (selectionCombo.getEValue() > bestEValue) {
					bestSelection = selectionCombo;
					bestEValue = selectionCombo.getEValue();
				}
			}
			System.out.println("The best selection to choose is: " + bestSelection.toString());
			selectedDice = bestSelection.getDiceSelection();
		}
		System.out.println("Turn is over.");
		int category = chooseBestCategory(dice);
		categoryHasBeenChosen[category] = true;
		System.out.println("Choosing category " + categories.get(category - 1));
		boolean isValid = isDiceValidForCategory(dice, category);
		System.out.println("Dice are valid for this category: " + isValid);
		int score = calculateCategoryScore(category, isValid, dice);
		System.out.println("Score for this category: " + score);
		updateScore(category, score);
	}

	private static void rollDice(int roll, int[] dice, boolean[] isDieSelected) {
		for (int i = 0; i < 5; i++) {
			if (roll == 0 || isDieSelected[i]) {
				int die = rgen.nextInt(6) + 1;
				dice[i] = die;
			}
		}
		sortdices(dice);
	}

	private static void sortdices(int[] dice) {
		int temp;
		for (int i = dice.length - 1; i >= 1; --i) {
			for (int j = 0; j < i; ++j) {
				if (dice[j] > dice[j + 1]) {
					temp = dice[j];
					dice[j] = dice[j + 1];
					dice[j + 1] = temp;
				}
			}
		}
	}

	private static String diceToString(int[] dice) {
		String result = "[ ";
		for (int i = 0; i < dice.length; i++) {
			result += dice[i] + " ";
		}
		result += "]";
		return result;
	}

	private static void generateAllDiceSelections() {
		for (int d0 = 0; d0 <= 1; d0++) {
			for (int d1 = 0; d1 <= 1; d1++) {
				for (int d2 = 0; d2 <= 1; d2++) {
					for (int d3 = 0; d3 <= 1; d3++) {
						for (int d4 = 0; d4 <= 1; d4++) {
							boolean[] arr = new boolean[5];
							arr[0] = (d0 == 0) ? false : true;
							arr[1] = (d1 == 0) ? false : true;
							arr[2] = (d2 == 0) ? false : true;
							arr[3] = (d3 == 0) ? false : true;
							arr[4] = (d4 == 0) ? false : true;
							DiceSelection combo = new DiceSelection(arr);
							allSelections.put(combo.toString(), combo);
						}
					}
				}
			}
		}
	}

	private static List<DiceCombination> generateDiceCombinations(boolean[] selections, int[] dice) {
		countDiceCombinations = 0;
		List<DiceCombination> result = new ArrayList<DiceCombination>();
		int lb0 = (selections[0] == false ? dice[0] : 1);
		int ub0 = (selections[0] == false ? dice[0] : 6);
		for (int d0 = lb0; d0 <= ub0; d0++) {

			int lb1 = (selections[1] == false ? dice[1] : 1);
			int ub1 = (selections[1] == false ? dice[1] : 6);
			for (int d1 = lb1; d1 <= ub1; d1++) {

				int lb2 = (selections[2] == false ? dice[2] : 1);
				int ub2 = (selections[2] == false ? dice[2] : 6);
				for (int d2 = lb2; d2 <= ub2; d2++) {

					int lb3 = (selections[3] == false ? dice[3] : 1);
					int ub3 = (selections[3] == false ? dice[3] : 6);
					for (int d3 = lb3; d3 <= ub3; d3++) {

						int lb4 = (selections[4] == false ? dice[4] : 1);
						int ub4 = (selections[4] == false ? dice[4] : 6);
						for (int d4 = lb4; d4 <= ub4; d4++) {

							int[] arr = { d0, d1, d2, d3, d4 };
							DiceCombination combo = new DiceCombination(arr);
							result.add(combo);
							countDiceCombinations++;
						}
					}
				}
			}
		}
		System.out.println("CountDiceCombinations: " + countDiceCombinations);
		return result;
	}

	private static int chooseBestCategory(int[] dice) {
		int categoryIndex = 0;
		int highestScore = -1;
		for (int i = 1; i < 16; i++) { // sloppy, fix later.
			if (categoryHasBeenChosen[i] == false) {
				boolean isValid = isDiceValidForCategory(dice, i);
				int score = calculateCategoryScore(i, isValid, dice);
				if (score > highestScore) {
					highestScore = score;
					categoryIndex = i;
				}
				if (i == 6)
					i = 8;
			}
		}

		return categoryIndex;
	}

	private static boolean isDiceValidForCategory(int[] dice, int category) {
		sortdices(dice);
		if (category >= ONES && category <= SIXES) {
			for (int i = 0; i < 5; i++) {
				if (dice[i] == category)
					return true;
			}
		}
		switch (category) {
		case THREE_OF_A_KIND:
			return isNOfAKind(3, dice, false);
		case FOUR_OF_A_KIND:
			return isNOfAKind(4, dice, false);
		case FULL_HOUSE:
			return (isNOfAKind(3, dice, true) && isNOfAKind(2, dice, true));
		case SMALL_STRAIGHT:
			return (isSmallStraight(dice)||isLargeStraight(dice));
		case LARGE_STRAIGHT:
			return isLargeStraight(dice);
		case YAHTZEE:
			return isNOfAKind(5, dice, false);
		case CHANCE:
			return true;
		default:
			return false;
		}
	}

	private static int calculateCategoryScore(int category, boolean isValid, int[] dice) {
		if (isValid) {
			switch (category) {
			case ONES:
			case TWOS:
			case THREES:
			case FOURS:
			case FIVES:
			case SIXES:
				return sumDice(dice, category);
			case THREE_OF_A_KIND:
				return sumDice(dice, 0);
			case FOUR_OF_A_KIND:
				return sumDice(dice, 0);
			case FULL_HOUSE:
				return FULL_HOUSE_SCORE;
			case SMALL_STRAIGHT:
				return SMALL_STRAIGHT_SCORE;
			case LARGE_STRAIGHT:
				return LARGE_STRAIGHT_SCORE;
			case YAHTZEE:
				return YAHTZEE_SCORE;
			case CHANCE:
				return sumDice(dice, 0);
			default:
				return 0;
			}
		} else {
			return 0;
		}
	}

	private static boolean isNOfAKind(int n, int[] dice, boolean exact) {
		boolean result = false;
		int[] equals = new int[6];
		for (int i = 0; i < 5; i++) {
			equals[dice[i] - 1]++;
		}
		for (int i = 0; i < equals.length; i++) {
			if (exact) {
				if (equals[i] == n)
					return true;
			} else {
				if (equals[i] >= n)
					return true;
			}
		}
		return result;
	}

	private static int sumDice(int[] dice, int dieValueRequirement) {
		int result = 0;
		for (int i = 0; i < 5; i++) {
			if (dieValueRequirement == 0) {
				result += dice[i];
			} else {
				if (dice[i] == dieValueRequirement)
					result += dice[i];
			}
		}
		return result;
	}

	private static boolean isSmallStraight(int[] dice) {
		int mask = 0;
		for (int i = 0; i < 5; i++) {
			mask = mask | (1 << (dice[i] - 1));
		}
		if ((mask & SMALL_STRAIGHT_MASK1) == SMALL_STRAIGHT_MASK1) {
			return true;
		} else if ((mask & SMALL_STRAIGHT_MASK2) == SMALL_STRAIGHT_MASK2) {
			return true;
		} else if ((mask & SMALL_STRAIGHT_MASK3) == SMALL_STRAIGHT_MASK3) {
			return true;
		}
		return false;
	}

	private static boolean isLargeStraight(int[] dice) {
		int mask = 0;
		for (int i = 0; i < 5; i++) {
			mask = mask | (1 << (dice[i] - 1));
		}
		if ((mask & LARGE_STRAIGHT_MASK1) == LARGE_STRAIGHT_MASK1) {
			return true;
		} else if ((mask & LARGE_STRAIGHT_MASK2) == LARGE_STRAIGHT_MASK2) {
			return true;
		}
		return false;
	}

	private static void updateScore(int category, int score) {
		scorecard[category] = score;
	}

	private static void evaluateTotalScores(int round) {
		updateScore(UPPER_SCORE, sumScores(ONES, SIXES));
		updateScore(LOWER_SCORE, sumScores(THREE_OF_A_KIND, CHANCE));
		updateScore(TOTAL, (scorecard[UPPER_SCORE] + scorecard[UPPER_BONUS] + scorecard[LOWER_SCORE]));
		if (isUpperScoreComplete()) {
			if (scorecard[UPPER_SCORE] >= 63) {
				updateScore(UPPER_BONUS, UPPER_BONUS_SCORE);
			} else {
				updateScore(UPPER_BONUS, 0);
			}
		}
	}

	private static int sumScores(int startCategory, int endCategory) {
		int result = 0;
		for (int i = startCategory; i <= endCategory; i++) {
			result += scorecard[i];

		}
		return result;
	}

	private static boolean isUpperScoreComplete() {
		for (int i = ONES; i <= SIXES; i++) {
			if (scorecard[i] == 0)
				return false;
		}
		return true;
	}

	private static void printScorecard() {
		System.out.println("Printing scorecard...");
		for (int i = 1; i <= N_CATEGORIES; i++) {
			if (categoryHasBeenChosen[i] == true) {
				System.out.println("[" + categories.get(i - 1) + "]: " + scorecard[i]);
			} else {
				System.out.println("[" + categories.get(i - 1) + "]: ");
			}
		}
	}
}
