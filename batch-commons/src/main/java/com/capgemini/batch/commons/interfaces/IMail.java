package com.capgemini.batch.commons.interfaces;

import com.capgemini.batch.commons.impl.ReadMailException;

public interface IMail {

	public void readMessages() throws ReadMailException;
}
