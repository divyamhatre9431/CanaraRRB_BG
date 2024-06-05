package com.idbi.intech.iaml.tool;

import java.util.ArrayList;
import java.util.Set;


public class AML_WLScan_Logic {
	ArrayList<Double> arrRes = null;

	public double scanName(Set<String> NList, Set<String> cust, double threshold) {
		arrRes = new ArrayList<Double>();
		double main_result = 0.0;

		for (String cName : cust) {

			double result = 0.0;
			double temp_result = 0.0;

			int k = 0;

			for (String neg : NList) {

				temp_result = FuzzyMatch.compare(neg.toUpperCase(),
						cName.toUpperCase()) * 100;

				// System.out.println(temp_result);

				if (temp_result >= threshold) {
					if (temp_result > result)
						result = temp_result;
				}
			}

			if (result > 0) {
				arrRes.add(result);
			}
		}

		for (double main_res : arrRes) {
			main_result = main_res + main_result;
		}

		main_result = main_result / cust.size();
		return main_result;
	}

	// public static void main(String args[]) {
	// SortedSet<String> hsNeg = new TreeSet<String>();
	// SortedSet<String> hsCust = new TreeSet<String>();
	//
	// // hsCust.add("sharath");
	// // hsCust.add("nair");
	// // hsCust.add("sasi");
	// //
	// // hsNeg.add("sharath");
	// // hsNeg.add("nair");
	//
	// hsCust.add("ena");
	// hsCust.add("publication");
	//
	// hsNeg.add("ena");
	// hsNeg.add("ursula");
	// hsNeg.add("pena");
	// hsNeg.add("pena");
	//
	// System.out.println(new AML_WLScan_Logic().scanName(hsNeg, hsCust, 70));
	//
	// }

}
