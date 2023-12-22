package com.bortolo.softwarequalityclass.service;

import com.bortolo.softwarequalityclass.exception.MessageNotFoundException;
import com.bortolo.softwarequalityclass.model.Message;
import com.bortolo.softwarequalityclass.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {


    private final MessageRepository messageRepository;

    @Override
    public Message registerMessage(Message message) {
        message.setId(UUID.randomUUID());
        return messageRepository.save(message);
    }

    @Override
    public Message findMessage(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Mensagem nao encontrada"));
    }

    @Override
    public Message updateMessage(UUID oldId, Message newMessage) {

        Message message = findMessage(oldId);

        if (!message.getId().equals(newMessage.getId()))
            throw new MessageNotFoundException("Mensagem sem Id correto");



        return messageRepository.save(newMessage);
    }

    @Override
    public boolean deleteMessage(UUID messageId) {

        findMessage(messageId);
        messageRepository.deleteById(messageId);

        return true;
    }

    @Override
    public Page<Message> findAllMessage(Pageable pageable) {
        return messageRepository.listMessages(pageable);
    }
}
