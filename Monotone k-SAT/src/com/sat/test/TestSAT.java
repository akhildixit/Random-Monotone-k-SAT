package com.sat.test;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import com.beans.Atom;
import com.beans.CNFFormula;
import com.beans.Query;
import com.beans.Relation;
import com.beans.Schema;
import com.core.AnswersComputer;
import com.core.Encoder;
import com.core.Preprocessor;
import com.sat.beans.Stats;
import com.sat.core.Generator;
import com.sat.core.SATtoSinkQueryEncoder;
import com.sat.core.Solver;
import com.util.DBEnvironment;

public class TestSAT {

	public static void main(String[] args) {
		TestSAT test = new TestSAT();
		test.runSATtoCQAtoSATExperiment();
	}

	public void runSATtoCQAtoSATExperiment() {
		int samples = 10;
		int n = 150;
		System.out.println("S:Satisfiable, C:Conflicts, D:Decisions, P:Propagations, T:Time\n");
		Connection con = new DBEnvironment().getConnection();
		for (int i = 37; i < 50; i++) {
			int l = (int) (n * ((double) i / 10));
			System.out.println("---------------------- Ratio = " + (double) i / 10 + " ---------------------");
			for (int j = 0; j < samples; j++) {
				Generator gen = new Generator();
				Solver sol = new Solver();
				CNFFormula formula = gen.generate(3, n, l, true, 0.5);
				Stats stats = sol.solve(formula);
				System.out
						.print("S:" + stats.isSolved() + " C:" + stats.getConflicts() + " P:" + stats.getPropagations()
								+ " D:" + stats.getDecisions() + " T:" + stats.getTime() * 1000 + " ");

				SATtoSinkQueryEncoder satToSinkQueryEncoder = new SATtoSinkQueryEncoder();
				satToSinkQueryEncoder.encode(formula);
				Query query = getSinkQueryBean();
				Schema schema = getSinkSchema();

				Preprocessor preprocessor = new Preprocessor(schema, query, con);
				preprocessor.dropAllTables();
				preprocessor.createIndexesOnKeys();
				preprocessor.createKeysViews();
				long start = System.currentTimeMillis();
				preprocessor.createAnsFromCons();
				if (preprocessor.checkBooleanConsAnswer()) {
					System.out.println("Cons: true, T: " + (System.currentTimeMillis() - start) + "ms (immediate)");
					return;
				}
				preprocessor.createWitnesses(false);
				int totalRelevantFacts = preprocessor.createRelevantViews();
				preprocessor.createWitnesses(true);

				Encoder encoder = new Encoder(schema, query, con);
				com.beans.CNFFormula f1 = encoder.createPositiveClauses(totalRelevantFacts);
				com.beans.CNFFormula f2 = encoder.createNegativeClauses(totalRelevantFacts, 1);
				com.beans.CNFFormula f = f1.combine(f2);
				Encoder.createDimacsFile("formula1.txt", f, 1);
				AnswersComputer computer = new AnswersComputer(con);
				start = System.currentTimeMillis();
				boolean answer = computer.computeBooleanAnswer("formula1.txt", "glucose");
				System.out.println("Cons: " + answer + ", T: " + (System.currentTimeMillis() - start) + "ms");
			}
		}
	}

	private Query getSinkQueryBean() {
		Query query = new Query();
		Atom atom1 = new Atom("posClauses");
		atom1.addAttribute("x");
		atom1.addAttribute("y");
		atom1.setAtomIndex(1);
		query.addAtom(atom1);

		Atom atom2 = new Atom("negClauses");
		atom2.addAttribute("x'");
		atom2.addAttribute("y");
		atom2.setAtomIndex(1);
		query.addAtom(atom2);

		return query;
	}

	private Schema getSinkSchema() {
		Schema schema = new Schema();
		Set<Relation> relations = new HashSet<Relation>();

		Relation posClauses = new Relation("posClauses");
		posClauses.addAttribute("id");
		posClauses.addAttribute("value");
		posClauses.addKeyAttribute(1);
		relations.add(posClauses);

		Relation negClauses = new Relation("negClauses");
		negClauses.addAttribute("id");
		negClauses.addAttribute("value");
		negClauses.addKeyAttribute(1);
		relations.add(negClauses);

		schema.setRelations(relations);
		return schema;
	}

	public void runTransitionPhaseExperiment() {
		Generator gen = new Generator();
		Solver sol = new Solver();
		int samples = 250;
		int n = 250;
		for (int i = 37; i <= 55; i++) {
			int sat = 0, conflicts = 0, propagations = 0, decisions = 0;
			double time = 0;
			int l = (int) (n * ((double) i / 10));
			for (int j = 0; j < samples; j++) {
				Stats stats = sol.solve(gen.generate(3, n, l, true, 0.5));
				if (stats.isSolved()) {
					sat++;
				}
				conflicts += stats.getConflicts();
				propagations += stats.getPropagations();
				decisions += stats.getDecisions();
				time += stats.getTime();
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