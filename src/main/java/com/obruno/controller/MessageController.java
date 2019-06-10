package com.obruno.controller;

import com.obruno.model.Message;
import com.obruno.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping(value = "/message")
    public void produce() {
        Message message = Message.builder().id(1L).text("Pohaaaa").build();
        messageService.produce(message);
    }

}
