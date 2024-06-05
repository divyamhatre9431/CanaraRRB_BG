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
 * <p>Java class for StateCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StateCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AN"/>
 *     &lt;enumeration value="AP"/>
 *     &lt;enumeration value="AR"/>
 *     &lt;enumeration value="AS"/>
 *     &lt;enumeration value="BR"/>
 *     &lt;enumeration value="CH"/>
 *     &lt;enumeration value="CG"/>
 *     &lt;enumeration value="DN"/>
 *     &lt;enumeration value="DD"/>
 *     &lt;enumeration value="DL"/>
 *     &lt;enumeration value="GA"/>
 *     &lt;enumeration value="GJ"/>
 *     &lt;enumeration value="HR"/>
 *     &lt;enumeration value="HP"/>
 *     &lt;enumeration value="JK"/>
 *     &lt;enumeration value="JH"/>
 *     &lt;enumeration value="KA"/>
 *     &lt;enumeration value="KL"/>
 *     &lt;enumeration value="LD"/>
 *     &lt;enumeration value="MP"/>
 *     &lt;enumeration value="MH"/>
 *     &lt;enumeration value="MN"/>
 *     &lt;enumeration value="ML"/>
 *     &lt;enumeration value="MZ"/>
 *     &lt;enumeration value="NL"/>
 *     &lt;enumeration value="OR"/>
 *     &lt;enumeration value="PY"/>
 *     &lt;enumeration value="PB"/>
 *     &lt;enumeration value="RJ"/>
 *     &lt;enumeration value="SK"/>
 *     &lt;enumeration value="TN"/>
 *     &lt;enumeration value="TR"/>
 *     &lt;enumeration value="UP"/>
 *     &lt;enumeration value="UA"/>
 *     &lt;enumeration value="WB"/>
 *     &lt;enumeration value="XX"/>
 *     &lt;enumeration value="ZZ"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StateCode")
@XmlEnum
public enum StateCode {

    AN,
    AP,
    AR,
    AS,
    BR,
    CH,
    CG,
    DN,
    DD,
    DL,
    GA,
    GJ,
    HR,
    HP,
    JK,
    JH,
    KA,
    KL,
    LD,
    MP,
    MH,
    MN,
    ML,
    MZ,
    NL,
    OR,
    PY,
    PB,
    RJ,
    SK,
    TN,
    TR,
    UP,
    UA,
    WB,
    XX,
    ZZ;

    public String value() {
        return name();
    }

    public static StateCode fromValue(String v) {
        return valueOf(v);
    }

}
