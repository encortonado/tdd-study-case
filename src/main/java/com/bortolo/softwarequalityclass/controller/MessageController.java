package com.bortolo.softwarequalityclass.controller;


import com.bortolo.softwarequalityclass.exception.MessageNotFoundException;
import com.bortolo.softwarequalityclass.model.Message;
import com.bortolo.softwarequalityclass.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;


    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> registerMessage(@RequestBody Message message) {

        Message msgCreated = messageService.registerMessage(message);

        return new ResponseEntity<>(msgCreated, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> findMessage(@PathVariable String id) {
        try {
            Message msgFound = messageService.findMessage(UUID.fromString(id));
            return new ResponseEntity<>(msgFound, HttpStatus.OK);
        } catch (MessageNotFoundException ex) {
            return new ResponseEntity<>("Id Invalido", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(
            value = ""
    )
    public ResponseEntity<Page<Message>> listMesages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> mensagens = messageService.findAllMessage(pageable);
        return ResponseEntity.ok(mensagens);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateMessage(@PathVariable String id, @RequestBody Message msg) {

        try {
            Message message = messageService.updateMessage(UUID.fromString(id), msg);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (MessageNotFoundException ex) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteMessage(@PathVariable String id) {
        try {
            messageService.deleteMessage(UUID.fromString(id));
            return new ResponseEntity<>("Menssagem Removida com Sucesso", HttpStatus.OK);
        } catch (MessageNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }

    }

}
