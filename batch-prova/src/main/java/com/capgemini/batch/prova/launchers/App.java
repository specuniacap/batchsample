package com.capgemini.batch.prova.launchers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.capgemini.batch.commons.launchers.AppUtils;



public class App {
	protected static Log logger = LogFactory.getLog(App.class);

	
	public static void main(String[] args) {
		// lista dei context soring in input
		String[] springConfig = { "com-capgemini-batch-prova-context.xml" };

		// nome del job da eseguire
		new AppUtils().launch(springConfig, "reportJob");

	}
}
