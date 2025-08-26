package com.icsd.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.icsd.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	
	@Autowired
 	JavaMailSender mailSender;
	
	@Override
	public void sendEmail(String to, String subject, String body) {
	    try {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(to);
	        message.setSubject(subject);
	        message.setText(body);
	        mailSender.send(message);
	        log.info("Email sent to: " + to);
	    } catch (Exception e) {
	        log.info("Failed to send email: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

}
