package com.mylab;

public class CategoryResult {

	public CategoryResult() {

	}

	public void setCategory(int n) {
		category = n;
	}

	public void setValid(boolean b) {
		validity = b;
	}

	public int getCategory() {
		return category;
	}

	public boolean isValid() {
		return validity;
	}

	private int category;
	private boolean validity;

}
