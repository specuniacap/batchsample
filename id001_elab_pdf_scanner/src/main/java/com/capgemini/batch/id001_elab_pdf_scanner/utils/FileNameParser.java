package com.capgemini.batch.id001_elab_pdf_scanner.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.capgemini.batch.commons.interfaces.IStringParser;

@Component
public class FileNameParser implements IStringParser {

	protected Log logger = LogFactory.getLog(getClass());
	
	@Override
	public Object parse(String input) {
		logger.debug("input = " + input);
		
		return null;
	}

}
