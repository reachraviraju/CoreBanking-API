package com.corebanking.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		String token = extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
		if (token == null) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String email = jwtService.extractEmail(token);
			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				// Disabled users should never get authenticated even with a previously issued token.
				if (!userDetails.isEnabled()) {
					writeForbidden(response, "User account is disabled");
					return;
				}
				// Fail fast here instead of letting invalid tokens reach protected endpoints.
				if (!jwtService.isTokenValid(token, userDetails)) {
					writeUnauthorized(response, "Invalid or expired token");
					return;
				}
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (UsernameNotFoundException ex) {
			writeUnauthorized(response, "Invalid or expired token");
			return;
		} catch (JwtException | IllegalArgumentException ex) {
			writeUnauthorized(response, "Invalid or expired token");
			return;
		}

		filterChain.doFilter(request, response);
	}

	private static String extractBearerToken(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return null;
		}
		String token = authHeader.substring(7).trim();
		return token.isEmpty() ? null : token;
	}

	private static void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
		writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, message);
	}

	private static void writeForbidden(HttpServletResponse response, String message) throws IOException {
		writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, message);
	}

	private static void writeJsonError(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		String escaped = message.replace("\"", "\\\"");
		response.getWriter().write("{\"message\":\"" + escaped + "\"}");
	}
}
