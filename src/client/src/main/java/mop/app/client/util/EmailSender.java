package mop.app.client.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.slf4j.LoggerFactory;

public class EmailSender {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EmailSender.class);
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";

    static {
        Logger.getLogger("jakarta.mail").setLevel(Level.SEVERE);
    }

    public static void sendEmailAsync(String to, String subject, String body) {
        String senderEmail = "namisme16052004@gmail.com";
        String senderPassword = "oktc ovts lheg pprj";
        Task<Void> emailTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                sendEmail(to, subject, body, senderEmail, senderPassword);
                return null;
            }
        };

        emailTask.setOnRunning(e -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sending Email");
                alert.setHeaderText(null);
                alert.setContentText("Sending email...");
                alert.show();
            });
        });

        emailTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Email sent successfully!");
                alert.show();
            });
        });

        emailTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to send email: " +
                    emailTask.getException().getMessage());
                alert.show();
            });
        });

        Thread emailThread = new Thread(emailTask);
        emailThread.setDaemon(true);
        emailThread.start();
    }

    private static void sendEmail(String to, String subject, String body,
                                  String senderEmail, String senderPassword)
        throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.secure", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        session.setDebug(false);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO,
            InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }
}
