package com.bortolo.softwarequalityclass.controller;

import com.bortolo.softwarequalityclass.utils.MessageHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class MessageControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CreateMessage {

        @Test
        void shouldRegisterMessageTest() {
            var msg = MessageHelper.messageBuilderWithoutUUID();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(msg)
                    .log().all()
                    .when()
                    .post("/messages")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath(
                            "schemas/Message.schema.json"));

        }

        @Test
        void shouldExceptionWhenRegisterMessageNotJson() {

            var msg = "body";

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(msg)
                    .log().all()
            .when()
                    .post("/messages")
            .then()
                    .log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("$", hasKey("timestamp"))
                    .body("$", hasKey("status"))
                    .body("$", hasKey("error"))
                    .body("$", hasKey("path"));

        }

    }

    @Nested
    class FindMessage {

        @Test
        void shouldFindMessageTest() {
            var id = "f82e9d4a-3b7c-4e6a-af6c-9f8a21a97405";

            when()
                    .get("/messages/{id}", id)
            .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        void shouldThrowsExceptionIdNullFindMessageTest() {
            var id = UUID.randomUUID().toString();

            when()
                    .get("/messages/{id}", id)
            .then()
                    .log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }


    @Nested
    class UpdateMessage {

        @Test
        void shouldUpdateMessageTest() {

            var msg = MessageHelper.messageBuilderWithoutUUID();
            msg.setId(UUID.fromString("7c12739a-6a7f-4b13-ba58-62b8a8d6e7f1"));

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(msg)
            .when()
                    .put("/messages/{id}", msg.getId())
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/Message.schema.json"));

        }

        @Test
        void shouldExceptionWhenUpdateMessageNotJson() {
            var msg = MessageHelper.messageBuilderWithoutUUID();
            msg.setId(UUID.fromString("68c7f5b6-8e9d-4a3c-bf20-6f4d2e1b3aa1"));

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("eu sou uma mensagem errada")
                    .when()
                    .put("/messages/{id}", msg.getId())
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
//                    .body(matchesJsonSchemaInClasspath("schemas/Message.schema.json"));
        }

        @Test
        void shouldExceptionWhenIdNotExistsUpdateMessageTest() {
            var msg = MessageHelper.messageBuilderWithoutUUID();
            msg.setId(UUID.fromString("68c7f5b6-8e9d-4a3c-bf20-6f4d2e1b3aa1"));

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(msg)
                    .when()
                    .put("/messages/{id}", UUID.randomUUID())
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
//                    .body(matchesJsonSchemaInClasspath("schemas/Message.schema.json"));
        }

        @Test
        void shouldExceptionWhenIdIncorrectUpdateMessageTest() {
            var msg = MessageHelper.messageBuilderWithUUID();


            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(msg)
                    .when()
                    .put("/messages/{id}", UUID.fromString("68c7f5b6-8e9d-4a3c-bf20-6f4d2e1b3aa1"))
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class DeleteMessage {

        @Test
        void shouldDeleteMessageTest() {

            var id = UUID.fromString("68c7f5b6-8e9d-4a3c-bf20-6f4d2e1b3aa1");

            when()
                    .delete("/messages/{id}", id)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo("Menssagem Removida com Sucesso"));

        }

        @Test
        void shouldNotDeleteMessageWithIdNullTest() {
            var id = UUID.randomUUID();

            when()
                    .delete("/messages/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Mensagem nao encontrada"));

        }
    }

    @Nested
    class ListMessage {

        @Test
        void devePermitirListarMensagens() {
            given()
                    .param("page", "0")
                    .param("size", "2")
                    .when()
                    .get("/messages")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/MessageList.schema.json"));

        }

        @Test
        void shouldListMessagesWhenPaginationNotInformed() {

            given()

            .when()
                    .get("/messages")
            .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/MessageList.schema.json"));

        }
    }

}
