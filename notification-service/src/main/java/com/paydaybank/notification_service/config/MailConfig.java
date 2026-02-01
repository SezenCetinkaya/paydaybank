package com.paydaybank.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @org.springframework.beans.factory.annotation.Value("${spring.mail.host}")
    private String mailHost;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.port}")
    private int mailPort;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(mailHost);
        sender.setPort(mailPort);
        return sender;
    }
}
