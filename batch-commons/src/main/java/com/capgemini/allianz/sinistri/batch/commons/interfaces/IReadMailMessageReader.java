package com.capgemini.allianz.sinistri.batch.commons.interfaces;

import com.capgemini.allianz.sinistri.batch.commons.dto.MailMessageBean;

public interface IReadMailMessageReader {
	public void manageMessage(MailMessageBean mailMessageBean) throws Exception;
}
