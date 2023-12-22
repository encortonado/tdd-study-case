package com.bortolo.softwarequalityclass.repository;

import com.bortolo.softwarequalityclass.model.Message;
import com.bortolo.softwarequalityclass.utils.MessageHelper;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
 class MessageRepositoryIT {

    @Autowired
    private MessageRepository messageRepository;

    @Test
     void shouldCreateTable() {
        var totalRegisters = messageRepository.count();

        Assertions.assertThat(totalRegisters).isPositive();
    }

    @Test
     void shouldCreateMessage() {

        var msg = MessageHelper.messageBuilderWithUUID();

        var msgStored = messageRepository.save(msg);

        Assertions.assertThat(msgStored).isInstanceOf(Message.class).isNotNull();

        Assertions.assertThat(msgStored.getId()).isEqualTo(msg.getId());
    }


    @Test
     void shouldDeleteMessage() {
        var msg = UUID.fromString("7c12739a-6a7f-4b13-ba58-62b8a8d6e7f1");

        messageRepository.deleteById(msg);

        var msgRemoved = messageRepository.findById(msg);

        Assertions.assertThat(msgRemoved).isEmpty();

    }

    @Test
     void shouldListMessage() {

        var messagesStored = messageRepository.findAll();

        Assertions.assertThat(messagesStored).hasSizeGreaterThan(1);

    }

    @Test
     void shouldFindMessage() {
        var msg = UUID.fromString("7c12739a-6a7f-4b13-ba58-62b8a8d6e7f1");


        var msgStored = messageRepository.findById(msg);

        Assertions.assertThat(msgStored).isPresent();

        msgStored.ifPresent(msgs -> {
            Assertions.assertThat(msgs.getId()).isEqualTo(msg);
        });

    }




    private Message registerMessage(Message message) {
        return messageRepository.save(message);
    }
}
