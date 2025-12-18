package com.renato.projects.trello.service.email;

import org.springframework.stereotype.Service;


@Service
public class EmailService {

	public void sendEmail(EmailData emailData){
		System.out.println("ENVIANDO \n"+emailData.toString());
	}
	
}
