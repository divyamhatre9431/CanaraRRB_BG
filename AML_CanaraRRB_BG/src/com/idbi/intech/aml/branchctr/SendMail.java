package com.idbi.intech.aml.branchctr;

import java.util.ArrayList;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;

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
	public static String password = "";

	public static synchronized boolean sendEmailNew(ArrayList<String> toList, ArrayList<String> cc, ArrayList<String> bcc, String subject,
			String mailText, String smtpServer, String userName, String password,String filenamearr,String filepatharr)
			{
                 try{           
		// String from="";
		if ((toList == null) && (subject == null)) {
			return false;
		}
		if (mailText == null) {
			mailText = "";
		}
		mailText = mailText + "<br><br><br><font size='3'><p>This is System generated mail.Please do not reply on this.For any query please contact INSP team </p></font>";
		Properties props = System.getProperties();
		//props.setProperty("mail.smtp.host", smtpServer);
                //props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.host", smtpServer);
                props.put("mail.smtp.auth", "true");
                //props.put("mail.debug","true");
              
		Session session = Session.getDefaultInstance(props);
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
                BodyPart text=new MimeBodyPart();
                text.setContent(mailText, "text/html");
		MimeMultipart mp = new MimeMultipart();
		mp.addBodyPart(text);
                if(!filenamearr.equals("")||!filepatharr.equals("")){
                        text=new MimeBodyPart();
                        DataSource source=new FileDataSource(filepatharr);
                        text.setDataHandler(new DataHandler(source));
                        text.setFileName(filenamearr);
                        mp.addBodyPart(text);
                }
		message.setContent(mp);

		// SMTP authentication
		Transport transport = session.getTransport("smtp");
		//System.out.println(userName + ":" + password );
		transport.connect(smtpServer,userName, password);
		message.saveChanges();
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
                //Transport.send(message);
		return true;
           }catch(Exception ex){
                  ex.printStackTrace();
               System.out.println(ex.getMessage());
            }
           return false;      
	}
        
}
