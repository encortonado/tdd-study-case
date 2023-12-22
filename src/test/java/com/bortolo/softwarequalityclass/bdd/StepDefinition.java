package com.bortolo.softwarequalityclass.bdd;

import com.bortolo.softwarequalityclass.model.Message;
import com.bortolo.softwarequalityclass.utils.MessageHelper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class StepDefinition {

    private Response response;

    private Message messageResponse;

    private final String ENDPOINT_API = "http://localhost:8080/messages";


    @Quando("eu registrar uma mensagem")
    public Message eu_registrar_uma_mensagem() {
        var msgReq = MessageHelper.messageBuilderWithoutUUID();
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(msgReq)
            .when()
                .post(ENDPOINT_API);

        messageResponse = response.then().extract().as(Message.class);
        return messageResponse;
    }

    @Entao("o valor deve ser registrado no sistema")
    public void o_valor_deve_ser_registrado_no_sistema() {
       response
               .then()
               .statusCode(HttpStatus.CREATED.value())
               .body(matchesJsonSchemaInClasspath("schemas/Message.schema.json"));
    }

    @Entao("o valor deve ser apresentado")
    public void o_valor_deve_ser_apresentado() {
        response
                .then()
                .body(matchesJsonSchemaInClasspath("schemas/Message.schema.json"));
    }


    @Dado("que uma mensagem já foi publicada")
    public void que_uma_mensagem_já_foi_publicada() {

        messageResponse = eu_registrar_uma_mensagem();



    }
    @Quando("efetuar a busca de mensagem")
    public void efetuar_a_busca_de_mensagem() {

        response = when()
                .get(ENDPOINT_API + "/{id}", messageResponse.getId());



    }
    @Entao("A mensagem é exibida com sucesso")
    public void a_mensagem_é_exibida_com_sucesso() {
        response
                .then()
                .body(matchesJsonSchemaInClasspath("schemas/Message.schema.json"));
    }

}
