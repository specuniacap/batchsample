package com.capgemini.allianz.sinistri.batch.commons.impl;



import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.capgemini.allianz.sinistri.batch.commons.exceptions.SendMailException;
import com.capgemini.allianz.sinistri.batch.commons.interfaces.IMailSender;
import com.sun.mail.smtp.SMTPSSLTransport;
import com.sun.mail.smtp.SMTPTransport;

public class SendMailManager implements IMailSender{

	private String mailSmtpDomainLocalhost;
	private String to;
	private String replyTo;
	private String cc;
	private String bcc;
	private String from;
	private String subject;
	private String mailHost;
	private int mailPort = -1;
	private boolean smtpsProtocol = false;

	private String mimeType;

	private String mailAuthenticatorUser;
	private String mailAuthenticatorPassword;
	private boolean mailAuthenticator = false;
	private String connectionTimeoutMsec = "10000";
	private String completeEmlFilePath;

	private List<String> attachmentsFileNames = new ArrayList<String>();
	private List<BufferedDataSource> attachmentsBuffers = new ArrayList<BufferedDataSource>();

	private static final Log _logger = LogFactory.getLog(SendMailManager.class.getName());

	public int getMailPort() {
		return mailPort;
	}

	public void setMailPort(int mailPort) {
		this.mailPort = mailPort;
	}

	public String getMailSmtpDomainLocalhost() {
		return mailSmtpDomainLocalhost;
	}

	public void setMailSmtpDomainLocalhost(String mailSmtpDomainLocalhost) {
		this.mailSmtpDomainLocalhost = mailSmtpDomainLocalhost;
	}

	public String getMailAuthenticatorUser() {
		return mailAuthenticatorUser;
	}

	public void setMailAuthenticatorUser(String mailAuthenticatorUser) {
		this.mailAuthenticatorUser = mailAuthenticatorUser;
	}

	public String getMailAuthenticatorPassword() {
		return mailAuthenticatorPassword;
	}

	public void setMailAuthenticatorPassword(String mailAuthenticatorPassword) {
		this.mailAuthenticatorPassword = mailAuthenticatorPassword;
	}

	public boolean isMailAuthenticator() {
		return mailAuthenticator;
	}

	public void setMailAuthenticator(boolean mailAuthenticator) {
		this.mailAuthenticator = mailAuthenticator;
	}

	public void addAttachmentFile(String filePath) {
		if (filePath != null)
			attachmentsFileNames.add(filePath);
	}

	public void addAttachmentBuffer(byte[] bytes, String fileName) {
		if (bytes != null && fileName != null) {
			BufferedDataSource bds = new BufferedDataSource(bytes, fileName);
			attachmentsBuffers.add(bds);
		}
	}

	public String sendTextMsg(String msgText) throws SendMailException {
		return this.sendTextMsg(msgText, null);
	}

	@SuppressWarnings("rawtypes")
	public String sendTextMsg(String msgText, OutputStream out) throws SendMailException {
		Properties props = new Properties();

		String protocol = isSmtpsProtocol() ? "smtps" : "smtp";

		if (isSmtpsProtocol()) {
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtps.auth", "true");
		}

		if (mailSmtpDomainLocalhost != null) {
			props.put("mail." + protocol + ".localhost", mailSmtpDomainLocalhost);
		}
		if (mailHost != null) {
			props.put("mail." + protocol + ".host", mailHost);
		}
		if (mailAuthenticator) {
			props.put("mail." + protocol + ".auth", "" + mailAuthenticator);
		}
		if (mailPort != -1) {
			props.put("mail." + protocol + ".port", "" + mailPort);
		}

		props.setProperty("mail." + protocol + ".connectiontimeout", connectionTimeoutMsec);

		try {
			Authenticator auth = null;
			if (mailAuthenticator)
				auth = new SMTPAuthenticator();

			Session session = Session.getInstance(props, auth);

			Message msg = new MimeMessage(session);
			if (from != null)
				msg.setFrom(new InternetAddress(from));
			else
				msg.setFrom();

			if (replyTo != null)
				msg.setReplyTo(InternetAddress.parse(replyTo, false));

			if (cc != null)
				msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
			if (bcc != null)
				msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));

			msg.setSentDate(new Date());
			if (to != null)
				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
			msg.setSubject(subject);

			if (attachmentsFileNames.size() == 0 && attachmentsBuffers.size() == 0) {
				if (StringUtils.isEmpty(mimeType))
					msg.setText(msgText);
				else
					msg.setContent(msgText, mimeType);
			} else {
				MimeMultipart mimeMultipart = new MimeMultipart();

				BodyPart messageBodyPart = new MimeBodyPart();
				if (StringUtils.isEmpty(mimeType))
					messageBodyPart.setText(msgText);
				else
					messageBodyPart.setContent(msgText, mimeType);

				mimeMultipart.addBodyPart(messageBodyPart);

				// verifico se ci sono file da allegare
				for (int i = 0; i < attachmentsFileNames.size(); i++) {
					BodyPart attachBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(attachmentsFileNames.get(i));
					attachBodyPart.setDataHandler(new DataHandler(source));

					File f = new File(attachmentsFileNames.get(i));
					attachBodyPart.setFileName(f.getName());
					f = null;

					mimeMultipart.addBodyPart(attachBodyPart);
				}

				for (int i = 0; i < attachmentsBuffers.size(); i++) {
					BodyPart attachBodyPart = new MimeBodyPart();
					DataSource source = attachmentsBuffers.get(i);
					attachBodyPart.setDataHandler(new DataHandler(source));
					attachBodyPart.setFileName(source.getName());

					mimeMultipart.addBodyPart(attachBodyPart);
				}

				msg.setContent(mimeMultipart);
			}

			// gestisco il backup in formato RFC 822
			try {
				if (out != null) {
					msg.writeTo(out);
					_logger.debug("msg.writeTo executed");
				}
			} catch (Exception e) {
				_logger.error("msg.writeTo exception", e);
			}

			String messageId = null;
			if (mailHost != null) {

				if (isSmtpsProtocol()) {
					msg.saveChanges();

					SMTPTransport t = (SMTPSSLTransport) session.getTransport(protocol);
					t.setStartTLS(true); // <-- impostiamo il flag per iniziare
											// la comunicazione sicura
					t.connect(mailHost, mailAuthenticatorUser, mailAuthenticatorPassword);

					t.sendMessage(msg, msg.getAllRecipients());
					String[] headerNames = { "Message-Id" };
					Enumeration headers = msg.getMatchingHeaders(headerNames);

					while (headers.hasMoreElements()) { //
						Header header = (Header) headers.nextElement();
						messageId = header.getValue();
					}

					if (StringUtils.isNotEmpty(completeEmlFilePath))
						msg.writeTo(new FileOutputStream(new File(completeEmlFilePath)));

					if (t != null)
						t.close();
				} else
					Transport.send(msg);

			} else
				throw new SendMailException("mailHost is null");

			return messageId;

		} catch (Exception a) {
			_logger.error(buildParametersString(msgText) + " --- " + a.getMessage(), a);
			throw new SendMailException(buildParametersString(msgText) + " --- " + a.getMessage(), a);
		}
	}

	public String buildParametersString(String msgText) {
		String ret = "";
		ret = ret + "mailHost = /" + mailHost + "/ ";
		ret = ret + "mailPort = /" + mailPort + "/ ";
		ret = ret + "mailAuthenticator = /" + mailAuthenticator + "/ ";
		ret = ret + "mailAuthenticatorUser = /" + mailAuthenticatorUser + "/ ";
		ret = ret + "mailAuthenticatorPassword = /" + mailAuthenticatorPassword + "/ ";
		ret = ret + "from = /" + from + "/ ";
		ret = ret + "replyTo = /" + replyTo + "/ ";
		ret = ret + "cc = /" + cc + "/ ";
		ret = ret + "bcc = /" + bcc + "/ ";
		ret = ret + "to = /" + to + "/ ";
		ret = ret + "subject = /" + subject + "/ ";
		ret = ret + "mimeType = /" + mimeType + "/ ";
		ret = ret + "connectionTimeoutMsec = /" + connectionTimeoutMsec + "/ ";
		ret = ret + "mailSmtpDomainLocalhost = /" + mailSmtpDomainLocalhost + "/ ";
		ret = ret + "msgText = /" + msgText + "/";

		return ret;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMailHost() {
		return mailHost;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(mailAuthenticatorUser, mailAuthenticatorPassword);
		}
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getConnectionTimeoutMsec() {
		return connectionTimeoutMsec;
	}

	public void setConnectionTimeoutMsec(String connectionTimeoutMsec) {
		this.connectionTimeoutMsec = connectionTimeoutMsec;
	}

	public boolean isSmtpsProtocol() {
		return smtpsProtocol;
	}

	public void setSmtpsProtocol(boolean smtpsProtocol) {
		this.smtpsProtocol = smtpsProtocol;
	}

	public String getCompleteEmlFilePath() {
		return completeEmlFilePath;
	}

	public void setCompleteEmlFilePath(String completeEmlFilePath) {
		this.completeEmlFilePath = completeEmlFilePath;
	}

}
