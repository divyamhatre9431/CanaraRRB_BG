package com.idbi.intech.aml.XMLParser;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "billOfEntrys")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillOfEntrys {
	
	@XmlElement(name="billOfEntry")
	private List<BillOfEntry> billOfEntry;

	public List<BillOfEntry> getBillOfEntry() {
		return billOfEntry;
	}

	public void setBillOfEntry(List<BillOfEntry> billOfEntry) {
		this.billOfEntry = billOfEntry;
	}
	

	
}
