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
 * Header to be used across all the batch reports to be submitted
 * 
 * <p>
 * Java class for BatchHeader complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="BatchHeader">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DataStructureVersion" type="{}DataStructureVersion"/>
 *         &lt;element name="GenerationUtilityVersion">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DataSource" type="{}DataSource"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "BatchHeader")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatchHeader", propOrder = { "dataStructureVersion", "generationUtilityVersion", "dataSource" })
public class BatchHeader {

	@XmlElement(name = "DataStructureVersion", required = true)
	protected String dataStructureVersion;
	@XmlElement(name = "GenerationUtilityVersion", required = true)
	protected String generationUtilityVersion;
	@XmlElement(name = "DataSource", required = true)
	protected DataSource dataSource;

	/**
	 * Gets the value of the dataStructureVersion property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDataStructureVersion() {
		return dataStructureVersion;
	}

	/**
	 * Sets the value of the dataStructureVersion property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDataStructureVersion(String value) {
		this.dataStructureVersion = value;
	}

	/**
	 * Gets the value of the generationUtilityVersion property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getGenerationUtilityVersion() {
		return generationUtilityVersion;
	}

	/**
	 * Sets the value of the generationUtilityVersion property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setGenerationUtilityVersion(String value) {
		this.generationUtilityVersion = value;
	}

	/**
	 * Gets the value of the dataSource property.
	 * 
	 * @return possible object is {@link DataSource }
	 * 
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Sets the value of the dataSource property.
	 * 
	 * @param value
	 *            allowed object is {@link DataSource }
	 * 
	 */
	public void setDataSource(DataSource value) {
		this.dataSource = value;
	}

}
