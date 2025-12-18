package com.renato.projects.trello.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renato.projects.trello.controller.dto.confirmemail.ConfirmDTO;
import com.renato.projects.trello.controller.dto.confirmemail.ResendDTO;
import com.renato.projects.trello.domain.User;
import com.renato.projects.trello.repository.UserRepository;
import com.renato.projects.trello.service.email.EmailData;
import com.renato.projects.trello.service.email.EmailService;
import com.renato.projects.trello.service.email.context.ConfirmEmailContext;
import com.renato.projects.trello.service.email.template.IEmailTemplate;

@RestController
@RequestMapping("/email")
public class ConfirmEmailController {

	private final UserRepository userRepository;
	private final EmailService emailService;
	private final IEmailTemplate<ConfirmEmailContext> emailTemplate;

	public ConfirmEmailController(UserRepository userRepository, EmailService emailService,
			IEmailTemplate<ConfirmEmailContext> emailTemplate) {
		super();
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.emailTemplate = emailTemplate;
	}

	@PostMapping("/verify")
	public ResponseEntity<?> confirmEmail(@RequestBody ConfirmDTO confirmDTO) {
		Optional<User> optionalUser = userRepository.findByVerificationToken(confirmDTO.token());
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token inválido");
		}

		User user = optionalUser.get();

		if (user.getTokenExpiry().isBefore(Instant.now())) {
			return ResponseEntity.status(HttpStatus.GONE).body("Token expirado");
		}

		user.setVerified(true);
		user.setVerificationToken(null);
		user.setTokenExpiry(null);
		userRepository.save(user);

		return ResponseEntity.ok("Email confirmado com sucesso!");
	}

	@PostMapping("/resend")
	public ResponseEntity<?> resendLink(@RequestBody ResendDTO resendDTO) {
		Optional<User> optionalUser = userRepository.findByEmail(resendDTO.email());

		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		User user = optionalUser.get();

		String token = UUID.randomUUID().toString();
		user.setVerificationToken(token);
		user.setTokenExpiry(Instant.now().plus(1, ChronoUnit.HOURS));
		userRepository.save(user);

		// enviar email{
		ConfirmEmailContext confirmEmailContext = new ConfirmEmailContext(token, user.getEmail(), "Sem nome");
		EmailData build = emailTemplate.build(confirmEmailContext);
		emailService.sendEmail(build);
		// }

		return ResponseEntity.status(HttpStatus.OK).build();
	}
}