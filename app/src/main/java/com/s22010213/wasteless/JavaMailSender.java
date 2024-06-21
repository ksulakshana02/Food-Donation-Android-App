package com.s22010213.wasteless;


import android.util.Log;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailSender {

    private static final String SMTP_SERVER = "email-smtp.us-east-1.amazonaws.com";
    private static final String USERNAME = "AKIA6ODU6YXKJ66YIL4U";
    private static final String PASSWORD = "BNnmowuqMoByau0KVp3UC/CTubUMDU5vLilG0e8nMIcj";
    private static final String SENDER_EMAIL = "kaveeshasulakshana@gmail.com";


    public interface EmailSentCallback {
        void onEmailSent();
        void onEmailFailed(Exception e);
    }

    public static void sendOtpEmail(String recipientEmail, String otp, EmailSentCallback callback){
        Properties prop = new Properties();
        prop.put("mail.smtp.host", SMTP_SERVER);
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.trust",SMTP_SERVER);
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME,PASSWORD);
            }
        });

        new Thread(() -> {
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL));
                message.addRecipient(
                        Message.RecipientType.TO,
                        new InternetAddress(recipientEmail)
                );
                message.setSubject("WasteLess OTP Code");
                String msg = "Your WasteLess OTP code is: " + otp;
                message.setText(msg);

                Transport.send(message);

                Log.d("otpMail", "OTP email sent successfully.");

                if (callback != null) {
                    callback.onEmailSent();
                }

            } catch (MessagingException e) {
                Log.e("otpMail", "Failed to send OTP email", e);
                if (callback != null) {
                    callback.onEmailFailed(e);
                }
            }
        }).start();
    }
}
