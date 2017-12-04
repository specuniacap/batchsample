package com.capgemini.batch.commons.impl;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capgemini.batch.commons.dto.MailMessageBean;
import com.capgemini.batch.commons.interfaces.IMail;
import com.capgemini.batch.commons.interfaces.IReadMailMessageReader;
import com.capgemini.batch.commons.interfaces.IReadMailMessageSearcher;

@Component
public class ReadMailManager implements IMail {
	
	@Autowired
	private IReadMailMessageReader readMailMessageReaderInstance;
	
	public static final String MAIL_SERVER_TYPE_POP3 = "pop3";
	public static final String MAIL_SERVER_TYPE_IMAP = "imap";
	public static final String MAIL_SERVER_TYPE_IMAPS = "imaps";

	public static final int MAIL_SERVER_PORT_POP3 = 110;
	public static final int MAIL_SERVER_PORT_IMAP = 143;
	public static final int MAIL_SERVER_PORT_IMAPS = 993;

	public static final String HeaderPec_XRicevuta = "X-Ricevuta";
	public static final String HeaderPec_XTrasporto = "X-Trasporto";
	public static final String HeaderPec_XRiferimentoMessageID = "X-Riferimento-Message-ID";

	public static final String HeaderPec_XRicevuta_PresaInCarico = "presa-in-carico";
	public static final String HeaderPec_XRicevuta_Accettazione = "accettazione";
	public static final String HeaderPec_XRicevuta_AvvenutaConsegna = "avvenuta-consegna";
	public static final String HeaderPec_XRicevuta_NonAccettazione = "non-accettazione";
	public static final String HeaderPec_XRicevuta_PreavvisoErroreConsegna = "preavviso-errore-consegna";
	public static final String HeaderPec_XRicevuta_RilevazioneVirus = "rilevazione-virus";
	public static final String HeaderPec_XRicevuta_ErroreConsegna = "errore-consegna";

	public static final String HeaderPec_XTrasporto_Errore = "errore";
	public static final String HeaderPec_XTrasporto_PostaCertificata = "posta-certificata";

	private String username, password, host, readMailMessageReaderClassName, connectionTimeoutMsec = "10000", folderName;
	private String readMailMessageSearcherClassName = "com.capgemini.batch.commons.impl.DefaultReadMailMessageSearcher";
	private boolean markSeenOnReadOk = false;
	private boolean debug = false;
	private String debugFilePath;

	public boolean isMarkSeenOnReadOk() {
		return markSeenOnReadOk;
	}

	public void setMarkSeenOnReadOk(boolean markSeenOnReadOk) {
		this.markSeenOnReadOk = markSeenOnReadOk;
	}

	private boolean deleteOnReadOk = true;
	private String mailServerType = MAIL_SERVER_TYPE_POP3;

	public String getMailServerType() {
		return mailServerType;
	}

	public void setMailServerType(String mailServerType) {
		if (!MAIL_SERVER_TYPE_POP3.equals(mailServerType) && !MAIL_SERVER_TYPE_IMAP.equals(mailServerType)
				&& !MAIL_SERVER_TYPE_IMAPS.equals(mailServerType))
			throw new RuntimeException("Only " + MAIL_SERVER_TYPE_POP3 + "/" + MAIL_SERVER_TYPE_IMAP + "/" + MAIL_SERVER_TYPE_IMAPS
					+ " are allowed mail server type");
		this.mailServerType = mailServerType;
	}

	public boolean isDeleteOnReadOk() {
		return deleteOnReadOk;
	}

	public void setDeleteOnReadOk(boolean deleteOnReadOk) {
		this.deleteOnReadOk = deleteOnReadOk;
	}

	private Map<String, Object> searchParameters = new HashMap<String, Object>();

	public Map<String, Object> getSearchParameters() {
		return searchParameters;
	}

	public void setSearchParameters(Map<String, Object> searchParameters) {
		if (searchParameters == null)
			throw new RuntimeException("searchParameters cannot be null");

		this.searchParameters = searchParameters;
	}

	public String getReadMailMessageSearcherClassName() {
		return readMailMessageSearcherClassName;
	}

	public void setReadMailMessageSearcherClassName(String readMailMessageSearcherClassName) {
		if (StringUtils.isEmpty(readMailMessageSearcherClassName))
			throw new RuntimeException("readMailMessageSearcherClassName cannot be null or empty");

		this.readMailMessageSearcherClassName = readMailMessageSearcherClassName;
	}

	private int port = -1;

	private static final Log _logger = LogFactory.getLog(ReadMailManager.class.getName());

	public String getReadMailMessageReaderClassName() {
		return readMailMessageReaderClassName;
	}

	public void setReadMailMessageReaderClassName(String readMailMessageReaderClassName) {
		this.readMailMessageReaderClassName = readMailMessageReaderClassName;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void readMessages() throws ReadMailException {
		try {
			if (port == -1)
				port = (MAIL_SERVER_TYPE_POP3.equals(mailServerType) ? MAIL_SERVER_PORT_POP3
						: (MAIL_SERVER_TYPE_IMAP.equals(mailServerType) ? MAIL_SERVER_PORT_IMAP : MAIL_SERVER_PORT_IMAPS));

			// Create empty properties
			Properties props = new Properties();
			props.setProperty("mail." + mailServerType + ".connectiontimeout", connectionTimeoutMsec);

			// Get session
			Session session = Session.getInstance(props, null);
			session.setDebug(debug);
			if (debug)
				session.setDebugOut(new PrintStream(new File(debugFilePath)));

			// Get the store
			Store store = session.getStore(mailServerType);
			store.connect(host, port, username, password);

			// Get folder
			Folder folder = store.getFolder((StringUtils.isEmpty(folderName) ? "INBOX" : folderName));
			folder.open(Folder.READ_WRITE);

			// Get directory
			Message message[] = ((IReadMailMessageSearcher) Class.forName(readMailMessageSearcherClassName).newInstance()).searchMessages(folder,
					searchParameters);
			_logger.info("Letti " + message.length + " messaggi di email dell'utente " + username);

			for (int i = 0, n = message.length; i < n; i++) {
				MailMessageBean mailMessageBean = new MailMessageBean();
				mailMessageBean.setSender(message[i].getFrom()[0].toString());
				mailMessageBean.setSubject(message[i].getSubject());
				mailMessageBean.setReceivedDate(message[i].getReceivedDate());
				mailMessageBean.setMessageId(message[i].getMessageNumber());
				Map<String, String> headers = new HashMap<String, String>();
				Enumeration<Header> mHeaders = message[i].getAllHeaders();
				while (mHeaders.hasMoreElements()) {
					Header header = mHeaders.nextElement();
					headers.put(header.getName(), header.getValue());
				}
				mailMessageBean.setHeaders(headers);

				ByteArrayOutputStream messageContent = new ByteArrayOutputStream();
				message[i].writeTo(messageContent);

				mailMessageBean.setAllContent(new String(messageContent.toByteArray()));

				InputStream inputStream = message[i].getInputStream();
				byte[] data = new byte[1024];
				int read = 0;
				ByteArrayOutputStream messageBody = new ByteArrayOutputStream();
				while ((read = inputStream.read(data)) > 0)
					messageBody.write(data, 0, read);
				mailMessageBean.setBody(new String(messageBody.toByteArray()));

				boolean inError = false;
				if (StringUtils.isNotEmpty(readMailMessageReaderClassName)) {
					try {
						//Class readMailMessageReaderClass = Class.forName(readMailMessageReaderClassName);
						//IReadMailMessageReader readMailMessageReaderInstance = (IReadMailMessageReader) readMailMessageReaderClass.newInstance();
						readMailMessageReaderInstance.manageMessage(mailMessageBean);
					} catch (Throwable e) {
						_logger.error(e.getMessage(), e);
						inError = true;
					}
				}

				if (!inError) {

					if (deleteOnReadOk) {
						_logger.info("Marked for delete message " + message[i].getMessageNumber() + " with subject /" + message[i].getSubject() + "/");
						message[i].setFlag(Flags.Flag.DELETED, true);
					} else if (markSeenOnReadOk) {
						_logger.info("Marked seen message " + message[i].getMessageNumber() + " with subject /" + message[i].getSubject() + "/");
						message[i].setFlag(Flags.Flag.SEEN, true);
					} else
						_logger.warn("Read message " + message[i].getMessageNumber() + " with subject /" + message[i].getSubject()
								+ "/ ---  not marked for delete nor as seen");
				} else
					_logger.warn("Skip deleting message " + message[i].getMessageNumber() + " with subject /" + message[i].getSubject() + "/");
			}

			// Close connection
			folder.close(true);
			store.close();
		} catch (Exception e) {
			throw new ReadMailException(buildParametersString(), e);
		}

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getConnectionTimeoutMsec() {
		return connectionTimeoutMsec;
	}

	public void setConnectionTimeoutMsec(String connectionTimeoutMsec) {
		this.connectionTimeoutMsec = connectionTimeoutMsec;
	}

	public String buildParametersString() {
		String ret = "";
		ret = ret + "mailHost = /" + host + "/ ";
		ret = ret + "mailPort = /" + port + "/ ";
		ret = ret + "mailServerType = /" + mailServerType + "/ ";
		ret = ret + "folderName = /" + folderName + "/ ";
		ret = ret + "mailAuthenticatorUser = /" + username + "/ ";
		ret = ret + "mailAuthenticatorPassword = /" + password + "/ ";
		ret = ret + "connectionTimeoutMsec = /" + connectionTimeoutMsec + "/ ";

		return ret;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getDebugFilePath() {
		return debugFilePath;
	}

	public void setDebugFilePath(String debugFilePath) {
		this.debugFilePath = debugFilePath;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

}