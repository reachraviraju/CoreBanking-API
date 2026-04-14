package com.corebanking.security;

import com.corebanking.user.entity.User;
import com.corebanking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String email = username.trim().toLowerCase();
		return userRepository.findByEmail(email)
				.map(AccountUserDetailsService::toPrincipal)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	private static UserPrincipal toPrincipal(User user) {
		return new UserPrincipal(
				user.getId(),
				user.getEmail(),
				user.getPasswordHash(),
				user.isEnabled());
	}
}
