package com.idbi.intech.aml.XMLParser;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "shippingBills")
@XmlAccessorType(XmlAccessType.FIELD) 
public class ShippingBills {
	
	@XmlElement(name="shippingBill")
	private List<ShippingBill> shippingBill;

	public List<ShippingBill> getShippingBill() {
		return shippingBill;
	}

	public void setShippingBill(List<ShippingBill> shippingBill) {
		this.shippingBill = shippingBill;
	}

}
