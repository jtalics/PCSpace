package gov.nih.ncgc.openhts.tool1.util;


import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

/**
 * <p>Title: Cleaner Sign Supplies</p>
 * <p>Description: website</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: jtalics</p>
 * @author Joseph Talafous
 * @version 1.0
 */

public class Mailer {
  public void send(String from,String recipient,String subject,String text)
    throws MailerException {

    try {
    // Create a mail session
    Properties props = new java.util.Properties();
    props.put("mail.smtp.host","smtp.gmail.com");
    props.put("mail.smtp.auth","true");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.starttls.enable", "true");
    //props.put("mail.smtp.port", "465"); also works for google --- NOT 25 like everyone else

    Authenticator auth=new SMTPAuthenticator();


    javax.mail.Session session = javax.mail.Session.getDefaultInstance(props,auth);

    // Construct and send the message
    MimeMessage msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(from));
    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
    msg.setSubject(subject);
    msg.setText(text);
    Transport.send(msg);
    }
    catch(Throwable t) {
      throw new MailerException(t);
    }
  }
  static String vendor_email="cleansign@gmail.com";
  static String vendor_pw="jimt=627";

  private class SMTPAuthenticator extends Authenticator {
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(vendor_email,vendor_pw);
    }
  }
}
