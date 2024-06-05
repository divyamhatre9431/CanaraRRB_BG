/**
 * © Copyright IDBI intech Limited
 * 
 * File Name  : InfoLoggerDebug.java
 * Created By : Jignesh Kansara
 * 
 * Modification History
 * 
 * 11-07-2011	Jignesh Kansara		Initial version
 */

package com.idbi.intech.aml.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class InfoLoggerDebug extends InfoLoggerTrace implements Serializable {

	/**
	 * Constructor for InfoLoggerDebug.
	 */
	public InfoLoggerDebug() {
		super();
	}

	public void logDebugText(String currentClass, String method, String aText) {
		InfoLogger.writeToFile(this.getLogFileName(), InfoLogger.formatText("D",
				currentClass, method, aText));
	}

}
