package com.capgemini.batch.commons.dto;



import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.capgemini.batch.commons.impl.ReadMailManager;

public class MailMessageBean {
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getAllContent() {
		return allContent;
	}

	public void setAllContent(String allContent) {
		this.allContent = allContent;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	private String sender;
	private String allContent;
	private String subject;
	private String body;
	private Date receivedDate;
	private long messageId;
	private Map<String, String> headers;

	@Override
	public String toString() {
		String ret = "";
		ret = ret + "messageId = /" + messageId + "/ ";
		ret = ret + "receivedDate = /" + receivedDate + "/ ";
		ret = ret + "sender = /" + sender + "/ ";
		ret = ret + "subject = /" + subject + "/ ";

		if (headers != null) {
			for (String headerName : headers.keySet()) {
				ret = ret + "Header[" + headerName + "= /" + headers.get(headerName) + "/ ] ";
			}
		} else
			ret = ret + "headers = /" + null + "/ ";

		return ret;
	}

	public String getHeader(String headerName) {
		if (headers == null)
			return null;

		return headers.get(headerName);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getPecRiferimentoMessageId() {
		return getHeader(ReadMailManager.HeaderPec_XRiferimentoMessageID);
	}

	public String getPecEsitoInvio() {
		String esitoInvio = getHeader(ReadMailManager.HeaderPec_XRicevuta);
		if (StringUtils.isEmpty(esitoInvio))
			esitoInvio = getHeader(ReadMailManager.HeaderPec_XTrasporto);

		return esitoInvio;
	}

}
