package com.capgemini.batch.prova.tasklets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class ManageFileTasklet implements Tasklet {

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		String fileIn = (String) arg1.getStepContext().getStepExecutionContext().get("fileIn");
		
		logger.debug("ManageFileTasklet fileIn = " + fileIn);
		
		return null;
	}

}