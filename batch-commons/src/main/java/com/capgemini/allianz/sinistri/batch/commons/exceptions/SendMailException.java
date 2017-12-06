package com.capgemini.allianz.sinistri.batch.commons.exceptions;

public class SendMailException extends Exception {

	private static final long serialVersionUID = 1164820301760211536L;

	public SendMailException() {
	}

	public SendMailException(String message) {
		super(message);
	}

	public SendMailException(Throwable cause) {
		super(cause);
	}

	public SendMailException(String message, Throwable cause) {
		super(message, cause);
	}

}
