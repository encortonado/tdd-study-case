package com.bortolo.softwarequalityclass.service;


import com.bortolo.softwarequalityclass.exception.MessageNotFoundException;
import com.bortolo.softwarequalityclass.model.Message;
import com.bortolo.softwarequalityclass.repository.MessageRepository;
import com.bortolo.softwarequalityclass.utils.MessageHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import org.assertj.core.api.Assertions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class MessageServiceIT {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;


    @Nested
    class RegisterMessage {
        @Test
        void shouldRegisterMessageTest() {
            var msg = MessageHelper.messageBuilderWithoutUUID();

            var msgStored = messageService.registerMessage(msg);

            Assertions.assertThat(msgStored).isNotNull().isInstanceOf(Message.class);
            Assertions.assertThat(msgStored.getId()).isNotNull();
            Assertions.assertThat(msgStored.getDateCreationTime()).isNotNull();
        }
    }

    @Nested
    class FindMessage {
        @Test
        void shouldFindMessageTest() {

            var msgStored = messageService.findMessage(UUID.fromString("7c12739a-6a7f-4b13-ba58-62b8a8d6e7f1"));

            Assertions.assertThat(msgStored)
                    .isNotNull()
                    .isInstanceOf(Message.class);
            Assertions.assertThat(msgStored.getId())
                    .isNotNull()
                    .isEqualTo(UUID.fromString("7c12739a-6a7f-4b13-ba58-62b8a8d6e7f1"));
            Assertions.assertThat(msgStored.getUser_message())
                    .isNotNull()
                    .isEqualTo("USER TEST");

        }

        @Test
        void shouldThrowsExceptionIdNullFindMessageTest() {
            var id = UUID.randomUUID();

            Assertions.assertThatThrownBy(() -> messageService.findMessage(id))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessage("Mensagem nao encontrada");
        }

        @Test
        void shouldFindAllMessageTest() {

            Page<Message> listMsgs = messageService.findAllMessage(Pageable.unpaged());

            Assertions.assertThat(listMsgs).hasSize(3);
            Assertions.assertThat(listMsgs.getContent())
                    .hasSize(3)
                    .asList()
                    .allSatisfy(msg -> {
                        Assertions.assertThat(msg).isNotNull();
                    });

        }
    }

    @Nested
    class UpdateMessage {
        @Test
        void shouldUpdateMessageTest() {
            var id = UUID.fromString("f82e9d4a-3b7c-4e6a-af6c-9f8a21a97405");
            var msg = MessageHelper.messageBuilderWithoutUUID();
            msg.setId(id);

            var msgStored = messageService.updateMessage(id, msg);

            Assertions.assertThat(msgStored.getId()).isEqualTo(msg.getId());
            Assertions.assertThat(msgStored.getContent()).isEqualTo(msg.getContent());

        }

        @Test
        void shouldExceptionWhenIdNotExistsUpdateMessageTest() {

            var id = UUID.randomUUID();

            var msg = MessageHelper.messageBuilderWithoutUUID();
            msg.setId(id);

            Assertions.assertThatThrownBy(() -> messageService.updateMessage(id, msg))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessage("Mensagem nao encontrada");

        }


        @Test
        void shouldExceptionWhenIdIncorrectUpdateMessageTest() {

            var id = UUID.fromString("f82e9d4a-3b7c-4e6a-af6c-9f8a21a97405");

            var msg = MessageHelper.messageBuilderWithUUID();

            Assertions.assertThatThrownBy(() -> messageService.updateMessage(id, msg))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessage("Mensagem sem Id correto");

        }
    }


    @Nested
    class DeleteMessage {
        @Test
        void shouldDeleteMessageTest() {

            var id = UUID.fromString("f82e9d4a-3b7c-4e6a-af6c-9f8a21a97405");

            var isDeleted = messageService.deleteMessage(id);

            Assertions.assertThat(isDeleted).isTrue();

        }

        @Test
        void shouldNotDeleteMessageWithIdNullTest() {

            var id = UUID.randomUUID();

            Assertions.assertThatThrownBy(() -> {
                messageService.deleteMessage(id);
            }).hasMessage("Mensagem nao encontrada");


        }
    }








}
