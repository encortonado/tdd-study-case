package com.bortolo.softwarequalityclass.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {



    @Id
    private UUID id;

    @Column(nullable = false)
    @NotEmpty(message = "user not null")
    private String user_message;

    @Column(nullable = false)
    @NotEmpty(message = "content not null")
    private String content;

    @Builder.Default
    private LocalDateTime dateCreationTime = LocalDateTime.now();


    @Builder.Default
    private int reactions = 0;

}
