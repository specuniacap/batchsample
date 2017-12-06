package com.capgemini.allianz.sinistri.batch.commons.impl;

import java.util.Map;

import javax.mail.Folder;
import javax.mail.Message;

import com.capgemini.allianz.sinistri.batch.commons.interfaces.IReadMailMessageSearcher;

public class DefaultReadMailMessageSearcher implements IReadMailMessageSearcher {

	@Override
	public Message[] searchMessages(Folder folder, Map<String, Object> searchParameters) throws Exception {
		return folder.getMessages();
	}

}
