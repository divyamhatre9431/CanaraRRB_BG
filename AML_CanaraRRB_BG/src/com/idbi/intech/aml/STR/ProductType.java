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
 * <p>Java class for ProductType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ProductType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BD"/>
 *     &lt;enumeration value="ST"/>
 *     &lt;enumeration value="CD"/>
 *     &lt;enumeration value="CP"/>
 *     &lt;enumeration value="EQ"/>
 *     &lt;enumeration value="FU"/>
 *     &lt;enumeration value="OP"/>
 *     &lt;enumeration value="DF"/>
 *     &lt;enumeration value="EF"/>
 *     &lt;enumeration value="HF"/>
 *     &lt;enumeration value="LF"/>
 *     &lt;enumeration value="MF"/>
 *     &lt;enumeration value="XF"/>
 *     &lt;enumeration value="CO"/>
 *     &lt;enumeration value="IP"/>
 *     &lt;enumeration value="ZZ"/>
 *     &lt;enumeration value="XX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ProductType")
@XmlEnum
public enum ProductType {

    BD,
    ST,
    CD,
    CP,
    EQ,
    FU,
    OP,
    DF,
    EF,
    HF,
    LF,
    MF,
    XF,
    CO,
    IP,
    ZZ,
    XX;

    public String value() {
        return name();
    }

    public static ProductType fromValue(String v) {
        return valueOf(v);
    }

}
