package com.example.localstack.usecase.sqs.ses.notification;

import lombok.Builder;

/**
 * A Java record representing a notification to be sent.
 * Records are a concise way to declare immutable data classes in Java 16+.
 */
@Builder
public record Notification(
        String address, // The recipient's email address
        String subject, // The subject of the email
        String body     // The body content of the email
) {

}