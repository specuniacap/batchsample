package com.capgemini.allianz.sinistri.batch.id001_elab_pdf_scanner.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

import com.capgemini.allianz.sinistri.batch.commons.constants.Constants;
import com.capgemini.allianz.sinistri.batch.commons.interfaces.IExceptionHandler;

public class ExceptionHandler implements IExceptionHandler {

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	public void handleException(Throwable t, StepContribution arg0, ChunkContext arg1) {
		
		String fileIn = (String) arg1.getStepContext().getStepExecutionContext().get(Constants.PART_FILE_IN);

		logger.debug("handleException fileIn = " + fileIn);

	}

}
