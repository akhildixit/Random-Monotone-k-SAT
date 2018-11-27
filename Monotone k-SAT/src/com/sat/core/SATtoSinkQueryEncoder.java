package com.sat.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.sat.beans.CNFFormula;
import com.sat.beans.Clause;
import com.sat.util.DBEnvironment;

public class SATtoSinkQueryEncoder {
	// This encoder assumes that the database has two relations R and S.
	public void encode(CNFFormula monotoneFormula) {
		Connection con = new DBEnvironment().getConnection();
		PreparedStatement psPos, psNeg = null;
		int i = 0;
		try {
			con.prepareStatement("DELETE FROM posClauses").executeUpdate();
			con.prepareStatement("DELETE FROM negClauses").executeUpdate();
			psPos = con.prepareStatement("INSERT INTO posClauses VALUES (?, ?)");
			psNeg = con.prepareStatement("INSERT INTO negClauses VALUES (?, ?)");

			for (Clause clause : monotoneFormula.getClauses()) {
				i++;
				if (clause.getVars().iterator().next() > 0) {
					for (int j : clause.getVars()) {
						psPos.setInt(1, i);
						psPos.setInt(2, j);
						psPos.addBatch();
						psPos.clearParameters();
					}
				} else {
					for (int j : clause.getVars()) {
						psNeg.setInt(1, i);
						psNeg.setInt(2, Math.abs(j));
						psNeg.addBatch();
						psNeg.clearParameters();
					}
				}
			}
			System.out.println("Starting execution..");
			psPos.executeBatch();
			psNeg.executeBatch();
			System.out.println("Done.");
			psPos.close();
			psNeg.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}