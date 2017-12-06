package com.capgemini.allianz.sinistri.batch.commons.interfaces;

import java.util.Map;

import javax.mail.Folder;
import javax.mail.Message;

public interface IReadMailMessageSearcher {
	public Message[] searchMessages(Folder folder, Map<String, Object> searchParameters) throws Exception;
}
