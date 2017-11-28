package com.capgemini.batch.prova.launchers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.capgemini.batch.commons.launchers.ILauncher;
import com.capgemini.batch.commons.launchers.Launcher;

public abstract class  App {
	protected static Log logger = LogFactory.getLog(App.class);

	private static ILauncher appUtils;

	public static void main(String[] args) {
		if (args == null || args.length < 1)
			throw new RuntimeException("Parametro jobIdentifier non specificato in args");

		String[] springConfig = { "com-capgemini-batch-" + args[0] + "-context.xml" };
		ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);

		appUtils = context.getBean(Launcher.class);
		appUtils.launch(context);
		
	}
}
