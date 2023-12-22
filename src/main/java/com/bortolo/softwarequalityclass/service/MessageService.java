package com.bortolo.softwarequalityclass.service;

import com.bortolo.softwarequalityclass.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


/**
 * TBD.
 */
public interface MessageService {

    Message registerMessage(Message message);

    Message findMessage(UUID messageId);

    Message updateMessage(UUID oldId, Message newMessage);

    boolean deleteMessage(UUID messageId);

    Page<Message> findAllMessage(Pageable pageable);

}
