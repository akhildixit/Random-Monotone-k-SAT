package com.sat.test;

import com.sat.beans.CNFFormula;
import com.sat.beans.Stats;
import com.sat.core.Generator;
import com.sat.core.SATtoSinkQueryEncoder;
import com.sat.core.Solver;

public class TestSAT {

	public static void main(String[] args) {
		TestSAT test = new TestSAT();
		test.runSATtoCQAtoSATExperiment();
	}

	public void runSATtoCQAtoSATExperiment() {
		Generator gen = new Generator();
		Solver sol = new Solver();
		CNFFormula formula = gen.generate(3, 300, 1290, true, 0.5);
		//formula.prettyPrint();
		Stats stats = sol.solve(formula);
		System.out.println(stats.isSolved());
		SATtoSinkQueryEncoder encoder = new SATtoSinkQueryEncoder();
		encoder.encode(formula);
	}

	public void runTransitionPhaseExperiment() {
		Generator gen = new Generator();
		Solver sol = new Solver();
		int samples = 200;
		int tenPercent = samples / 5;
		int n = 300;
		for (int i = 37; i <= 55; i++) {
			int sat = 0, conflicts = 0, propagations = 0, decisions = 0;
			double time = 0;
			int l = (int) (n * ((double) i / 10));
			for (int j = 0; j < samples; j++) {
				// if (j % tenPercent == 0)
				// System.out.print(". ");
				// System.out.print(j);
				Stats stats = sol.solve(gen.generate(3, n, l, true, 0.5));
				if (stats.isSolved()) {
					sat++;
					System.out.print("S");
				} else {
					System.out.print("U");
				}
				conflicts += stats.getConflicts();
				propagations += stats.getPropagations();
				decisions += stats.getDecisions();
				time += stats.getTime();
				// System.out.println(conflicts + " " + propagations + " " + decisions + " " +
				// time);
			}
			System.out.println();
			System.out.print((double) i / 10 + " " + ((double) sat * 100) / samples);
			System.out.print("Conflicts: " + ((double) conflicts / samples) + " ");
			System.out.print("Decisions: " + ((double) decisions / samples) + " ");
			System.out.print("Propagations: " + ((double) propagations / samples) + " ");
			System.out.print("Time: " + (time / samples) + " ");
		}
	}
}