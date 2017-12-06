package com.capgemini.allianz.sinistri.batch.commons.interfaces;

import java.io.OutputStream;

import com.capgemini.allianz.sinistri.batch.commons.exceptions.SendMailException;

public interface IMailSender {
	public String sendTextMsg(String msgText, OutputStream out) throws SendMailException;
}
