package com.corebanking.notification.service;

import com.corebanking.common.exception.ResourceNotFoundException;
import com.corebanking.security.AccessGuard;
import com.corebanking.notification.dto.NotificationResponse;
import com.corebanking.notification.dto.UnreadCountResponse;
import com.corebanking.notification.entity.Notification;
import com.corebanking.notification.entity.NotificationChannel;
import com.corebanking.notification.repository.NotificationRepository;
import com.corebanking.user.entity.User;
import com.corebanking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final AccessGuard accessGuard;

	@Transactional
	public void createInApp(User user, String title, String message) {
		Notification n = new Notification();
		n.setUser(user);
		n.setChannel(NotificationChannel.IN_APP);
		n.setTitle(title);
		n.setMessage(message);
		n.setReadFlag(false);
		notificationRepository.save(n);
	}

	@Transactional(readOnly = true)
	public List<NotificationResponse> listForUser(Long userId) {
		accessGuard.requireSameUser(userId);
		if (!userRepository.existsById(userId)) {
			throw new ResourceNotFoundException("User not found with id: " + userId);
		}
		return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
				.map(NotificationService::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public UnreadCountResponse unreadCount(Long userId) {
		accessGuard.requireSameUser(userId);
		if (!userRepository.existsById(userId)) {
			throw new ResourceNotFoundException("User not found with id: " + userId);
		}
		return new UnreadCountResponse(notificationRepository.countByUser_IdAndReadFlagFalse(userId));
	}

	@Transactional
	public NotificationResponse markRead(Long userId, Long notificationId) {
		accessGuard.requireSameUser(userId);
		Notification n = notificationRepository.findByIdAndUser_Id(notificationId, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Notification not found for user"));
		n.setReadFlag(true);
		return toResponse(n);
	}

	private static NotificationResponse toResponse(Notification n) {
		return new NotificationResponse(
				n.getId(),
				n.getChannel(),
				n.getTitle(),
				n.getMessage(),
				n.isReadFlag(),
				n.getCreatedAt()
		);
	}
}
