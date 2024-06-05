package com.idbi.intech.aml.XMLParser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

	@XmlRootElement(name = "checkSum")
	@XmlAccessorType(XmlAccessType.FIELD) 
	class CheckSum {
		@XmlElement(name="noOfbillOfEntry")
		private int noOfbillOfEntry;
		
		@XmlElement(name="noOfInvoices")
		private int noOfInvoices;
		
		@XmlElement(name="noOfShippingBills")
		private int noOfShippingBills;

		public int getNoOfbillOfEntry() {
			return noOfbillOfEntry;
		}

		public void setNoOfbillOfEntry(int noOfbillOfEntry) {
			this.noOfbillOfEntry = noOfbillOfEntry;
		}

		public int getNoOfInvoices() {
			return noOfInvoices;
		}

		public void setNoOfInvoices(int noOfInvoices) {
			this.noOfInvoices = noOfInvoices;
		}

		public int getNoOfShippingBills() {
			return noOfShippingBills;
		}

		public void setNoOfShippingBills(int noOfShippingBills) {
			this.noOfShippingBills = noOfShippingBills;
		}
		
		
		

	}

