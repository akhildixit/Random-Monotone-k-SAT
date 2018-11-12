package com.sat.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.sat.beans.CNFFormula;
import com.sat.beans.Clause;

public class Solver {
	private static final String FILEPATH = "C:\\Users\\Akhil\\OneDrive - ucsc.edu\\Abhyas\\CQA\\lingeling-master\\";
	private static final String FORMULA_FILENAME = "formula.txt";
	private static final String OUTPUT_FILENAME = "output.txt";

	public boolean solve(CNFFormula formula) {
		createDimacsFile(formula);
		executeCommand(new String[] { "./lingeling", FILEPATH + FORMULA_FILENAME });
		return isSAT();
	}

	public void createDimacsFile(CNFFormula formula) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(FILEPATH + FORMULA_FILENAME));
			writer.append("p cnf " + formula.getNoOfVariables() + " " + formula.getClauses().size() + "\n");
			for (Clause c : formula.getClauses()) {
				writer.append(c.getDimacsLine());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int executeCommand(String[] command) {
		ProcessBuilder pb = new ProcessBuilder(command);
		if (null != FILEPATH + OUTPUT_FILENAME) {
			pb.redirectOutput(new File(FILEPATH + OUTPUT_FILENAME));
			pb.redirectErrorStream(true);
		}
		try {
			Process p = pb.start();
			int exitVal = p.waitFor();
			return exitVal;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public boolean isSAT() {
		try (BufferedReader br = new BufferedReader(new FileReader(FILEPATH + OUTPUT_FILENAME))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.startsWith("s SATISFIABLE")) {
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
