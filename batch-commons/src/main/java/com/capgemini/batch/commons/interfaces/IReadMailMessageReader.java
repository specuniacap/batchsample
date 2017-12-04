package com.capgemini.batch.commons.interfaces;

import com.capgemini.batch.commons.dto.MailMessageBean;

public interface IReadMailMessageReader {
	public void manageMessage(MailMessageBean mailMessageBean) throws Exception;
}
