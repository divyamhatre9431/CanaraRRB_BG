package com.idbi.intech.iaml.screening;

public class LnCustomerScanning {
	public static void main(String args[]) {

		Thread t1 = new Thread(new IndividualCustomerScanning());
		t1.start();
		
		Thread t2 = new Thread(new NonIndividualCustomerScanning());
		t2.start();
	}
}
