package com.example.localstack.usecase.sqs.ses.notification;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final ReceiveSendNotifications receiveSendNotifications;

    // Send emails for all parseable notifications
    @RequestMapping(value = "/process", method = RequestMethod.GET)
    @ResponseBody
    List<String> processNotifications(HttpServletRequest request, HttpServletResponse response) {
        return receiveSendNotifications.processNotifications();
    }


    //  Lists all message bodies
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    List<HashMap<String, String>> listMessages(HttpServletRequest request, HttpServletResponse response) {
        return receiveSendNotifications.listMessages();
    }


    //  Purge the message queue
    @RequestMapping(value = "/purge", method = RequestMethod.GET)
    @ResponseBody
    void purgeQueue(HttpServletRequest request, HttpServletResponse response) {
        receiveSendNotifications.purgeQueue();
    }

}