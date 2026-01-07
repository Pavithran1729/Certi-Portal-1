package com.erp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.erp.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String SENDER_EMAIL = "office.stpeters@gmail.com";
    private static final String COLLEGE_NAME = "St. Peter's College of Engineering and Technology";
    private static final String OFFICE_LOCATION = "Office No. 3";

    @Override
    public void sendAttendanceCertificateEmail(String to, String fullName, Long id) throws MessagingException {
        String subject = "Notification: Attendance Certificate Approved";
        String body = buildEmailBody(fullName,
                "We are pleased to inform you that your request for an Attendance Certificate has been approved.",
                "Application ID: AC2025" + id,
                "Please visit <b>" + OFFICE_LOCATION + "</b> at <b>" + COLLEGE_NAME
                        + "</b> at your earliest convenience to collect it.",
                "For verification, kindly bring your Student ID card while collecting the certificate.");
        sendHtmlEmail(to, subject, body);
    }

    @Override
    public void sendAttendanceRejectionEmail(String to, String fullName, Long id) throws MessagingException {
        String subject = "Notification: Attendance Certificate Request Rejected";
        String body = buildEmailBody(fullName,
                "We regret to inform you that your request for an <b>Attendance Certificate</b> has been <b>rejected</b> due to some issues.",
                "Application ID: AC2025" + id,
                "There seems to be some incorrect information or missing details in your application.",
                "To resolve this, please visit <b>" + OFFICE_LOCATION + "</b> at <b>" + COLLEGE_NAME
                        + "</b> as soon as possible. Our administrative staff will guide you through the necessary corrections.");
        sendHtmlEmail(to, subject, body);
    }

    @Override
    public void sendBonafideApprovalEmail(String to, String fullName, Long id) throws MessagingException {
        String subject = "Notification: Bonafide Certificate Approved";
        String body = buildEmailBody(fullName,
                "We are pleased to inform you that your Bonafide Certificate request has been approved.",
                "Application ID (Bonafide ID): BC2025" + id,
                "Please visit <b>" + OFFICE_LOCATION + "</b> at <b>" + COLLEGE_NAME
                        + "</b> at your earliest convenience to collect it.",
                "For a smooth collection process, kindly ensure you bring your Student ID card as proof of identity.");
        sendHtmlEmail(to, subject, body);
    }

    @Override
    public void sendBonafideRejectionEmail(String to, String fullName, Long id) throws MessagingException {
        String subject = "Notification: Bonafide Certificate Request Rejected";
        String body = buildEmailBody(fullName,
                "We regret to inform you that your request for a Bonafide Certificate has been <b>rejected</b> due to some issues.",
                "Application ID: BC2025" + id,
                "There seems to be some incorrect information or missing details in your application.",
                "To resolve this, please visit <b>" + OFFICE_LOCATION + "</b> at <b>" + COLLEGE_NAME
                        + "</b> as soon as possible. Our administrative staff will guide you through the necessary corrections.");
        sendHtmlEmail(to, subject, body);
    }

    @Override
    public void sendTCCreationEmail(String to, String firstName, String lastName, Long id) throws MessagingException {
        String fullName = firstName + " " + lastName;
        String subject = "Notification: Transference Certificate Created Successfully";
        String body = buildEmailBody(fullName,
                "We are pleased to inform you that your Transfer Certificate (TC) has been created successfully.",
                "Application ID (TC ID): 2025" + id,
                "Please visit <b>" + OFFICE_LOCATION + "</b> at <b>" + COLLEGE_NAME
                        + "</b> at your earliest convenience to collect it.",
                "For a smooth collection process, kindly ensure you bring either your ID card or your admission receipt as proof of identity.");
        sendHtmlEmail(to, subject, body);
    }

    @Override
    public void sendLibraryDuesEmail(String to, String firstName, String lastName) throws MessagingException {
        String fullName = firstName + " " + lastName;
        String subject = "Important Notice Regarding Library Dues";
        String body = buildEmailBodyWithSignature(fullName, "library.stpeters@gmail.com",
                "Our records indicate that there are pending library dues associated with your account.",
                "Please note that clearing these dues is essential for any future processing of your <b>Transfer Certificate (TC)</b>.",
                "If you have already settled your dues, kindly provide the payment receipt to our office for verification.",
                "Otherwise, we request you to visit the <b>library office</b> at your earliest convenience to resolve the matter.",
                "Thank you for your prompt attention to this issue.");
        sendHtmlEmail(to, subject, body);
    }

    @Override
    public void sendAccountDuesEmail(String to, String firstName, String lastName) throws MessagingException {
        String fullName = firstName + " " + lastName;
        String subject = "You Have Outstanding Account Dues";
        String body = buildEmailBodyWithSignature(fullName, "account.stpeters@gmail.com",
                "We hope you are doing well.",
                "Our records indicate that you have <b>outstanding account dues</b> that need to be cleared.",
                "Please note that these dues must be settled before we can proceed with any further processing, including your <b>Transfer Certificate (TC)</b>.",
                "If you have already made the payment, kindly forward your payment confirmation to the <b>Accounts Office</b> for verification.",
                "For any questions or further assistance, please contact our <b>accounts department</b>.",
                "Thank you for your prompt attention to this matter.");
        sendHtmlEmail(to, subject, body);
    }

    private void sendHtmlEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(message);
    }

    private String buildEmailBody(String name, String... paragraphs) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h3>Dear ").append(name).append(",</h3>");
        for (String p : paragraphs) {
            sb.append("<p>").append(p).append("</p>");
        }
        sb.append(
                "<p>If you have any questions or need further assistance, please feel free to contact our administrative office.</p>");
        sb.append("<p>Thank you for your cooperation.</p>");
        sb.append("<br>");
        sb.append("<p><b>Best regards,</b><br>");
        sb.append(COLLEGE_NAME).append("<br>");
        sb.append("<i>").append(SENDER_EMAIL).append("</i></p>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String buildEmailBodyWithSignature(String name, String singerEmail, String... paragraphs) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h3>Dear ").append(name).append(",</h3>");
        for (String p : paragraphs) {
            sb.append("<p>").append(p).append("</p>");
        }
        sb.append("<br>");
        sb.append("<p><b>Best regards,</b><br>");
        sb.append(COLLEGE_NAME).append("<br>");
        sb.append("<i>").append(singerEmail).append("</i></p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
