package com.idbi.intech.iaml.tool;

public class SplitCheck {

	public static void main(String[] args) {
		String email="sharath.nair,idbiintech.com,rohit";
		String user[] = email.split(",");
		System.out.println(user[0]+" "+user[2]);
	}

}
