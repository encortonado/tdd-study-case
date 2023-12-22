package com.bortolo.softwarequalityclass.controller;

import com.bortolo.softwarequalityclass.exception.MessageNotFoundException;
import com.bortolo.softwarequalityclass.handler.GlobalExceptionHandler;
import com.bortolo.softwarequalityclass.model.Message;
import com.bortolo.softwarequalityclass.service.MessageService;
import com.bortolo.softwarequalityclass.utils.MessageHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MessageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MessageService messageService;

    AutoCloseable mock;

    @BeforeEach()
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        MessageController messageController = new MessageController(messageService);

        MessageController mensagemController = new MessageController(messageService);
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @AfterEach()
    void tearDoown() throws Exception {
        mock.close();
    }


    @Nested
    class RegisterMessage {

        @Test
        void shouldRegisterMessageTest() throws Exception {
            var msg = MessageHelper.messageBuilderWithoutUUID();

            when(messageService.registerMessage(any(Message.class)))
                    .thenAnswer(i -> i.getArgument(0));

            mockMvc.perform(
                    post("/messages")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(msg))
            ).andExpect(status().isCreated());
            verify(messageService, times(1)).registerMessage(any(Message.class));
        }

        @Test
        void shouldExceptionWhenRegisterMessageNotJson() throws Exception {
            String payload = "not a Json";

            mockMvc.perform(post("/messages")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(payload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(messageService, never()).registerMessage(any(Message.class));

        }
    }

    @Nested
    class FindMessage {


        @Test
        void shouldFindMessageTest() throws Exception {
            var msg = MessageHelper.messageBuilderWithUUID();

            when(messageService.findMessage(any(UUID.class))).thenReturn(msg);

            mockMvc.perform(get("/messages/{id}", msg.getId()))
                    .andExpect(status().isOk());

            verify(messageService, times(1)).findMessage(any(UUID.class));

        }

        @Test
        void shouldThrowsExceptionIdNullFindMessageTest() throws Exception {
            var msg = MessageHelper.messageBuilderWithUUID();

            when(messageService.findMessage(msg.getId())).thenThrow(MessageNotFoundException.class);

            mockMvc.perform(get("/messages/{id}", msg.getId()))
                    .andExpect(status().isBadRequest());
            verify(messageService, times(1)).findMessage(msg.getId());

        }



    }

    @Nested
    class UpdateMessage {

        @Test
        void shouldUpdateMessageTest() throws Exception {

            var msg = MessageHelper.messageBuilderWithUUID();

            when(messageService.updateMessage(any(UUID.class), any(Message.class))).thenAnswer(i -> i.getArgument(1));

            mockMvc.perform(put(
                    "/messages/{id}",
                                msg.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(msg)))
                    .andExpect(status().isOk());
            verify(messageService, times(1)).updateMessage(msg.getId(), msg);

        }

        @Test
        void shouldExceptionWhenUpdateMessageNotJson() throws Exception {
            String payload = "not a Json";

            mockMvc.perform(put("/messages/{id}", UUID.randomUUID())
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(payload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(messageService, never()).updateMessage(any(UUID.class), any(Message.class));

        }



        @Test
        void shouldExceptionWhenIdNotExistsUpdateMessageTest() throws Exception {

            var id = UUID.randomUUID();
            var msg = MessageHelper.messageBuilderWithoutUUID();

            var contentEx = "Mensagem nao encontrada";

            when(messageService.updateMessage(id, msg)).thenThrow(new MessageNotFoundException(contentEx));

            mockMvc.perform(put("/messages/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(msg)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(contentEx));
//                    .andDo(print());
            verify(messageService, times(1)).updateMessage(any(UUID.class), any(Message.class));
        }

        @Test
        void shouldExceptionWhenIdIncorrectUpdateMessageTest() throws Exception {
            var id = UUID.randomUUID();
            var msg = MessageHelper.messageBuilderWithUUID();

            var contentEx = "Mensagem sem Id correto";

            when(messageService.updateMessage(id, msg)).thenThrow(new MessageNotFoundException(contentEx));

            mockMvc.perform(put("/messages/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(msg)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(contentEx));
//                    .andDo(print());
            verify(messageService, times(1)).updateMessage(any(UUID.class), any(Message.class));
        }

    }

    @Nested
    class DeleteMessage {

        @Test
        void shouldDeleteMessageTest() throws Exception {

            var id = UUID.randomUUID();

            when(messageService.deleteMessage(id)).thenReturn(true);

            mockMvc.perform(delete("/messages/{id}", id))
                    .andExpect(status().isOk());
            verify(messageService, times(1)).deleteMessage(any(UUID.class));

        }

        @Test
        void shouldNotDeleteMessageWithIdNullTest() throws Exception {

            var id = UUID.randomUUID();

            var exception = "Mensagem nao encontrada";

            when(messageService.deleteMessage(id)).thenThrow(new MessageNotFoundException(exception));

            mockMvc.perform(delete("/messages/{id}", id))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(exception));
            verify(messageService, times(1)).deleteMessage(id);

        }

    }

    @Nested
    class ListMessages {

        @Test
        void devePermitirListarMensagens() throws Exception {
            var mensagem = MessageHelper.messageBuilderWithoutUUID();
            Page<Message> page = new PageImpl<>(Collections.singletonList(
                    mensagem
            ));
            when(messageService.findAllMessage(any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/messages")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(mensagem.getId().toString()))
                    .andExpect(jsonPath("$.content[0].content").value(mensagem.getContent()))
                    .andExpect(jsonPath("$.content[0].user_message").value(mensagem.getUser_message()))
                    .andExpect(jsonPath("$.content[0].dateCreationTime").exists())
                    .andExpect(jsonPath("$.content[0].reactions").exists());
            verify(messageService, times(1))
                    .findAllMessage(any(Pageable.class));
        }

    }

    public static String asJsonString(final Object obj) throws JsonProcessingException {

        ObjectMapper om = new ObjectMapper();

        om.registerModule(new JavaTimeModule());

        return om.writeValueAsString(obj);
    }

}
