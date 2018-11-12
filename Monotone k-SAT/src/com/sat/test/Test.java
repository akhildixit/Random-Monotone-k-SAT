package com.sat.test;

import java.util.List;

import com.sat.beans.CNFFormula;
import com.sat.core.Generator;

public class Test {

	public static void main(String[] args) {
		Generator gen = new Generator();
		List<CNFFormula> list = gen.generate(10, 3, 50, 250, false, 0.5);
		
	}
}
