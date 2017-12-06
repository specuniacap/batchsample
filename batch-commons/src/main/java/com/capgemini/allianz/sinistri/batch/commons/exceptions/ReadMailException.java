package com.capgemini.allianz.sinistri.batch.commons.exceptions;

public class ReadMailException extends Exception {

	private static final long serialVersionUID = -7001046670704335953L;

	public ReadMailException() {
	}

	public ReadMailException(String message) {
		super(message);
	}

	public ReadMailException(Throwable cause) {
		super(cause);
	}

	public ReadMailException(String message, Throwable cause) {
		super(message, cause);
	}

}
