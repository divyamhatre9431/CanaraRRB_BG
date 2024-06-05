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
 * <p>Java class for AccountType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AccountType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BS"/>
 *     &lt;enumeration value="BC"/>
 *     &lt;enumeration value="BR"/>
 *     &lt;enumeration value="BD"/>
 *     &lt;enumeration value="BP"/>
 *     &lt;enumeration value="BL"/>
 *     &lt;enumeration value="BT"/>
 *     &lt;enumeration value="BG"/>
 *     &lt;enumeration value="IL"/>
 *     &lt;enumeration value="IE"/>
 *     &lt;enumeration value="IA"/>
 *     &lt;enumeration value="IU"/>
 *     &lt;enumeration value="IH"/>
 *     &lt;enumeration value="IM"/>
 *     &lt;enumeration value="IT"/>
 *     &lt;enumeration value="IB"/>
 *     &lt;enumeration value="IW"/>
 *     &lt;enumeration value="ST"/>
 *     &lt;enumeration value="MF"/>
 *     &lt;enumeration value="DB"/>
 *     &lt;enumeration value="DH"/>
 *     &lt;enumeration value="DC"/>
 *     &lt;enumeration value="ZZ"/>
 *     &lt;enumeration value="XX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AccountType")
@XmlEnum
public enum AccountType {

    BS,
    BC,
    BR,
    BD,
    BP,
    BL,
    BT,
    BG,
    IL,
    IE,
    IA,
    IU,
    IH,
    IM,
    IT,
    IB,
    IW,
    ST,
    MF,
    DB,
    DH,
    DC,
    ZZ,
    XX;

    public String value() {
        return name();
    }

    public static AccountType fromValue(String v) {
        return valueOf(v);
    }

}
