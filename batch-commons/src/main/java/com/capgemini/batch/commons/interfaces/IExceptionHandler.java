package com.capgemini.batch.commons.interfaces;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

public interface IExceptionHandler {
	public void handleException(Throwable t, StepContribution arg0, ChunkContext arg1);
}
