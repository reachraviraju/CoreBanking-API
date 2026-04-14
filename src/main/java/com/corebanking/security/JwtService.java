package com.corebanking.security;

import com.corebanking.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

	private static final String CLAIM_USER_ID = "uid";

	private final JwtProperties jwtProperties;

	public String generateToken(User user) {
		Date issuedAt = new Date();
		Date expiresAt = new Date(issuedAt.getTime() + jwtProperties.expirationMs());
		return Jwts.builder()
				.subject(user.getEmail())
				.claim(CLAIM_USER_ID, user.getId())
				.issuedAt(issuedAt)
				.expiration(expiresAt)
				.signWith(signingKey())
				.compact();
	}

	public String extractEmail(String token) {
		return parseClaims(token).getSubject();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String email = extractEmail(token);
		return email.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return parseClaims(token).getExpiration().before(new Date());
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey signingKey() {
		byte[] bytes = jwtProperties.secret().getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			throw new IllegalStateException("security.jwt.secret must be at least 32 bytes for HS256");
		}
		return Keys.hmacShaKeyFor(bytes);
	}
}
