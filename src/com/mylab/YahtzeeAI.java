package com.mylab;

import java.util.*;

public class YahtzeeAI implements YahtzeeConstants {

	private static int scorecard[][];
	private final static int nPlayers = 1;
	private static boolean[][] categoryHasBeenChosen;
	private static int delay = 500;
	private final static Map<String, DiceSelection> allSelections = new HashMap<String, DiceSelection>();

	static Random rgen = new Random();

	public static void main(String[] args) {
		System.out.println("Hello World!");
		playGame();
		System.out.println("Goodbye World!");
	}

	private static void playGame() {
		boolean gameOver = false;
		scorecard = new int[N_CATEGORIES + 1][nPlayers + 1];
		categoryHasBeenChosen = new boolean[N_CATEGORIES + 1][nPlayers + 1];
		int round = 1;
		while (!gameOver) {
			playRound(round);
			if (round == N_SCORING_CATEGORIES)
				gameOver = true;
			round++;
		}
		System.out.println("Congratulations, you are the winner with a total score of " + scorecard[TOTAL][1] + "!");
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void playRound(int round) {
		for (int i = 1; i <= nPlayers; i++) {
			playTurn(i, round);
			evaluateTotalScores(i, round);
			printScorecard(i);
		}
	}

	public static void playTurn(int player, int round) {
		System.out.println("Playing round " + round);
		int[] dice = new int[N_DICE];
		generateAllDiceSelections();
		boolean[] selectedDice = new boolean[N_DICE];
		for (int rolls = 0; rolls < MAX_ROLLS; rolls++) {
			System.out.println("Rolling dice...");
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			rollDice(rolls, dice, selectedDice);
			System.out.println("Dice for roll " + rolls + ": " + diceToString(dice));
			if (rolls == MAX_ROLLS - 1)
				break;
			DiceSelection bestSelection = null;
			double bestEValue = -1.0;
			for (String name : allSelections.keySet()) {
				DiceSelection selectionCombo = allSelections.get(name);
				selectedDice = selectionCombo.getDiceSelection();
				selectionCombo.resetEValue();
				selectionCombo.setDiceCombinations(generateDiceCombinations(selectedDice, dice));
				Iterator<DiceCombination> it = selectionCombo.getDiceCombinationsIterator();
				while (it.hasNext()) {
					DiceCombination diceCombo = it.next();
					int[] comboDice = diceCombo.getCombination();
					int category = chooseBestCategory(player, comboDice);
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
			System.out.println("The best selection to choose is: " + bestSelection.getName());
			selectedDice = bestSelection.getDiceSelection();
		}
		System.out.println("Turn is over.");
		int category = chooseBestCategory(player, dice);
		categoryHasBeenChosen[category][player] = true;
		System.out.println("Choosing category " + category);
		boolean isValid = isDiceValidForCategory(dice, category);
		System.out.println("Dice are valid for this category: " + isValid);
		int score = calculateCategoryScore(category, isValid, dice);
		System.out.println("Score for this category: " + score);
		updateScore(player, category, score);
	}

	private static void rollDice(int roll, int[] dice, boolean[] isDieSelected) {
		for (int i = 0; i < N_DICE; i++) {
			if (roll == 0 || isDieSelected[i]) {
				int die = rgen.nextInt(6) + 1;
				dice[i] = die;
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
							allSelections.put(combo.getName(), combo);
						}
					}
				}
			}
		}
	}

	private static List<DiceCombination> generateDiceCombinations(boolean[] selections, int[] dice) {
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
						}
					}
				}
			}
		}
		return result;
	}

	private static int chooseBestCategory(int player, int[] dice) {
		int categoryIndex = 0;
		int highestScore = -1;
		for (int i = 1; i < 16; i++) { // sloppy, fix later.
			if (categoryHasBeenChosen[i][player] == false) {
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
		if (category >= ONES && category <= SIXES) {
			for (int i = 0; i < N_DICE; i++) {
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
			return isStraight(4, dice);
		case LARGE_STRAIGHT:
			return isStraight(5, dice);
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
		int[] frequency = diceValueFrequency(dice);
		for (int i = 0; i < frequency.length; i++) {
			if (exact) {
				if (frequency[i] == n)
					return true;
			} else {
				if (frequency[i] >= n)
					return true;
			}
		}
		return result;
	}

	private static int[] diceValueFrequency(int[] dice) {
		int[] result = new int[6];
		for (int i = 0; i < N_DICE; i++) {
			result[dice[i] - 1]++;
		}
		return result;
	}

	private static int sumDice(int[] dice, int dieValueRequirement) {
		int result = 0;
		for (int i = 0; i < N_DICE; i++) {
			if (dieValueRequirement == 0) {
				result += dice[i];
			} else {
				if (dice[i] == dieValueRequirement)
					result += dice[i];
			}
		}
		return result;
	}

	private static boolean isStraight(int n, int[] dice) {
		int[] frequency = diceValueFrequency(dice);
		for (int i = 0; i < (frequency.length - n + 1); i++) {
			int nInARow = 0;
			for (int j = 0; j < n; j++) {
				if (frequency[i + j] > 0)
					nInARow++;
			}
			if (nInARow == n)
				return true;
		}
		return false;
	}

	private static void updateScore(int player, int category, int score) {
		scorecard[category][player] = score;
	}

	private static void evaluateTotalScores(int player, int round) {
		updateScore(player, UPPER_SCORE, sumScores(player, ONES, SIXES));
		updateScore(player, LOWER_SCORE, sumScores(player, THREE_OF_A_KIND, CHANCE));
		updateScore(player, TOTAL,
				(scorecard[UPPER_SCORE][player] + scorecard[UPPER_BONUS][player] + scorecard[LOWER_SCORE][player]));
		if (isUpperScoreComplete(player)) {
			if (scorecard[UPPER_SCORE][player] >= 63) {
				updateScore(player, UPPER_BONUS, UPPER_BONUS_SCORE);
			} else {
				updateScore(player, UPPER_BONUS, 0);
			}
		}
	}

	private static int sumScores(int player, int startCategory, int endCategory) {
		int result = 0;
		for (int i = startCategory; i <= endCategory; i++) {
			result += scorecard[i][player];

		}
		return result;
	}

	private static boolean isUpperScoreComplete(int player) {
		for (int i = ONES; i <= SIXES; i++) {
			if (scorecard[i][player] == 0)
				return false;
		}
		return true;
	}

	private static void printScorecard(int player) {
		System.out.println("Printing scorecard...");
		for (int i = 1; i <= N_CATEGORIES; i++) {
			if (categoryHasBeenChosen[i][player] == true) {
				System.out.println("[" + i + "]: " + scorecard[i][player]);
			} else {
				System.out.println("[" + i + "]: ");
			}
		}
	}

}
