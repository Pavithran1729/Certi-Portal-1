package com.erp.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendAttendanceCertificateEmail(String to, String fullName, Long id) throws MessagingException;

    void sendAttendanceRejectionEmail(String to, String fullName, Long id) throws MessagingException;

    void sendBonafideApprovalEmail(String to, String fullName, Long id) throws MessagingException;

    void sendBonafideRejectionEmail(String to, String fullName, Long id) throws MessagingException;

    void sendTCCreationEmail(String to, String firstName, String lastName, Long id) throws MessagingException;

    void sendLibraryDuesEmail(String to, String firstName, String lastName) throws MessagingException;

    void sendAccountDuesEmail(String to, String firstName, String lastName) throws MessagingException;
}
