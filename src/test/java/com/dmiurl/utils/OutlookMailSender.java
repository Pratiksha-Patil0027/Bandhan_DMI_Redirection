package  com.dmiurl.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.List;
import java.util.Properties;

public class OutlookMailSender {

    // ✅ Single email with multiple attachments
    public static void sendOutlookMailWithAttachments(String toEmail, String subject, String body, List<String> filePaths) {
        String outlookSenderMail = Credentials.getOutlookSenderMail();
        String outlookPassword = Credentials.getOutlookPassword();

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.office365.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(outlookSenderMail, outlookPassword);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(outlookSenderMail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(subject);

            // Email body
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(body, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // ✅ Add multiple attachments
            if (filePaths != null) {
                for (String filePath : filePaths) {
                    if (filePath != null && !filePath.trim().isEmpty()) {
                        MimeBodyPart attachmentPart = new MimeBodyPart();
                        attachmentPart.attachFile(new File(filePath));
                        multipart.addBodyPart(attachmentPart);
                    }
                }
            }

            msg.setContent(multipart);
            Transport.send(msg);

            System.out.println("Email sent to " + toEmail + " with " + filePaths.size() + " attachments.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
