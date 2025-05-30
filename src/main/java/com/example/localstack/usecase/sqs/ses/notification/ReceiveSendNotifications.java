package com.example.localstack.usecase.sqs.ses.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReceiveSendNotifications {

    @Value("${app.ses.source-email}")
    private final String sourceEmail;

    @Value("${app.ses.recipient-email}")
    private final String recipientEmail;
    private final SqsClient sqsClient;
    private final SesClient sesClient;
    private final String notificationQueueUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> processNotifications() {
        // receive messages from queue
        ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(
                request -> request.queueUrl(notificationQueueUrl).maxNumberOfMessages(10)
        );
        if (!receiveMessageResponse.hasMessages()) {
            return Collections.emptyList();
        }
        // transform notifications
        List<Message> messages = receiveMessageResponse.messages();
        List<Notification> notificationsToSend = new ArrayList<>(messages.size());
        List<String> notificationReceipts = new ArrayList<>(messages.size());
        for (Message message : messages) {
            log.info(message.toString());
            String body = message.body();
            try {
                // extract SNS event
                HashMap snsEvent = objectMapper.readValue(body, HashMap.class);
                log.info("processing snsEvent {}", body);
                // Notification is expected to be wrapped in the SNS message body
                String notificationString = snsEvent.get("Message").toString();
                //Notification notification = objectMapper.readValue(notificationString, Notification.class);

                notificationsToSend.add(Notification.builder()
                        .address(recipientEmail)
                        .body(snsEvent.get("Message").toString())
                        .subject(snsEvent.get("Subject").toString())
                        .build());
                notificationReceipts.add(message.receiptHandle());
            } catch (Exception e) {
                log.error("error processing message body {}", body, e);
            }
        }

        // send notifications transactional
        List<String> sentMessages = new ArrayList<>();
        for (int i = 0; i < notificationsToSend.size(); i++) {
            Notification notification = notificationsToSend.get(i);
            String receiptHandle = notificationReceipts.get(i);

            try {
                String messageId = sendNotificationAsEmail(notification);
                log.info("successfully sent notification as email, message id = {}", messageId);
                sentMessages.add(messageId);
            } catch (Exception e) {
                log.error("could not send notification as email {}", notification, e);
                continue;
            }

            sqsClient.deleteMessage(builder -> {
                builder.queueUrl(notificationQueueUrl).receiptHandle(receiptHandle);
            });
        }

        return sentMessages;
    }

    public String sendNotificationAsEmail(Notification notification) {
        return sesClient.sendEmail(notificationToEmail(notification)).messageId();
    }

    public SendEmailRequest notificationToEmail(Notification notification) {
        SendEmailRequest build = SendEmailRequest.builder().applyMutation(email -> {
            email.message(msg -> {
                msg.body(body -> {
                    body.text(text -> {
                        text.data(notification.body());
                    });
                }).subject(subject -> {
                    subject.data(notification.subject());
                });
            }).destination(dest -> {
                dest.toAddresses(notification.address());
            }).source(sourceEmail);
        }).build();
        log.info(build.toString());
        return build;
    }

    public List<HashMap<String, String>> listMessages() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(notificationQueueUrl)
                .visibilityTimeout(0)
                .maxNumberOfMessages(10)
                .build();

        ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveRequest);
        if (!receiveMessageResponse.hasMessages()) {
            return Collections.emptyList();
        }
        return receiveMessageResponse.messages()
                .stream()
                .map(Message::body)
                .map(str -> {
                    try {
                        return (HashMap<String, String>) objectMapper.readValue(str, HashMap.class);
                    } catch (JsonProcessingException e) {
                        log.error("error processing message body {}", str, e);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("body", str);
                        return map;
                    }
                }).collect(Collectors.toList());
    }

    public void purgeQueue() {
        sqsClient.purgeQueue(builder -> {
            builder.queueUrl(notificationQueueUrl);
        });
    }

}
