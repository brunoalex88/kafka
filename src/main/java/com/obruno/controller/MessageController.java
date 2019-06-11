package com.obruno.controller;

import com.obruno.model.Message;
import com.obruno.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(value = "/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public void produce(Message message) {
        messageService.produce(message);
    }

    @GetMapping
    public Message consume() {
        return messageService.consume();
    }

}
