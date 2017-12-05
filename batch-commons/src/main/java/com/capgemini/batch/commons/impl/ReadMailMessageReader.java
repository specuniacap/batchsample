package com.capgemini.batch.commons.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.capgemini.batch.commons.dto.MailMessageBean;
import com.capgemini.batch.commons.interfaces.IReadMailMessageReader;

public class ReadMailMessageReader implements IReadMailMessageReader {
	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	public void manageMessage(MailMessageBean mailMessageBean) throws Exception {
		logger.info("start manageMessage");
		
		logger.info("end manageMessage");
	}

}
