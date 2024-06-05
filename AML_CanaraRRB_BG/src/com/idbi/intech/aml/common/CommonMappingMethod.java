package com.idbi.intech.aml.common;

public class CommonMappingMethod {

	static public String accountTypeMapping(String val) {

		if (val.equalsIgnoreCase("A")) {
			return "BS";
		} else if (val.equalsIgnoreCase("B")) {
			return "BC";
		} else if (val.equalsIgnoreCase("C")) {
			return "BR";
		} else if (val.equalsIgnoreCase("L")) {
			return "BL";
		} else if (val.equalsIgnoreCase("T")) {
			return "BT";
		}else if (val.equalsIgnoreCase("R")) {
			return "BR";
		} else {
			return "ZZ";
		}

	}

	static public String stateMapping(String val) {

		if (val == null) {
			return "XX";
		} else if (val.trim().equalsIgnoreCase("")) {
			return "ZZ";
		} else if (val.equalsIgnoreCase("ANDHRAPRADESH")) {
			return "AP";
		} else if (val.equalsIgnoreCase("ARUNACHAL PRADESH")) {
			return "AR";
		} else if (val.equalsIgnoreCase("ASSAM")) {
			return "AS";
		} else if (val.equalsIgnoreCase("BIHAR")) {
			return "BR";
		} else if (val.equalsIgnoreCase("CHANDIGARH")) {
			return "CH";
		} else if (val.equalsIgnoreCase("CHHATISGARH")) {
			return "CG";
		} else if (val.equalsIgnoreCase("DADRA NAGAR HAVELI")) {
			return "DN";
		} else if (val.equalsIgnoreCase("DAMAN & DUI")) {
			return "DD";
		} else if (val.equalsIgnoreCase("DELHI")) {
			return "DL";
		} else if (val.equalsIgnoreCase("GOA")) {
			return "GA";
		} else if (val.equalsIgnoreCase("GUJARAT")) {
			return "GJ";
		} else if (val.equalsIgnoreCase("HARYANA")) {
			return "HR";
		} else if (val.equalsIgnoreCase("HIMACHAL PRADESH")) {
			return "HP";
		} else if (val.equalsIgnoreCase("JAMMU & KASHMIR")) {
			return "JK";
		} else if (val.equalsIgnoreCase("JHARKHAND")) {
			return "JH";
		} else if (val.equalsIgnoreCase("KARNATAKA")) {
			return "KA";
		} else if (val.equalsIgnoreCase("KERALA")) {
			return "KL";
		} else if (val.equalsIgnoreCase("LAKSHADWEEP")) {
			return "LK";
		} else if (val.equalsIgnoreCase("MADHYAPRADESH")) {
			return "MP";
		} else if (val.equalsIgnoreCase("MAHARASHTRA")) {
			return "MH";
		} else if (val.equalsIgnoreCase("MANIPUR")) {
			return "MN";
		} else if (val.equalsIgnoreCase("MEGHALAYA")) {
			return "ML";
		} else if (val.equalsIgnoreCase("MIZORAM")) {
			return "MZ";
		} else if (val.equalsIgnoreCase("NAGALAND")) {
			return "NL";
		} else if (val.equalsIgnoreCase("ODISHA")) {
			return "OR";
		} else if (val.equalsIgnoreCase("ORISSA")) {
			return "OR";
		} else if (val.equalsIgnoreCase("PONDICHERRY")) {
			return "PY";
		} else if (val.equalsIgnoreCase("PUNJAB")) {
			return "PB";
		} else if (val.equalsIgnoreCase("RAJASTHAN")) {
			return "RJ";
		} else if (val.equalsIgnoreCase("SIKKIM")) {
			return "SK";
		} else if (val.equalsIgnoreCase("TAMIL NADU")) {
			return "TN";
		} else if (val.equalsIgnoreCase("TRIPURA")) {
			return "TR";
		} else if (val.equalsIgnoreCase("UTTAR PRADESH")) {
			return "UP";
		} else if (val.equalsIgnoreCase("UTTARAKHAND")) {
			return "UA";
		} else if (val.equalsIgnoreCase("WEST BENGAL")) {
			return "WB";
		} else {
			return "ZZ";
		}
	}

	static public String genderMapping(String val) {

		if (val.equalsIgnoreCase("M")) {
			return "M";
		} else if (val.equalsIgnoreCase("F")) {
			return "F";
		} else {
			return "X";
		}

	}

	static public String countryMapping(String val) {

		if (val == null) {
			return "XX";
		} else {

			if (val.equalsIgnoreCase("")) {
				return "ZZ";
			} else if (val.equalsIgnoreCase(" ")) {
				return "ZZ";
			} else if (val.equalsIgnoreCase("")) {
				return "ZZ";
			} else if (val.equalsIgnoreCase("N.A.")) {
				return "ZZ";
			} else if (val.equalsIgnoreCase("XXX")) {
				return "ZZ";
			} else if (val.equalsIgnoreCase("XX")) {
				return "ZZ";
			} else if (val.equalsIgnoreCase("DFL")) {
				return "ZZ";
			} else {
				return val;
			}
		}

	}

}
