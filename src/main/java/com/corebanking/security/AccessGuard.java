package com.corebanking.security;

import com.corebanking.account.entity.Account;
import com.corebanking.account.repository.AccountRepository;
import com.corebanking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessGuard {

	private final AccountRepository accountRepository;

	public Long currentUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
			throw new AccessDeniedException("Not authenticated");
		}
		return principal.getUserId();
	}

	public void requireSameUser(Long userId) {
		if (!currentUserId().equals(userId)) {
			throw new AccessDeniedException("You cannot access another user's data");
		}
	}

	public void requireAccountOwner(Long accountId) {
		Long uid = currentUserId();
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
		if (!account.getUser().getId().equals(uid)) {
			throw new AccessDeniedException("You cannot access this account");
		}
	}
}
