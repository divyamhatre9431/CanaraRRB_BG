package com.idbi.intech.aml.upload.watchlist;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

class XMLReaderWithoutNamespace extends StreamReaderDelegate {
    public XMLReaderWithoutNamespace(XMLStreamReader reader) {
      super(reader);
    }
    @Override
    public String getAttributeNamespace(int arg0) {
      return "";
    }
    @Override
    public String getNamespaceURI() {
      return "";
    }
}