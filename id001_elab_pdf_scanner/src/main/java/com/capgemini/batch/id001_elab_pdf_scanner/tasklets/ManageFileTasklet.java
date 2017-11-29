package com.capgemini.batch.id001_elab_pdf_scanner.tasklets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.capgemini.batch.commons.constants.Constants;
import com.capgemini.batch.commons.interfaces.IExceptionHandler;
import com.capgemini.batch.commons.interfaces.IStringParser;

public class ManageFileTasklet implements Tasklet {

	protected Log logger = LogFactory.getLog(getClass());

	@Autowired
	@Qualifier("fileNameParser")
	private IStringParser fileNameParser;

	@Autowired
	@Qualifier("exceptionHandler")
	private IExceptionHandler exceptionHandler;

	@Override
	public RepeatStatus execute(StepContribution sc, ChunkContext cc) throws Exception {
		String fileIn = (String) cc.getStepContext().getStepExecutionContext().get(Constants.PART_FILE_IN);

		logger.debug("ManageFileTasklet fileIn = " + fileIn);

		try {
			if (fileIn.endsWith("001.JPG.JPG"))
				throw new RuntimeException("ERROR: " + fileIn);

			fileNameParser.parse(fileIn);

		} catch (Throwable t) {
			exceptionHandler.handleException(t, sc, cc);
		}

		return null;
	}

}