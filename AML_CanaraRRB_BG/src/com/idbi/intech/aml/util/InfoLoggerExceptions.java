/**
 * © Copyright IDBI intech Limited
 * 
 * File Name  : InfoLoggerExceptions.java
 * Created By : Jignesh Kansara
 * 
 * Modification History
 * 
 * 11-07-2011	Jignesh Kansara		Initial version
 */

package com.idbi.intech.aml.util;

import java.io.Serializable;

public class InfoLoggerExceptions extends InfoLogger implements Serializable {

	static final long serialVersionUID = 1;

	/**
	 * Constructor for InfoLoggerNormal.
	 */
	public InfoLoggerExceptions() {
		super();
	}

	public void logExceptionText(String currentClass, String method,
			String aText) {
		InfoLogger.writeToFile(this.getLogFileName(),InfoLogger.formatText("E",currentClass, method, aText));
	}

	public void logExceptionText(String currentClass, String method,
			String aText, Exception e) {
		InfoLogger.writeToFile(this.getLogFileName(),
				InfoLogger.formatText("E",currentClass, method, aText), e);
	}

	public void logVerboseText(String currentClass, String method, String aText) {
	}

	public void logTraceText(String currentClass, String method, String aText) {
	}

	public void logDebugText(String currentClass, String method, String aText) {
	}

	@Override
	public void logNormalText(String currentClass, String method, String aText) {

	}

}
