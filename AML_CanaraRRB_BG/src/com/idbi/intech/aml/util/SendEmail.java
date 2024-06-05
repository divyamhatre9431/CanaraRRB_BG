package com.idbi.intech.aml.util;

import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendEmail {
	public static String smtpServer = "smtp.idbiintech.com";
	public static String userName = "";
	public static String password;

	public static synchronized boolean sendEmailNew(ArrayList<String> toList, ArrayList<String> cc, ArrayList<String> bcc, String subject,
			String mailText, String smtpServer, String userName, String password) throws AuthenticationFailedException, SendFailedException,
			MessagingException {

		// String from="";
		if ((toList == null) && (subject == null)) {
			return false;
		}
		if (mailText == null) {
			mailText = "";
		}
		mailText = mailText + "<br><br><br>This information should be treated  confidential with no disclosure to customers under any circumstances as per the requirement of Prevention of Money Laundering ACT-2002.";
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);

		// Setting the 'from', 'to', 'cc' addresses and the 'subject'
		message.setFrom(new InternetAddress(userName));

		// if multiple Recipient
		InternetAddress[] addressTo = new InternetAddress[toList.size()];

		for (int i = 0; i < toList.size(); i++) {
			addressTo[i] = new InternetAddress(toList.get(i));
		}
		message.setRecipients(Message.RecipientType.TO, addressTo);

		// for cc
		if (cc != null && cc.size() > 0) {
			InternetAddress[] addressCC = new InternetAddress[cc.size()];

			for (int i = 0; i < cc.size(); i++) {
				addressCC[i] = new InternetAddress(cc.get(i));
			}
			message.setRecipients(Message.RecipientType.CC, addressCC);
		}

		// for bcc
		if (bcc != null && bcc.size() > 0) {
			InternetAddress[] addressBCC = new InternetAddress[bcc.size()];

			for (int i = 0; i < bcc.size(); i++) {
				addressBCC[i] = new InternetAddress(bcc.get(i));
			}
			message.setRecipients(Message.RecipientType.BCC, addressBCC);
		}

		message.setSubject(subject);

		// Making the mail body as inline and of html type
		MimeMultipart mp = new MimeMultipart();
		MimeBodyPart text = new MimeBodyPart();
		text.setDisposition(Part.INLINE);
		text.setContent(mailText, "text/html");
		mp.addBodyPart(text);
		message.setContent(mp);

		// SMTP authentication
		Transport transport = session.getTransport("smtp");
		transport.connect(smtpServer, userName, password);
		message.saveChanges();
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

		return true;
	}

	public static synchronized boolean sendEmailFile(ArrayList<String> toList, ArrayList<String> ccList, ArrayList<String> bccList, String subject,
			String body, String smtpServer, String userName, String password, ArrayList<String> filenameArray, ArrayList<String> filePathArray)
			throws AuthenticationFailedException, SendFailedException, MessagingException {

		// String from="";
		if ((toList == null) || (toList.size() == 0) || (subject == null)) {
			return false;
		}
		if (body == null) {
			body = "";
		}

		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);

		// Setting the 'from', 'to', 'cc' addresses and the 'subject'
		message.setFrom(new InternetAddress(userName));

		// if multiple Recipients
		if (toList != null) {
			InternetAddress[] addressTo = new InternetAddress[toList.size()];

			for (int i = 0; i < toList.size(); i++) {
				addressTo[i] = new InternetAddress(toList.get(i));
			}

			message.setRecipients(Message.RecipientType.TO, addressTo);
		}

		// for cc
		if (ccList != null) {
			InternetAddress[] addressCC = new InternetAddress[ccList.size()];

			for (int i = 0; i < ccList.size(); i++) {
				addressCC[i] = new InternetAddress(ccList.get(i));
			}
			message.setRecipients(Message.RecipientType.CC, addressCC);
		}

		// for bcc
		if (bccList != null) {
			InternetAddress[] addressBCC = new InternetAddress[bccList.size()];

			for (int i = 0; i < bccList.size(); i++) {
				addressBCC[i] = new InternetAddress(bccList.get(i));
			}
			message.setRecipients(Message.RecipientType.BCC, addressBCC);
		}

		if (subject != null) {
			message.setSubject(subject);
		}

		// Create the message part
		BodyPart text = new MimeBodyPart();

		// Fill the message
		if (body != null) {
			;
			//text.setContent(body, "text/html");//<%@ page contentType=\"text/html; charset=utf-8\"%> 
		}

		// Making the mail body as inline and of html type
		MimeMultipart mp = new MimeMultipart();
		
		// Set text message part
		mp.addBodyPart(text);

		// Part two is attachment
		if (filenameArray != null) {
			for (int i = 0; i < filenameArray.size(); i++) {

				text = new MimeBodyPart();

				DataSource source = new FileDataSource(filePathArray.get(i));
				text.setDataHandler(new DataHandler(source));
				text.setFileName(filenameArray.get(i));
				mp.addBodyPart(text);
			}
		}

		message.setContent(mp);

		// SMTP authentication
		Transport transport = session.getTransport("smtp");
		transport.connect(smtpServer, userName, password);
		message.saveChanges();
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

		return true;
	}

	public static void main(String ar[]) throws AuthenticationFailedException, SendFailedException, MessagingException {
		try {

			ArrayList<String> toList = new ArrayList<String>();

			ArrayList<String> ccList = new ArrayList<String>();
			ArrayList<String> bccList = new ArrayList<String>();

			sendEmailNew(toList, ccList, bccList, "hi",
					"<HTML><HEAD></HEAD><BODY>Unable to sent email due to wrong email id ('abc@idbiintech.com')  </BODY><HTML>",
					"smtp.idbiintech.com", "aml", "password");

		} catch (SendFailedException e) {

			throw e;
		} catch (AuthenticationFailedException e) {

			throw e;
		} catch (MessagingException e) {
			throw e;
		}
	}
}