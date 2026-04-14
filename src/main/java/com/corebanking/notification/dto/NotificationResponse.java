package com.corebanking.notification.dto;

import com.corebanking.notification.entity.NotificationChannel;

import java.time.Instant;

public record NotificationResponse(
		Long id,
		NotificationChannel channel,
		String title,
		String message,
		boolean read,
		Instant createdAt
) {
}
