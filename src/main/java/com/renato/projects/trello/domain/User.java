package com.renato.projects.trello.domain;

import java.time.Instant;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	@Column(unique = true, nullable = false)
	private String email;
	private String password;
	private String name;
	//Email{
	@Column(nullable = false)
	private boolean verified = false;
	@Column(unique = true)
	private String verificationToken;
	@Column
	private Instant tokenExpiry;
	//}fim email
	//senha{
	@Column(unique = true)
    private String passwordResetToken;
    @Column
    private Instant passwordResetTokenExpiry;
    //}fim senha

	public User(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
}