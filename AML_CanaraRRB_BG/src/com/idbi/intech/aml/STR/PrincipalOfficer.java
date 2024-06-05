//
// This file was com.idbiintech.str by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.idbiintech.str on: 2011.07.28 at 10:17:54 AM IST 
//

package com.idbi.intech.aml.STR;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Information of Principal Officer submitting the batch report
 * 
 * <p>
 * Java class for PrincipalOfficer complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="PrincipalOfficer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="POName">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="80"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PODesignation">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="80"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="POAddress" type="{}Address"/>
 *         &lt;element name="POPhone" type="{}Phone"/>
 *         &lt;element name="POEmail" type="{}EmailAddress"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "PrincipalOfficer")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrincipalOfficer", propOrder = { "poName", "poDesignation", "poAddress", "poPhone", "poEmail" })
public class PrincipalOfficer {

	@XmlElement(name = "POName", required = true)
	protected String poName;
	@XmlElement(name = "PODesignation", required = true)
	protected String poDesignation;
	@XmlElement(name = "POAddress", required = true)
	protected Address poAddress;
	@XmlElement(name = "POPhone", required = true)
	protected Phone poPhone;
	@XmlElement(name = "POEmail", required = true, nillable = true)
	protected String poEmail;

	/**
	 * Gets the value of the poName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPOName() {
		return poName;
	}

	/**
	 * Sets the value of the poName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPOName(String value) {
		this.poName = value;
	}

	/**
	 * Gets the value of the poDesignation property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPODesignation() {
		return poDesignation;
	}

	/**
	 * Sets the value of the poDesignation property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPODesignation(String value) {
		this.poDesignation = value;
	}

	/**
	 * Gets the value of the poAddress property.
	 * 
	 * @return possible object is {@link Address }
	 * 
	 */
	public Address getPOAddress() {
		return poAddress;
	}

	/**
	 * Sets the value of the poAddress property.
	 * 
	 * @param value
	 *            allowed object is {@link Address }
	 * 
	 */
	public void setPOAddress(Address value) {
		this.poAddress = value;
	}

	/**
	 * Gets the value of the poPhone property.
	 * 
	 * @return possible object is {@link Phone }
	 * 
	 */
	public Phone getPOPhone() {
		return poPhone;
	}

	/**
	 * Sets the value of the poPhone property.
	 * 
	 * @param phPo
	 *            allowed object is {@link Phone }
	 * 
	 */
	public void setPOPhone(com.idbi.intech.aml.STR.Phone phPo) {
		this.poPhone = phPo;
	}

	/**
	 * Gets the value of the poEmail property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPOEmail() {
		return poEmail;
	}

	/**
	 * Sets the value of the poEmail property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPOEmail(String value) {
		this.poEmail = value;
	}

}
