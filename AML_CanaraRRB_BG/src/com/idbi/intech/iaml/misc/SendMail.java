// iAML Source Code, Copyright IDBI Intech Ltd.                                          
//                                                                          
// iAML Source Code is proprietary and confidential information       
// of IDBI Intech Ltd. Copying, reproduction or             
// distribution is strictly prohibited without the express written           
// consent of IDBI Intech Ltd.

package com.idbi.intech.iaml.misc;

import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMail {
	public static String smtpServer = "";
	public static String userName = "";
	public static String password;

	/**
	 * Send email
	 * 
	 * @param toList
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param mailText
	 * @param smtpServer
	 * @param userName
	 * @param password
	 * @return
	 * @throws AuthenticationFailedException
	 * @throws SendFailedException
	 * @throws MessagingException
	 */
	public static synchronized boolean sendEmailNew(ArrayList<String> toList,
			ArrayList<String> cc, ArrayList<String> bcc, String subject,
			String mailText, String smtpServer, String userName,
			String password, String file, String path)
			throws AuthenticationFailedException, SendFailedException,
			MessagingException {

		// String from="";
		if ((toList == null) && (subject == null)) {
			return false;
		}
		if (mailText == null) {
			mailText = "";
		}

		Properties props = new Properties();
		// props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth.plain.disable", true);
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.port", "25");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.auth.mechanism", "NTLM");
		props.put("mail.smtp.auth.domain", "WINDOMAIN");

		final String user = userName;
		final String pass = password;

		Session session = Session.getDefaultInstance(props, null);

		session = Session.getInstance(props, new Authenticator() {
			@SuppressWarnings("unused")
			protected PasswordAuthentication gePasswordAuthentication() {
				return new PasswordAuthentication(user, pass);
			}
		});

		session.setDebug(true);

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
		MimeMultipart mp = new MimeMultipart("related");
		MimeBodyPart text = new MimeBodyPart();
		text.setDisposition(Part.INLINE);
		text.setContent(mailText, "text/html");
		mp.addBodyPart(text);

		if (!file.equals("") && !path.equals("")) {
			text = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			text.setDataHandler(new DataHandler(source));
			text.setFileName(file);
			mp.addBodyPart(text);
		}

		// Adding Image
		/*
		 * text = new MimeBodyPart(); DataSource fds = new FileDataSource
		 * ("C:\\AML\\IMAGE\\client.png"); text.setDataHandler(new
		 * DataHandler(fds)); text.setHeader("Content-ID","<iamllogo>");
		 * 
		 * mp.addBodyPart(text);
		 * 
		 * text = new MimeBodyPart(); fds = new FileDataSource
		 * ("C:\\AML\\IMAGE\\IDBI_Bank_Logo.gif"); text.setDataHandler(new
		 * DataHandler(fds)); text.setHeader("Content-ID","<fbllogo>");
		 * 
		 * mp.addBodyPart(text);
		 */

		message.setContent(mp);

		// SMTP authentication
		Transport transport = session.getTransport("smtp");
		transport.connect(smtpServer, user, pass);
		message.saveChanges();
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		return true;
	}

	public static synchronized boolean sendEmailKYC(ArrayList<String> toList,
			ArrayList<String> cc, ArrayList<String> bcc, String subject,
			String mailText, String smtpServer, String userName, String password)
			throws AuthenticationFailedException, SendFailedException,
			MessagingException {

		// String from="";
		if ((toList == null) && (subject == null)) {
			return false;
		}
		if (mailText == null) {
			mailText = "";
		}

		Properties props = new Properties();
		// props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth.plain.disable", true);
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.port", "25");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.auth.mechanism", "NTLM");
		props.put("mail.smtp.auth.domain", "WINDOMAIN");

		final String user = userName;
		final String pass = password;

		Session session = Session.getDefaultInstance(props, null);

		session = Session.getInstance(props, new Authenticator() {
			@SuppressWarnings("unused")
			protected PasswordAuthentication gePasswordAuthentication() {
				return new PasswordAuthentication(user, pass);
			}
		});

		//session.setDebug(true);

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
		MimeMultipart mp = new MimeMultipart("related");
		MimeBodyPart text = new MimeBodyPart();
		text.setDisposition(Part.INLINE);
		text.setContent(mailText, "text/html");
		mp.addBodyPart(text);

		// Adding Image

		// text = new MimeBodyPart();
		// DataSource fds = new FileDataSource("F:\\AML\\IMAGE\\client.png");
		// text.setDataHandler(new DataHandler(fds));
		// text.setHeader("Content-ID", "<iamllogo>");
		//
		// mp.addBodyPart(text);
		//
		// text = new MimeBodyPart();
		// fds = new FileDataSource("F:\\AML\\IMAGE\\IDBI_Bank_Logo.jpg");
		// text.setDataHandler(new DataHandler(fds));
		// text.setHeader("Content-ID", "<fbllogo>");
		//
		// mp.addBodyPart(text);
		//
		message.setContent(mp);

		// SMTP authentication
		Transport transport = session.getTransport("smtp");
		transport.connect(smtpServer, user, pass);
		message.saveChanges();
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		return true;
	}
}// End of Class