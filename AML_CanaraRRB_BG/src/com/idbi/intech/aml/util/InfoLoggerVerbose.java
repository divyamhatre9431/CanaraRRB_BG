/**
 * © Copyright IDBI intech Limited
 * 
 * File Name  : InfoLoggerVerbose.java
 * Created By : Jignesh Kansara
 * 
 * Modification History
 * 
 * 11-07-2011	Jignesh Kansara		Initial version
 */

package com.idbi.intech.aml.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class InfoLoggerVerbose extends InfoLoggerDebug implements Serializable {

	/**
	 * Constructor for InfoLoggerVerbose.
	 */
	public InfoLoggerVerbose() {
		super();
	}

	public void logVerboseText(String currentClass, String method, String aText) {
		InfoLogger.writeToFile(this.getLogFileName(), InfoLogger.formatText("V",
				currentClass, method, aText));
	}
}
