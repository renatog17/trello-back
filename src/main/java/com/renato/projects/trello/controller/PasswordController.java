package com.renato.projects.trello.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renato.projects.trello.controller.dto.password.ChangePasswordDTO;
import com.renato.projects.trello.controller.dto.password.RequestPasswordResetDTO;
import com.renato.projects.trello.domain.User;
import com.renato.projects.trello.repository.UserRepository;
import com.renato.projects.trello.service.email.EmailData;
import com.renato.projects.trello.service.email.EmailService;
import com.renato.projects.trello.service.email.context.ResetPasswordContext;
import com.renato.projects.trello.service.email.template.IEmailTemplate;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/password")
public class PasswordController {

	private final UserRepository userRepository;
	private final IEmailTemplate<ResetPasswordContext> emailTemplate;
	private final EmailService emailService;
	private final PasswordEncoder encoder;

	public PasswordController(UserRepository userRepository, IEmailTemplate<ResetPasswordContext> emailTemplate,
			EmailService emailService, PasswordEncoder encoder) {
		this.userRepository = userRepository;
		this.emailTemplate = emailTemplate;
		this.emailService = emailService;
		this.encoder = encoder;
	}

	@PostMapping("/forget")
	public ResponseEntity<?> requestPasswordReset(@RequestBody RequestPasswordResetDTO request) {
		Optional<User> optionalUser = userRepository.findByEmail(request.email());

		if (optionalUser.isEmpty()) {
			// sucesso fake, para não expor se o email existe ou não
			return ResponseEntity.ok().build();
		}

		User user = optionalUser.get();

		String token = UUID.randomUUID().toString();
		user.setPasswordResetToken(token);
		user.setPasswordResetTokenExpiry(Instant.now().plus(15, ChronoUnit.MINUTES));

		userRepository.save(user);

		ResetPasswordContext resetPasswordContext = new ResetPasswordContext(token, user.getEmail(), "Sem nome");
		EmailData build = emailTemplate.build(resetPasswordContext);
		emailService.sendEmail(build);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/reset")
	@Transactional
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO dto) {

		Optional<User> optionalUser = userRepository.findByEmail(dto.email());
		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body("Invalid request");
		}

		User user = optionalUser.get();

		if (user.getPasswordResetToken() == null || user.getPasswordResetTokenExpiry() == null
				|| user.getPasswordResetTokenExpiry().isBefore(Instant.now())) {

			return ResponseEntity.badRequest().body("Invalid or expired token");
		}

		if (!user.getPasswordResetToken().equals(dto.token())) {
			return ResponseEntity.badRequest().body("Invalid or expired token");
		}

		user.setPassword(encoder.encode(dto.password()));

		user.setPasswordResetToken(null);
		user.setPasswordResetTokenExpiry(null);

		return ResponseEntity.ok().body("Password changed successfully");
	}
}