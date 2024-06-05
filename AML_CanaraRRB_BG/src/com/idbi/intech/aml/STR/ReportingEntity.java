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
 * Reporting Entity global complexType used to provide Reporting Entity details
 * 
 * <p>
 * Java class for ReportingEntity complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ReportingEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReportingEntityName">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="80"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ReportingEntityCategory" type="{}ReportingEntityCategory"/>
 *         &lt;element name="RERegistrationNumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="0"/>
 *               &lt;maxLength value="12"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FIUREID">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "ReportingEntity")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportingEntity", propOrder = { "reportingEntityName", "reportingEntityCategory", "reRegistrationNumber", "fiureid" })
public class ReportingEntity {

	@XmlElement(name = "ReportingEntityName", required = true)
	protected String reportingEntityName;
	@XmlElement(name = "ReportingEntityCategory", required = true)
	protected ReportingEntityCategory reportingEntityCategory;
	@XmlElement(name = "RERegistrationNumber")
	protected String reRegistrationNumber;
	@XmlElement(name = "FIUREID", required = true)
	protected String fiureid;

	/**
	 * Gets the value of the reportingEntityName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getReportingEntityName() {
		return reportingEntityName;
	}

	/**
	 * Sets the value of the reportingEntityName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setReportingEntityName(String value) {
		this.reportingEntityName = value;
	}

	/**
	 * Gets the value of the reportingEntityCategory property.
	 * 
	 * @return possible object is {@link ReportingEntityCategory }
	 * 
	 */
	public ReportingEntityCategory getReportingEntityCategory() {
		return reportingEntityCategory;
	}

	/**
	 * Sets the value of the reportingEntityCategory property.
	 * 
	 * @param value
	 *            allowed object is {@link ReportingEntityCategory }
	 * 
	 */
	public void setReportingEntityCategory(ReportingEntityCategory value) {
		this.reportingEntityCategory = value;
	}

	/**
	 * Gets the value of the reRegistrationNumber property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRERegistrationNumber() {
		return reRegistrationNumber;
	}

	/**
	 * Sets the value of the reRegistrationNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRERegistrationNumber(String value) {
		this.reRegistrationNumber = value;
	}

	/**
	 * Gets the value of the fiureid property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getFIUREID() {
		return fiureid;
	}

	/**
	 * Sets the value of the fiureid property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFIUREID(String value) {
		this.fiureid = value;
	}

}
