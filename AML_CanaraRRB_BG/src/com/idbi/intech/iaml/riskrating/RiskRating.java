package com.idbi.intech.iaml.riskrating;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class RiskRating {
	private static Connection conn;

	static {
		try {
			conn = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		RiskRatingProcess riskObj = new RiskRatingProcess();
		String option = "";
		String fromdate = "";
		String todate = "";
		String report = "";
		String gendata = "";
		System.out
				.print("**********Risk Rating Process Tool Powered by i-AML**********\n");
		System.out
				.print("-------------------------------------------------------------\n");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.print("Select any one choice from below option...\n");
			System.out.print("1.Risk Rating Categorywise Count\n");
			System.out.print("2.Risk Rating Rule Process\n");
			System.out.print("3.Exit\n");
			System.out.print("Enter the option: ");
			option = br.readLine();
			if (option.equalsIgnoreCase("1")) {
				report = "Risk Rating Categorywise Count";
				System.out.print("Enter the Start Date(DD-MON-YY): ");
				fromdate = br.readLine();
				System.out.print("Enter the End Date(DD-MON-YY): ");
				todate = br.readLine();
				System.out.print("The " + report
						+ " will be generated for the date range " + fromdate
						+ " to " + todate + "\n");
				System.out.print("To generate the report enter(Y/N): ");
				gendata = br.readLine();
				if (gendata.equalsIgnoreCase("Y")) {

				}
			} else if (option.equalsIgnoreCase("2")) {

			} else {
				System.out.println("Please enter the correct option...\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
