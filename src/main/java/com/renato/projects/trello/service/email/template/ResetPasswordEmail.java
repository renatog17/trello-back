package com.renato.projects.trello.service.email.template;

import org.springframework.stereotype.Service;

import com.renato.projects.trello.service.email.EmailData;
import com.renato.projects.trello.service.email.context.ResetPasswordContext;

@Service
public class ResetPasswordEmail implements IEmailTemplate<ResetPasswordContext>{

	@Override
	public EmailData build(ResetPasswordContext ctx) {
		return new EmailData(
		        "noreply@site.com",
		        "Meu Sistema",
		        ctx.getEmail(),
		        ctx.getName(),
		        "Reset de senha",
		        "Olá " + ctx.getName() + ", use este código: " + ctx.getVerificationToken(),
		        "<h1>Olá " + ctx.getName() + "</h1><p>Seu código é: <b>" + ctx.getVerificationToken() + "</b></p>"
		   );
	}

}
