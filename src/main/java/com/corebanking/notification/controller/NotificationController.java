package com.corebanking.notification.controller;

import com.corebanking.notification.dto.NotificationResponse;
import com.corebanking.notification.dto.UnreadCountResponse;
import com.corebanking.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	public ResponseEntity<List<NotificationResponse>> list(@PathVariable Long userId) {
		return ResponseEntity.ok(notificationService.listForUser(userId));
	}

	@GetMapping("/unread-count")
	public ResponseEntity<UnreadCountResponse> unreadCount(@PathVariable Long userId) {
		return ResponseEntity.ok(notificationService.unreadCount(userId));
	}

	@PatchMapping("/{notificationId}/read")
	public ResponseEntity<NotificationResponse> markRead(
			@PathVariable Long userId,
			@PathVariable Long notificationId) {
		return ResponseEntity.ok(notificationService.markRead(userId, notificationId));
	}
}
