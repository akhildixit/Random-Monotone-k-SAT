package com.sat.test;

import java.util.List;

import com.sat.beans.CNFFormula;
import com.sat.core.Generator;
import com.sat.core.Solver;

public class TestSAT {

	public static void main(String[] args) {
		Generator gen = new Generator();
		List<CNFFormula> list = gen.generate(1, 3, 50, 250, false, 0.5);
		Solver sol = new Solver();
		for (CNFFormula formula : list) {
			System.out.println(sol.solve(formula));
		}
	}
}