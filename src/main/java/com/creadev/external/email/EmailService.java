package com.creadev.external.email;

public interface EmailService {
    /**
     * Send an email.
     *
     * @param from    the sender address
     * @param to      the recipient address
     * @param subject the email subject
     * @param body    the email body content
     */
    void sendEmail(String from, String to, String subject, String body);
} 