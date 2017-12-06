package com.capgemini.allianz.sinistri.batch.commons.interfaces;

import com.capgemini.allianz.sinistri.batch.commons.exceptions.ReadMailException;

public interface IMailReader {

	public void readMessages() throws ReadMailException;
	
}
