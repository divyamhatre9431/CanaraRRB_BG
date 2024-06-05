/**
 * © Copyright IDBI intech Limited
 * 
 * File Name  : InfoLoggerNormal.java
 * Created By : Jignesh Kansara
 * 
 * Modification History
 * 
 * 11-07-2011	Jignesh Kansara		Initial version
 */

package com.idbi.intech.aml.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class InfoLoggerNormal extends InfoLoggerExceptions implements
		Serializable {

	/**
	 * Constructor for InfoLoggerNormal.
	 */
	public InfoLoggerNormal() {
		super();
	}

	public void logNormalText(String currentClass, String method, String aText) {
		InfoLogger.writeToFile(this.getLogFileName(),
				InfoLogger.formatText("N",currentClass, method, aText));
	}

	public void logNormalText(String currentClass, String method, String aText,
			Exception e) {
		InfoLogger.writeToFile(this.getLogFileName(),
				InfoLogger.formatText("N", currentClass, method, aText), e);
	}

}
