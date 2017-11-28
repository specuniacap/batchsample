package com.capgemini.batch.commons.launchers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Launcher implements ILauncher {
	protected Log logger = LogFactory.getLog(getClass());

	@Value("#{fileApplicationProperties['launcher.mutex.dir']}")
	private String launcherMutexDir;

	public void launch(ApplicationContext context) {

		MutexExecutionChecker mec = new MutexExecutionChecker();
		boolean deleteFileMutex = true;
		try {
			Job job = (Job) context.getBean(Job.class);
			
			logger.info("Going to run job [" + job.getName() + "]");

			mec.setBatchInExecutionFilePath(launcherMutexDir + job.getName() + ".mutex");
			if (mec.isFileInExecutionPresent()) {
				deleteFileMutex = false;
				logger.warn("Execution aborted: file mutex [" + mec.getBatchInExecutionFilePath() + "] presente");
				return;
			} else
				mec.createInExecutionFile();

			JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");

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
