/**
 * © Copyright IDBI intech Limited
 * 
 * File Name  : InfoLoggerTrace.java
 * Created By : Jignesh Kansara
 * 
 * Modification History
 * 
 * 11-07-2011	Jignesh Kansara		Initial version
 */

package com.idbi.intech.aml.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class InfoLoggerTrace extends InfoLoggerNormal implements Serializable {

	/**
	 * Constructor for InfoLoggerTrace.
	 */
	public InfoLoggerTrace() {
		super();
	}

	public void logTraceText(String currentClass, String method, String aText) {
		InfoLogger.writeToFile(this.getLogFileName(), InfoLogger.formatText("T",
				currentClass, method, aText));
	}

}
