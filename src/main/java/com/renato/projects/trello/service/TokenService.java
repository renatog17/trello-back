package com.renato.projects.trello.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class TokenService {

	private static final String SECRET = "abc123";
	private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

	public String generateToken(UserDetails user) {
		return JWT.create().withSubject(user.getUsername()).withIssuer("api").withIssuedAt(new Date())
				.withExpiresAt(Date.from(Instant.now().plus(24, ChronoUnit.HOURS))).sign(ALGORITHM);
	}

	public String getSubject(String token) {
		JWTVerifier verifier = JWT.require(ALGORITHM).withIssuer("api").build();

		return verifier.verify(token).getSubject();
	}

}