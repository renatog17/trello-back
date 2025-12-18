package com.renato.projects.trello.service.email.template;

import org.springframework.stereotype.Service;

import com.renato.projects.trello.service.email.EmailData;
import com.renato.projects.trello.service.email.context.ConfirmEmailContext;

@Service
public class ConfirmEmail implements IEmailTemplate<ConfirmEmailContext>{


	@Override
	public EmailData build(ConfirmEmailContext ctx) {
	    return new EmailData(
	        "noreply@site.com",
	        "Meu Sistema",
	        ctx.getEmail(),
	        ctx.getName(),
	        "Confirme seu e-mail",
	        "Olá " + ctx.getName() + ", use este código: " + ctx.getVerificationToken(),
	        "<h1>Olá " + ctx.getName() + "</h1><p>Seu código é: <b>" + ctx.getVerificationToken() + "</b></p>"
	    );
	}


}
