package com.bortolo.softwarequalityclass.utils;

import com.bortolo.softwarequalityclass.model.Message;

import java.util.UUID;

public abstract class MessageHelper {


    public static Message messageBuilderWithoutUUID() {
        return Message.builder()
                .user_message("User123")
                .content("conteudo123")
                .build();
    }

    public static Message messageBuilderWithUUID() {
        return Message.builder()
                .id(UUID.randomUUID())
                .user_message("User123")
                .content("conteudo123")
                .build();
    }

}
