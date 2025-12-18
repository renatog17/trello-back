package com.renato.projects.trello.controller;

import java.util.Map;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renato.projects.trello.controller.dto.auth.LoginRequest;
import com.renato.projects.trello.service.TokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authManager, TokenService tokenService) {
        this.authManager = authManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    	System.out.println(request.toString());
        var authToken = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        );
        Authentication authentication = authManager.authenticate(authToken);

        UserDetails user = (UserDetails) authentication.getPrincipal();

        String token = tokenService.generateToken(user);
        
        ResponseCookie cookie = ResponseCookie
        		.from("session", token)
                .httpOnly(true)
                .secure(false) // colocar true se estiver usando HTTPS
                .path("/")
                .sameSite("Lax") // ou Lax, dependendo do seu fluxo
                .maxAge(86400) // 1 dia, opcional
                .build();


        return ResponseEntity
        			.ok()
        			.header("Set-Cookie", cookie.toString())
        			.build();
    }
    
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {

        String token = extractToken(request);
        
        if (token == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        try {
            String username = tokenService.getSubject(token);
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "user", username
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        ResponseCookie cookie = ResponseCookie
        		.from("session", "")
                .httpOnly(true)
                .secure(false) // true em produção
                .path("/")
                .sameSite("Lax")
                .maxAge(0) // remove
                .build();

        return ResponseEntity
                .ok()
                .header("Set-Cookie", cookie.toString())
                .body(Map.of("logout", "ok"));
    }

    
	private String extractToken(HttpServletRequest request) {

		// prioriza cookie (já que agora você está sendo utilizado HttpOnly)
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals("session")) {
					return cookie.getValue();
				}
			}
		}

		// fallback opcional: ainda aceita Bearer no header
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7);
		}

		return null;
	}
}