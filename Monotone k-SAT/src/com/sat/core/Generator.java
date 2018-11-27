package com.sat.core;

import java.util.Random;

import com.sat.beans.CNFFormula;
import com.sat.beans.Clause;

public class Generator {

	public CNFFormula generate(int k, int n, int l, boolean isMonotone, double p) {
		CNFFormula formula = new CNFFormula();
		Random rand = new Random();
		if (isMonotone)
			for (int i = 0; i < l; i++) { // l is the number of clauses
				formula.addClause(generateMonotoneClause(n, k, p, rand));
			}
		else
			for (int i = 0; i < l; i++) { // l is the number of clauses
				formula.addClause(generateNonMonotoneClause(n, k, p, rand));
			}
		formula.setNoOfVariables(n);
		return formula;
	}

	private Clause generateMonotoneClause(int n, int k, double p, Random rand) {
		Clause clause = new Clause();
		int multiplier = 1;
		if (Math.random() <= p) {
			multiplier = -1;
		}
		for (int j = 0; j < k; j++) {
			clause.addVar(multiplier * (rand.nextInt(n) + 1));
		}
		return clause;
	}

	private Clause generateNonMonotoneClause(int n, int k, double p, Random rand) {
		Clause clause = new Clause();
		for (int j = 0; j < k; j++) {
			if (Math.random() <= p) {
				clause.addVar(-1 * (rand.nextInt(n) + 1));
			} else {
				clause.addVar(rand.nextInt(n) + 1);
			}
		}
		return clause;
	}
}
