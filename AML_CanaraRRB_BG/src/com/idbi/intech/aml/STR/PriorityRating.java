//
// This file was com.idbiintech.str by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// com.idbiintech.str on: 2011.07.28 at 10:17:54 AM IST 
//


package com.idbi.intech.aml.STR;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PriorityRating.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PriorityRating">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="P1"/>
 *     &lt;enumeration value="P2"/>
 *     &lt;enumeration value="P3"/>
 *     &lt;enumeration value="XX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PriorityRating")
@XmlEnum
public enum PriorityRating {

    @XmlEnumValue("P1")
    P_1("P1"),
    @XmlEnumValue("P2")
    P_2("P2"),
    @XmlEnumValue("P3")
    P_3("P3"),
    XX("XX");
    private final String value;

    PriorityRating(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PriorityRating fromValue(String v) {
        for (PriorityRating c: PriorityRating.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
