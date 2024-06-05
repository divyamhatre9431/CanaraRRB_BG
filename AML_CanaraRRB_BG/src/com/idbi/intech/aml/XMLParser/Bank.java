package com.idbi.intech.aml.XMLParser;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bank")
@XmlAccessorType(XmlAccessType.FIELD) 
public class Bank {
	
	@XmlElement(name="checkSum")
	private CheckSum checkSum;
	
	@XmlElement(name="billOfEntrys")
	private List<BillOfEntrys> billOfEntrys;
	
	@XmlElement(name="shippingBills")
	private List<ShippingBills> shippingBills;
	
	public List<ShippingBills> getShippingBills() {
		return shippingBills;
	}

	public void setShippingBills(List<ShippingBills> shippingBills) {
		this.shippingBills = shippingBills;
	}

	private String fileName;


	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<BillOfEntrys> getBillOfEntrys() {
		return billOfEntrys;
	}

	public void setBillOfEntrys(List<BillOfEntrys> billOfEntrys) {
		this.billOfEntrys = billOfEntrys;
	}

	public CheckSum getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(CheckSum checkSum) {
		this.checkSum = checkSum;
	}

}
