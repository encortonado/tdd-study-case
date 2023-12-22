package com.bortolo.softwarequalityclass.repository;

import com.bortolo.softwarequalityclass.model.Message;
import com.bortolo.softwarequalityclass.utils.MessageHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class MessageRepositoryTest {


    @Mock()
    private MessageRepository messageRepository;


    AutoCloseable openMocks;


    @BeforeEach()
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception{
        openMocks.close();
    }

    @Test
    public void shouldAllowRegisterMessageTest() {
        // criar cenário
        var msg = MessageHelper.messageBuilderWithUUID();
        when(messageRepository.save(any(Message.class))).thenReturn(msg);
        // rodar
        var msgStored = messageRepository.save(msg);
        // verificar
        Assertions.assertThat(msgStored).isNotNull().isEqualTo(msg);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    public void shouldAllowUpdateMessageTest() {
//        fail();
    }

    @Test
    public void shouldAllowDeleteMessageTest() {
        // criando cenário
        var id = UUID.randomUUID();

        doNothing().when(messageRepository).deleteById(any(UUID.class));

        // rodando cenário
        messageRepository.deleteById(id);

        // verificando cenário


        verify(messageRepository, times(1)).deleteById(any(UUID.class));


    }

    @Test
    public void shouldAllowGetMessageTest() {

        // criando cenário
        var id = UUID.randomUUID();
        var msg = MessageHelper.messageBuilderWithUUID();
        msg.setId(id);


        // rodando cenário
       when(messageRepository.findById(any(UUID.class))).thenReturn(Optional.of(msg));
       var msgStored = messageRepository.findById(id);


       // verificando cenário
        Assertions.assertThat(msgStored).isPresent().containsSame(msg);

        msgStored.ifPresent(msg2 -> {
            Assertions.assertThat(msg2.getId()).isEqualTo(msg.getId());
            Assertions.assertThat(msg2.getContent()).isEqualTo(msg.getContent());
        });

        verify(messageRepository, times(1)).findById(any(UUID.class));

    }

    @Test
    public void shouldListMessage() {

        var msgList = Arrays.asList(MessageHelper.messageBuilderWithUUID(), MessageHelper.messageBuilderWithUUID());

        when(messageRepository.findAll()).thenReturn(msgList);

        var msgStored = messageRepository.findAll();

        Assertions.assertThat(msgStored).hasSize(2).containsExactlyInAnyOrder(msgList.get(0), msgList.get(1));

        verify(messageRepository, times(1)).findAll();
    }



}
