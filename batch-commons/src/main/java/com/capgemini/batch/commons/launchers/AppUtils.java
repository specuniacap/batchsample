package com.capgemini.batch.commons.launchers;

import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AppUtils {
	protected Log logger = LogFactory.getLog(AppUtils.class);

	@Resource(name = "fileApplicationProperties")
	private Properties fileApplicationProperties;

	public void launch(String[] springConfig, String jobName) {

		MutexExecutionChecker mec = new MutexExecutionChecker();
		boolean deleteFileMutex = true;
		try {
			mec.setBatchInExecutionFilePath("C:/temp/" + jobName + ".mutex");
			if (mec.isFileInExecutionPresent()) {
				deleteFileMutex = false;
				logger.warn("Execution aborted: file mutex [" + mec.getBatchInExecutionFilePath() + "] presente");
				return;
			} else
				mec.createInExecutionFile();

			ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);
			
			JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
			Job job = (Job) context.getBean(jobName);
			
			logger.debug(fileApplicationProperties);
			
			JobExecution execution = jobLauncher.run(job, new JobParameters());
			logger.info("Exit Status : " + execution.getStatus());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (deleteFileMutex)
				mec.deleteInExecutionFile();
		}

	}

}
