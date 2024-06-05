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
 * <p>Java class for PurposeCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PurposeCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="S0001"/>
 *     &lt;enumeration value="S0002"/>
 *     &lt;enumeration value="S0003"/>
 *     &lt;enumeration value="S0004"/>
 *     &lt;enumeration value="S0005"/>
 *     &lt;enumeration value="S0006"/>
 *     &lt;enumeration value="S0007"/>
 *     &lt;enumeration value="S0008"/>
 *     &lt;enumeration value="S0009"/>
 *     &lt;enumeration value="S0010"/>
 *     &lt;enumeration value="S0011"/>
 *     &lt;enumeration value="S0012"/>
 *     &lt;enumeration value="S0013"/>
 *     &lt;enumeration value="S0014"/>
 *     &lt;enumeration value="S0015"/>
 *     &lt;enumeration value="S0016"/>
 *     &lt;enumeration value="S0017"/>
 *     &lt;enumeration value="S0018"/>
 *     &lt;enumeration value="S0101"/>
 *     &lt;enumeration value="S0102"/>
 *     &lt;enumeration value="S0103"/>
 *     &lt;enumeration value="S0104"/>
 *     &lt;enumeration value="S0201"/>
 *     &lt;enumeration value="S0202"/>
 *     &lt;enumeration value="S0203"/>
 *     &lt;enumeration value="S0204"/>
 *     &lt;enumeration value="S0205"/>
 *     &lt;enumeration value="S0206"/>
 *     &lt;enumeration value="S0207"/>
 *     &lt;enumeration value="S0208"/>
 *     &lt;enumeration value="S0209"/>
 *     &lt;enumeration value="S0210"/>
 *     &lt;enumeration value="S0211"/>
 *     &lt;enumeration value="S0212"/>
 *     &lt;enumeration value="S0213"/>
 *     &lt;enumeration value="S0301"/>
 *     &lt;enumeration value="S0302"/>
 *     &lt;enumeration value="S0303"/>
 *     &lt;enumeration value="S0304"/>
 *     &lt;enumeration value="S0305"/>
 *     &lt;enumeration value="S0306"/>
 *     &lt;enumeration value="S0401"/>
 *     &lt;enumeration value="S0402"/>
 *     &lt;enumeration value="S0403"/>
 *     &lt;enumeration value="S0404"/>
 *     &lt;enumeration value="S0501"/>
 *     &lt;enumeration value="S0502"/>
 *     &lt;enumeration value="S0601"/>
 *     &lt;enumeration value="S0602"/>
 *     &lt;enumeration value="S0603"/>
 *     &lt;enumeration value="S0604"/>
 *     &lt;enumeration value="S0605"/>
 *     &lt;enumeration value="S0606"/>
 *     &lt;enumeration value="S0701"/>
 *     &lt;enumeration value="S0702"/>
 *     &lt;enumeration value="S0703"/>
 *     &lt;enumeration value="S0801"/>
 *     &lt;enumeration value="S0802"/>
 *     &lt;enumeration value="S0803"/>
 *     &lt;enumeration value="S0804"/>
 *     &lt;enumeration value="S0805"/>
 *     &lt;enumeration value="S0806"/>
 *     &lt;enumeration value="S0901"/>
 *     &lt;enumeration value="S0902"/>
 *     &lt;enumeration value="S1001"/>
 *     &lt;enumeration value="S1002"/>
 *     &lt;enumeration value="S1003"/>
 *     &lt;enumeration value="S1004"/>
 *     &lt;enumeration value="S1005"/>
 *     &lt;enumeration value="S1006"/>
 *     &lt;enumeration value="S1007"/>
 *     &lt;enumeration value="S1008"/>
 *     &lt;enumeration value="S1009"/>
 *     &lt;enumeration value="S1010"/>
 *     &lt;enumeration value="S1011"/>
 *     &lt;enumeration value="S1012"/>
 *     &lt;enumeration value="S1013"/>
 *     &lt;enumeration value="S1019"/>
 *     &lt;enumeration value="S1101"/>
 *     &lt;enumeration value="S1102"/>
 *     &lt;enumeration value="S1201"/>
 *     &lt;enumeration value="S1202"/>
 *     &lt;enumeration value="S1301"/>
 *     &lt;enumeration value="S1302"/>
 *     &lt;enumeration value="S1303"/>
 *     &lt;enumeration value="S1304"/>
 *     &lt;enumeration value="S1305"/>
 *     &lt;enumeration value="S1306"/>
 *     &lt;enumeration value="S1401"/>
 *     &lt;enumeration value="S1402"/>
 *     &lt;enumeration value="S1403"/>
 *     &lt;enumeration value="S1404"/>
 *     &lt;enumeration value="S1405"/>
 *     &lt;enumeration value="S1406"/>
 *     &lt;enumeration value="S1407"/>
 *     &lt;enumeration value="S1501"/>
 *     &lt;enumeration value="S1502"/>
 *     &lt;enumeration value="S1503"/>
 *     &lt;enumeration value="S1504"/>
 *     &lt;enumeration value="P0001"/>
 *     &lt;enumeration value="P0002"/>
 *     &lt;enumeration value="P0003"/>
 *     &lt;enumeration value="P0004"/>
 *     &lt;enumeration value="P0005"/>
 *     &lt;enumeration value="P0006"/>
 *     &lt;enumeration value="P0007"/>
 *     &lt;enumeration value="P0008"/>
 *     &lt;enumeration value="P0009"/>
 *     &lt;enumeration value="P0010"/>
 *     &lt;enumeration value="P0011"/>
 *     &lt;enumeration value="P0012"/>
 *     &lt;enumeration value="P0013"/>
 *     &lt;enumeration value="P0014"/>
 *     &lt;enumeration value="P0015"/>
 *     &lt;enumeration value="P0016"/>
 *     &lt;enumeration value="P0017"/>
 *     &lt;enumeration value="P0018"/>
 *     &lt;enumeration value="P0101"/>
 *     &lt;enumeration value="P0102"/>
 *     &lt;enumeration value="P0103"/>
 *     &lt;enumeration value="P0104"/>
 *     &lt;enumeration value="P0105"/>
 *     &lt;enumeration value="P0106"/>
 *     &lt;enumeration value="P0107"/>
 *     &lt;enumeration value="P0201"/>
 *     &lt;enumeration value="P0202"/>
 *     &lt;enumeration value="P0205"/>
 *     &lt;enumeration value="P0207"/>
 *     &lt;enumeration value="P0208"/>
 *     &lt;enumeration value="P0211"/>
 *     &lt;enumeration value="P0213"/>
 *     &lt;enumeration value="P0301"/>
 *     &lt;enumeration value="P0308"/>
 *     &lt;enumeration value="P0401"/>
 *     &lt;enumeration value="P0402"/>
 *     &lt;enumeration value="P0403"/>
 *     &lt;enumeration value="P0404"/>
 *     &lt;enumeration value="P0501"/>
 *     &lt;enumeration value="P0601"/>
 *     &lt;enumeration value="P0602"/>
 *     &lt;enumeration value="P0603"/>
 *     &lt;enumeration value="P0604"/>
 *     &lt;enumeration value="P0605"/>
 *     &lt;enumeration value="P0606"/>
 *     &lt;enumeration value="P0701"/>
 *     &lt;enumeration value="P0702"/>
 *     &lt;enumeration value="P0703"/>
 *     &lt;enumeration value="P0801"/>
 *     &lt;enumeration value="P0802"/>
 *     &lt;enumeration value="P0803"/>
 *     &lt;enumeration value="P0804"/>
 *     &lt;enumeration value="P0805"/>
 *     &lt;enumeration value="P0806"/>
 *     &lt;enumeration value="P0807"/>
 *     &lt;enumeration value="P0901"/>
 *     &lt;enumeration value="P0902"/>
 *     &lt;enumeration value="P1001"/>
 *     &lt;enumeration value="P1002"/>
 *     &lt;enumeration value="P1003"/>
 *     &lt;enumeration value="P1004"/>
 *     &lt;enumeration value="P1005"/>
 *     &lt;enumeration value="P1006"/>
 *     &lt;enumeration value="P1007"/>
 *     &lt;enumeration value="P1008"/>
 *     &lt;enumeration value="P1009"/>
 *     &lt;enumeration value="P1010"/>
 *     &lt;enumeration value="P1011"/>
 *     &lt;enumeration value="P1012"/>
 *     &lt;enumeration value="P1013"/>
 *     &lt;enumeration value="P1019"/>
 *     &lt;enumeration value="P1101"/>
 *     &lt;enumeration value="P1102"/>
 *     &lt;enumeration value="P1201"/>
 *     &lt;enumeration value="P1203"/>
 *     &lt;enumeration value="P1301"/>
 *     &lt;enumeration value="P1302"/>
 *     &lt;enumeration value="P1303"/>
 *     &lt;enumeration value="P1304"/>
 *     &lt;enumeration value="P1306"/>
 *     &lt;enumeration value="P1401"/>
 *     &lt;enumeration value="P1403"/>
 *     &lt;enumeration value="P1404"/>
 *     &lt;enumeration value="P1405"/>
 *     &lt;enumeration value="P1406"/>
 *     &lt;enumeration value="P1407"/>
 *     &lt;enumeration value="P1501"/>
 *     &lt;enumeration value="P1502"/>
 *     &lt;enumeration value="P1503"/>
 *     &lt;enumeration value="P0091"/>
 *     &lt;enumeration value="P0092"/>
 *     &lt;enumeration value="P0093"/>
 *     &lt;enumeration value="P0094"/>
 *     &lt;enumeration value="P0095"/>
 *     &lt;enumeration value="P0100"/>
 *     &lt;enumeration value="P0144"/>
 *     &lt;enumeration value="P1590"/>
 *     &lt;enumeration value="P1591"/>
 *     &lt;enumeration value="S0091"/>
 *     &lt;enumeration value="S0092"/>
 *     &lt;enumeration value="S0093"/>
 *     &lt;enumeration value="S0094"/>
 *     &lt;enumeration value="S0095"/>
 *     &lt;enumeration value="S0144"/>
 *     &lt;enumeration value="S0190"/>
 *     &lt;enumeration value="S0191"/>
 *     &lt;enumeration value="S1590"/>
 *     &lt;enumeration value="S1591"/>
 *     &lt;enumeration value="P2088"/>
 *     &lt;enumeration value="P2199"/>
 *     &lt;enumeration value="S2088"/>
 *     &lt;enumeration value="S2199"/>
 *     &lt;enumeration value="XXXXX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PurposeCode")
@XmlEnum
public enum PurposeCode {

    @XmlEnumValue("S0001")
    S_0001("S0001"),
    @XmlEnumValue("S0002")
    S_0002("S0002"),
    @XmlEnumValue("S0003")
    S_0003("S0003"),
    @XmlEnumValue("S0004")
    S_0004("S0004"),
    @XmlEnumValue("S0005")
    S_0005("S0005"),
    @XmlEnumValue("S0006")
    S_0006("S0006"),
    @XmlEnumValue("S0007")
    S_0007("S0007"),
    @XmlEnumValue("S0008")
    S_0008("S0008"),
    @XmlEnumValue("S0009")
    S_0009("S0009"),
    @XmlEnumValue("S0010")
    S_0010("S0010"),
    @XmlEnumValue("S0011")
    S_0011("S0011"),
    @XmlEnumValue("S0012")
    S_0012("S0012"),
    @XmlEnumValue("S0013")
    S_0013("S0013"),
    @XmlEnumValue("S0014")
    S_0014("S0014"),
    @XmlEnumValue("S0015")
    S_0015("S0015"),
    @XmlEnumValue("S0016")
    S_0016("S0016"),
    @XmlEnumValue("S0017")
    S_0017("S0017"),
    @XmlEnumValue("S0018")
    S_0018("S0018"),
    @XmlEnumValue("S0101")
    S_0101("S0101"),
    @XmlEnumValue("S0102")
    S_0102("S0102"),
    @XmlEnumValue("S0103")
    S_0103("S0103"),
    @XmlEnumValue("S0104")
    S_0104("S0104"),
    @XmlEnumValue("S0201")
    S_0201("S0201"),
    @XmlEnumValue("S0202")
    S_0202("S0202"),
    @XmlEnumValue("S0203")
    S_0203("S0203"),
    @XmlEnumValue("S0204")
    S_0204("S0204"),
    @XmlEnumValue("S0205")
    S_0205("S0205"),
    @XmlEnumValue("S0206")
    S_0206("S0206"),
    @XmlEnumValue("S0207")
    S_0207("S0207"),
    @XmlEnumValue("S0208")
    S_0208("S0208"),
    @XmlEnumValue("S0209")
    S_0209("S0209"),
    @XmlEnumValue("S0210")
    S_0210("S0210"),
    @XmlEnumValue("S0211")
    S_0211("S0211"),
    @XmlEnumValue("S0212")
    S_0212("S0212"),
    @XmlEnumValue("S0213")
    S_0213("S0213"),
    @XmlEnumValue("S0301")
    S_0301("S0301"),
    @XmlEnumValue("S0302")
    S_0302("S0302"),
    @XmlEnumValue("S0303")
    S_0303("S0303"),
    @XmlEnumValue("S0304")
    S_0304("S0304"),
    @XmlEnumValue("S0305")
    S_0305("S0305"),
    @XmlEnumValue("S0306")
    S_0306("S0306"),
    @XmlEnumValue("S0401")
    S_0401("S0401"),
    @XmlEnumValue("S0402")
    S_0402("S0402"),
    @XmlEnumValue("S0403")
    S_0403("S0403"),
    @XmlEnumValue("S0404")
    S_0404("S0404"),
    @XmlEnumValue("S0501")
    S_0501("S0501"),
    @XmlEnumValue("S0502")
    S_0502("S0502"),
    @XmlEnumValue("S0601")
    S_0601("S0601"),
    @XmlEnumValue("S0602")
    S_0602("S0602"),
    @XmlEnumValue("S0603")
    S_0603("S0603"),
    @XmlEnumValue("S0604")
    S_0604("S0604"),
    @XmlEnumValue("S0605")
    S_0605("S0605"),
    @XmlEnumValue("S0606")
    S_0606("S0606"),
    @XmlEnumValue("S0701")
    S_0701("S0701"),
    @XmlEnumValue("S0702")
    S_0702("S0702"),
    @XmlEnumValue("S0703")
    S_0703("S0703"),
    @XmlEnumValue("S0801")
    S_0801("S0801"),
    @XmlEnumValue("S0802")
    S_0802("S0802"),
    @XmlEnumValue("S0803")
    S_0803("S0803"),
    @XmlEnumValue("S0804")
    S_0804("S0804"),
    @XmlEnumValue("S0805")
    S_0805("S0805"),
    @XmlEnumValue("S0806")
    S_0806("S0806"),
    @XmlEnumValue("S0901")
    S_0901("S0901"),
    @XmlEnumValue("S0902")
    S_0902("S0902"),
    @XmlEnumValue("S1001")
    S_1001("S1001"),
    @XmlEnumValue("S1002")
    S_1002("S1002"),
    @XmlEnumValue("S1003")
    S_1003("S1003"),
    @XmlEnumValue("S1004")
    S_1004("S1004"),
    @XmlEnumValue("S1005")
    S_1005("S1005"),
    @XmlEnumValue("S1006")
    S_1006("S1006"),
    @XmlEnumValue("S1007")
    S_1007("S1007"),
    @XmlEnumValue("S1008")
    S_1008("S1008"),
    @XmlEnumValue("S1009")
    S_1009("S1009"),
    @XmlEnumValue("S1010")
    S_1010("S1010"),
    @XmlEnumValue("S1011")
    S_1011("S1011"),
    @XmlEnumValue("S1012")
    S_1012("S1012"),
    @XmlEnumValue("S1013")
    S_1013("S1013"),
    @XmlEnumValue("S1019")
    S_1019("S1019"),
    @XmlEnumValue("S1101")
    S_1101("S1101"),
    @XmlEnumValue("S1102")
    S_1102("S1102"),
    @XmlEnumValue("S1201")
    S_1201("S1201"),
    @XmlEnumValue("S1202")
    S_1202("S1202"),
    @XmlEnumValue("S1301")
    S_1301("S1301"),
    @XmlEnumValue("S1302")
    S_1302("S1302"),
    @XmlEnumValue("S1303")
    S_1303("S1303"),
    @XmlEnumValue("S1304")
    S_1304("S1304"),
    @XmlEnumValue("S1305")
    S_1305("S1305"),
    @XmlEnumValue("S1306")
    S_1306("S1306"),
    @XmlEnumValue("S1401")
    S_1401("S1401"),
    @XmlEnumValue("S1402")
    S_1402("S1402"),
    @XmlEnumValue("S1403")
    S_1403("S1403"),
    @XmlEnumValue("S1404")
    S_1404("S1404"),
    @XmlEnumValue("S1405")
    S_1405("S1405"),
    @XmlEnumValue("S1406")
    S_1406("S1406"),
    @XmlEnumValue("S1407")
    S_1407("S1407"),
    @XmlEnumValue("S1501")
    S_1501("S1501"),
    @XmlEnumValue("S1502")
    S_1502("S1502"),
    @XmlEnumValue("S1503")
    S_1503("S1503"),
    @XmlEnumValue("S1504")
    S_1504("S1504"),
    @XmlEnumValue("P0001")
    P_0001("P0001"),
    @XmlEnumValue("P0002")
    P_0002("P0002"),
    @XmlEnumValue("P0003")
    P_0003("P0003"),
    @XmlEnumValue("P0004")
    P_0004("P0004"),
    @XmlEnumValue("P0005")
    P_0005("P0005"),
    @XmlEnumValue("P0006")
    P_0006("P0006"),
    @XmlEnumValue("P0007")
    P_0007("P0007"),
    @XmlEnumValue("P0008")
    P_0008("P0008"),
    @XmlEnumValue("P0009")
    P_0009("P0009"),
    @XmlEnumValue("P0010")
    P_0010("P0010"),
    @XmlEnumValue("P0011")
    P_0011("P0011"),
    @XmlEnumValue("P0012")
    P_0012("P0012"),
    @XmlEnumValue("P0013")
    P_0013("P0013"),
    @XmlEnumValue("P0014")
    P_0014("P0014"),
    @XmlEnumValue("P0015")
    P_0015("P0015"),
    @XmlEnumValue("P0016")
    P_0016("P0016"),
    @XmlEnumValue("P0017")
    P_0017("P0017"),
    @XmlEnumValue("P0018")
    P_0018("P0018"),
    @XmlEnumValue("P0101")
    P_0101("P0101"),
    @XmlEnumValue("P0102")
    P_0102("P0102"),
    @XmlEnumValue("P0103")
    P_0103("P0103"),
    @XmlEnumValue("P0104")
    P_0104("P0104"),
    @XmlEnumValue("P0105")
    P_0105("P0105"),
    @XmlEnumValue("P0106")
    P_0106("P0106"),
    @XmlEnumValue("P0107")
    P_0107("P0107"),
    @XmlEnumValue("P0201")
    P_0201("P0201"),
    @XmlEnumValue("P0202")
    P_0202("P0202"),
    @XmlEnumValue("P0205")
    P_0205("P0205"),
    @XmlEnumValue("P0207")
    P_0207("P0207"),
    @XmlEnumValue("P0208")
    P_0208("P0208"),
    @XmlEnumValue("P0211")
    P_0211("P0211"),
    @XmlEnumValue("P0213")
    P_0213("P0213"),
    @XmlEnumValue("P0301")
    P_0301("P0301"),
    @XmlEnumValue("P0308")
    P_0308("P0308"),
    @XmlEnumValue("P0401")
    P_0401("P0401"),
    @XmlEnumValue("P0402")
    P_0402("P0402"),
    @XmlEnumValue("P0403")
    P_0403("P0403"),
    @XmlEnumValue("P0404")
    P_0404("P0404"),
    @XmlEnumValue("P0501")
    P_0501("P0501"),
    @XmlEnumValue("P0601")
    P_0601("P0601"),
    @XmlEnumValue("P0602")
    P_0602("P0602"),
    @XmlEnumValue("P0603")
    P_0603("P0603"),
    @XmlEnumValue("P0604")
    P_0604("P0604"),
    @XmlEnumValue("P0605")
    P_0605("P0605"),
    @XmlEnumValue("P0606")
    P_0606("P0606"),
    @XmlEnumValue("P0701")
    P_0701("P0701"),
    @XmlEnumValue("P0702")
    P_0702("P0702"),
    @XmlEnumValue("P0703")
    P_0703("P0703"),
    @XmlEnumValue("P0801")
    P_0801("P0801"),
    @XmlEnumValue("P0802")
    P_0802("P0802"),
    @XmlEnumValue("P0803")
    P_0803("P0803"),
    @XmlEnumValue("P0804")
    P_0804("P0804"),
    @XmlEnumValue("P0805")
    P_0805("P0805"),
    @XmlEnumValue("P0806")
    P_0806("P0806"),
    @XmlEnumValue("P0807")
    P_0807("P0807"),
    @XmlEnumValue("P0901")
    P_0901("P0901"),
    @XmlEnumValue("P0902")
    P_0902("P0902"),
    @XmlEnumValue("P1001")
    P_1001("P1001"),
    @XmlEnumValue("P1002")
    P_1002("P1002"),
    @XmlEnumValue("P1003")
    P_1003("P1003"),
    @XmlEnumValue("P1004")
    P_1004("P1004"),
    @XmlEnumValue("P1005")
    P_1005("P1005"),
    @XmlEnumValue("P1006")
    P_1006("P1006"),
    @XmlEnumValue("P1007")
    P_1007("P1007"),
    @XmlEnumValue("P1008")
    P_1008("P1008"),
    @XmlEnumValue("P1009")
    P_1009("P1009"),
    @XmlEnumValue("P1010")
    P_1010("P1010"),
    @XmlEnumValue("P1011")
    P_1011("P1011"),
    @XmlEnumValue("P1012")
    P_1012("P1012"),
    @XmlEnumValue("P1013")
    P_1013("P1013"),
    @XmlEnumValue("P1019")
    P_1019("P1019"),
    @XmlEnumValue("P1101")
    P_1101("P1101"),
    @XmlEnumValue("P1102")
    P_1102("P1102"),
    @XmlEnumValue("P1201")
    P_1201("P1201"),
    @XmlEnumValue("P1203")
    P_1203("P1203"),
    @XmlEnumValue("P1301")
    P_1301("P1301"),
    @XmlEnumValue("P1302")
    P_1302("P1302"),
    @XmlEnumValue("P1303")
    P_1303("P1303"),
    @XmlEnumValue("P1304")
    P_1304("P1304"),
    @XmlEnumValue("P1306")
    P_1306("P1306"),
    @XmlEnumValue("P1401")
    P_1401("P1401"),
    @XmlEnumValue("P1403")
    P_1403("P1403"),
    @XmlEnumValue("P1404")
    P_1404("P1404"),
    @XmlEnumValue("P1405")
    P_1405("P1405"),
    @XmlEnumValue("P1406")
    P_1406("P1406"),
    @XmlEnumValue("P1407")
    P_1407("P1407"),
    @XmlEnumValue("P1501")
    P_1501("P1501"),
    @XmlEnumValue("P1502")
    P_1502("P1502"),
    @XmlEnumValue("P1503")
    P_1503("P1503"),
    @XmlEnumValue("P0091")
    P_0091("P0091"),
    @XmlEnumValue("P0092")
    P_0092("P0092"),
    @XmlEnumValue("P0093")
    P_0093("P0093"),
    @XmlEnumValue("P0094")
    P_0094("P0094"),
    @XmlEnumValue("P0095")
    P_0095("P0095"),
    @XmlEnumValue("P0100")
    P_0100("P0100"),
    @XmlEnumValue("P0144")
    P_0144("P0144"),
    @XmlEnumValue("P1590")
    P_1590("P1590"),
    @XmlEnumValue("P1591")
    P_1591("P1591"),
    @XmlEnumValue("S0091")
    S_0091("S0091"),
    @XmlEnumValue("S0092")
    S_0092("S0092"),
    @XmlEnumValue("S0093")
    S_0093("S0093"),
    @XmlEnumValue("S0094")
    S_0094("S0094"),
    @XmlEnumValue("S0095")
    S_0095("S0095"),
    @XmlEnumValue("S0144")
    S_0144("S0144"),
    @XmlEnumValue("S0190")
    S_0190("S0190"),
    @XmlEnumValue("S0191")
    S_0191("S0191"),
    @XmlEnumValue("S1590")
    S_1590("S1590"),
    @XmlEnumValue("S1591")
    S_1591("S1591"),
    @XmlEnumValue("P2088")
    P_2088("P2088"),
    @XmlEnumValue("P2199")
    P_2199("P2199"),
    @XmlEnumValue("S2088")
    S_2088("S2088"),
    @XmlEnumValue("S2199")
    S_2199("S2199"),
    XXXXX("XXXXX");
    private final String value;

    PurposeCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PurposeCode fromValue(String v) {
        for (PurposeCode c: PurposeCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
