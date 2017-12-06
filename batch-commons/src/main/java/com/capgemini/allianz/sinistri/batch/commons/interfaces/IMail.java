package com.capgemini.allianz.sinistri.batch.commons.interfaces;

import com.capgemini.allianz.sinistri.batch.commons.impl.ReadMailException;

public interface IMail {

	public void readMessages() throws ReadMailException;
}
