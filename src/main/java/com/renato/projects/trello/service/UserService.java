package com.renato.projects.trello.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.renato.projects.trello.domain.User;
import com.renato.projects.trello.repository.UserRepository;
import com.renato.projects.trello.service.email.EmailData;
import com.renato.projects.trello.service.email.EmailService;
import com.renato.projects.trello.service.email.context.ConfirmEmailContext;
import com.renato.projects.trello.service.email.template.IEmailTemplate;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder encoder;
	private final IEmailTemplate<ConfirmEmailContext> emailTemplate;
	private final EmailService emailService;

	public UserService(UserRepository userRepository, PasswordEncoder encoder, IEmailTemplate<ConfirmEmailContext> emailTemplate, EmailService emailService) {
		super();
		this.encoder = encoder;
		this.userRepository = userRepository;
		this.emailTemplate = emailTemplate;
		this.emailService = emailService;
	}

	public User save(User user) {
		Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
		if(optionalUser.isPresent()) {
			throw new ResponseStatusException(
	                HttpStatus.CONFLICT,
	                "Email already exists: " + user.getEmail()
	        );
		}
		
		//salvar{
		user.setPassword(encoder.encode(user.getPassword()));
		user.setVerified(false);
		String token = UUID.randomUUID().toString();
		user.setVerificationToken(token);
		user.setTokenExpiry(Instant.now().plus(1, ChronoUnit.HOURS));
		userRepository.save(user);
		//}
		//enviar email{
		ConfirmEmailContext confirmEmailContext = new ConfirmEmailContext(token, user.getEmail(), "Sem nome");
		EmailData build = emailTemplate.build(confirmEmailContext);
		emailService.sendEmail(build);
		//}
		return user;
	}
}
