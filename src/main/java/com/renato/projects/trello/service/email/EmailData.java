package com.renato.projects.trello.service.email;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EmailData {

	private String fromEmail;
    private String fromName;
    private String toEmail;
    private String toNome;
    private String assunto;
    private String textoSimples;
    private String htmlConteudo;
    
	public EmailData(String fromEmail, String fromName, String toEmail, String toNome, String assunto,
			String textoSimples, String htmlConteudo) {
		super();
		this.fromEmail = fromEmail.replaceAll("\\s+", "").toLowerCase();
		this.fromName = fromName;
		this.toEmail = toEmail;
		this.toNome = toNome;
		this.assunto = assunto;
		this.textoSimples = textoSimples;
		this.htmlConteudo = htmlConteudo;
	}
}