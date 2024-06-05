package com.idbi.intech.aml.XMLParser;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "invoice")
@XmlAccessorType(XmlAccessType.FIELD) 
public class Invoices {
	@XmlElement(name="invoice")
	private List<Invoice> invoice;

	public List<Invoice> getInvoice() {
		return invoice;
	}

	public void setInvoice(List<Invoice> invoice) {
		this.invoice = invoice;
	}

}
