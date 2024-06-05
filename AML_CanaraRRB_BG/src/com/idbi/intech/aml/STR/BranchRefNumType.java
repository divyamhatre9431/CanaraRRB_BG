//
// This file was com.idbiintech.str by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.idbiintech.str on: 2011.07.28 at 10:17:54 AM IST 
//


package com.idbi.intech.aml.STR;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BranchRefNumType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BranchRefNumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="R"/>
 *     &lt;enumeration value="B"/>
 *     &lt;enumeration value="I"/>
 *     &lt;enumeration value="M"/>
 *     &lt;enumeration value="S"/>
 *     &lt;enumeration value="Z"/>
 *     &lt;enumeration value="X"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BranchRefNumType")
@XmlEnum
public enum BranchRefNumType {

    R,
    B,
    I,
    M,
    S,
    Z,
    X;

    public String value() {
        return name();
    }

    public static BranchRefNumType fromValue(String v) {
        return valueOf(v);
    }

}
