package com.idbi.intech.iaml.bankerac;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class AML_WC_EntType {
	static final String record = "entity";
	static final String uid = "id";

	PreparedStatement pstmt = null;

	String id = "";
	String entityType = "";

	public void readXML(String file, Connection connection)
			throws FileNotFoundException, XMLStreamException {
		try {
			HashMap<String, String> hsEC =  new HashMap<String, String>();
			hsEC.put("01", "Government/Country");
			hsEC.put("02", "Principal City");
			hsEC.put("03", "Individual");
			hsEC.put("04", "Vessel");
			hsEC.put("05", "Bank");
			hsEC.put("06", "Other");
			hsEC.put("07", "Minister/Government Official");
			hsEC.put("08", "Company");
			hsEC.put("09", "Political/Religious Organization");

			
			pstmt = connection
					.prepareStatement("insert into aml_wl_enttype values (?,?)");
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = new FileInputStream(file);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {

					StartElement startElement = event.asStartElement();

					if (startElement.getName().getLocalPart() == record) {
						id = "";

						@SuppressWarnings("unchecked")
						Iterator<Attribute> attributes = startElement
								.getAttributes();

						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals(uid)) {
								id = attribute.getValue();
							}
						}
					}
				}

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					if (event.asStartElement().getName().getLocalPart()
							.equals("entityType")) {
						while (eventReader.peek() != null
								&& eventReader.peek().isCharacters()) {
							event = eventReader.nextEvent();
							entityType = event.asCharacters().getData() == null ? ""
									: event.asCharacters().getData()
											.toUpperCase();
							continue;
						}
					}
				}

				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart() == "entityType") {
						if (!entityType.equals("")) {
							String type = hsEC.get(entityType);
							pstmt.setString(1, id);
							pstmt.setString(2, type);

							pstmt.executeUpdate();
							//System.out.println(id + "~" + type);

							entityType = "";
							type = "";
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}
