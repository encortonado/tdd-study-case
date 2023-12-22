package com.bortolo.softwarequalityclass.service;

import com.bortolo.softwarequalityclass.exception.MessageNotFoundException;
import com.bortolo.softwarequalityclass.model.Message;
import com.bortolo.softwarequalityclass.repository.MessageRepository;
import com.bortolo.softwarequalityclass.utils.MessageHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    private MessageService messageService;

    AutoCloseable mock;

    @BeforeEach
    public void setup() {
        mock = MockitoAnnotations.openMocks(this);
        messageService = new MessageServiceImpl(messageRepository);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mock.close();
    }

    @Test
    void shouldRegisterMessageTest() {

        var msg = MessageHelper.messageBuilderWithoutUUID();

        when(messageRepository.save(any(Message.class))).thenAnswer(i -> i.getArgument(0));

        var msgStored = messageService.registerMessage(msg);

        Assertions.assertThat(msgStored).isInstanceOf(Message.class).isNotNull();

        Assertions.assertThat(msgStored.getContent()).isEqualTo(msg.getContent());

        Assertions.assertThat(msgStored.getId()).isNotNull();

        verify(messageRepository, times(1)).save(any(Message.class));


    }

    @Test
    void shouldFindMessageTest() {

        var id = UUID.randomUUID();
        var msg = MessageHelper.messageBuilderWithoutUUID();
        msg.setId(id);

        when(messageRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(msg));

        var msgStored = messageService.findMessage(id);

        Assertions.assertThat(msgStored).isEqualTo(msg);
        verify(messageRepository, times(1)).findById(any(UUID.class));


    }

    @Test
    void shouldThrowsExceptionIdNullFindMessageTest() {

        var id = UUID.randomUUID();

        when(messageRepository.findById(id))
                .thenReturn(Optional.empty());


        Assertions.assertThatThrownBy(() -> messageService.findMessage(id))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessage("Mensagem nao encontrada");
        verify(messageRepository, times(1)).findById(id);


    }

    @Test
    void shouldUpdateMessageTest() {
        // criando cenário
        var id = UUID.randomUUID();
        var oldMsg = MessageHelper.messageBuilderWithoutUUID();
        oldMsg.setId(id);

        var newMsg = Message
                .builder()
                .id(id)
                .user_message(oldMsg.getUser_message())
                .dateCreationTime(oldMsg.getDateCreationTime())
                .content("Teste 1234")
                .reactions(oldMsg.getReactions())
                .build();

//        newMsg.setContent("Teste 1234");

        when(messageRepository.findById(id))
                .thenReturn(Optional.of(oldMsg));

        when(messageRepository.save(newMsg))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // atuação
        var msgStored = messageService.updateMessage(id, newMsg);

        // garantindo

        Assertions.assertThat(msgStored).isNotEqualTo(oldMsg);
        Assertions.assertThat(msgStored).isEqualTo(newMsg);
        verify(messageRepository, times(1)).findById(id);
        verify(messageRepository, times(1)).save(newMsg);


    }

    @Test
    void shouldExceptionWhenIdNotExistsUpdateMessageTest() {
        // criando cenário
        var id = UUID.randomUUID();
        var oldMsg = MessageHelper.messageBuilderWithoutUUID();
        oldMsg.setId(id);

        when(messageRepository.findById(id))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> messageService.updateMessage(id, oldMsg))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessage("Mensagem nao encontrada");

        verify(messageRepository, times(1)).findById(any(UUID.class));
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void shouldExceptionWhenIdIncorrectUpdateMessageTest() {
        // criando cenário
        var id = UUID.randomUUID();
        var oldMsg = MessageHelper.messageBuilderWithoutUUID();
        oldMsg.setId(id);

        var newMsg = Message
                .builder()
                .id(UUID.randomUUID())
                .user_message(oldMsg.getUser_message())
                .dateCreationTime(oldMsg.getDateCreationTime())
                .content("Teste 1234")
                .reactions(oldMsg.getReactions())
                .build();

        when(messageRepository.findById(id))
                .thenReturn(Optional.of(oldMsg));

        Assertions.assertThatThrownBy(() -> messageService.updateMessage(id, newMsg))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessage("Mensagem sem Id correto");

        verify(messageRepository, times(1)).findById(any(UUID.class));
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void shouldDeleteMessageTest() {

        var id = UUID.randomUUID();
        var msg = MessageHelper.messageBuilderWithoutUUID();

        msg.setId(id);

        when(messageRepository.findById(id)).thenReturn(Optional.of(msg));
        doNothing().when(messageRepository).deleteById(id);

        var isRemoved = messageService.deleteMessage(id);

        Assertions.assertThat(isRemoved).isTrue();

        verify(messageRepository, times(1)).findById(any(UUID.class));
        verify(messageRepository, times(1)).deleteById(any(UUID.class));


    }

    @Test
    void shouldNotDeleteMessageWithIdNullTest() {

        var id = UUID.randomUUID();

        when(messageRepository.findById(id)).thenReturn(Optional.empty());
        doNothing().when(messageRepository).deleteById(id);

        Assertions.assertThatThrownBy(() -> messageService.deleteMessage(id))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessage("Mensagem nao encontrada");

        verify(messageRepository, times(1)).findById(any(UUID.class));
        verify(messageRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void shouldFindAllMessageTest() {

        Page<Message> messageList = new PageImpl<>(Arrays.asList(
                MessageHelper.messageBuilderWithoutUUID(),
                MessageHelper.messageBuilderWithoutUUID()));

        when(messageRepository.listMessages(any(Pageable.class))).thenReturn(messageList);

        var objStored = messageService.findAllMessage(Pageable.unpaged());

        Assertions.assertThat(objStored).hasSize(2);
        Assertions.assertThat(objStored.getContent())
                .asList()
                .allSatisfy(msg -> {
                    Assertions.assertThat(msg).isNotNull().isInstanceOf(Message.class);
        });

        verify(messageRepository, times(1)).listMessages(any(Pageable.class));

    }



}
