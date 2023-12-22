package com.bortolo.softwarequalityclass.controller;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;


class AppControllerTest {


    AppController appController;

    @BeforeEach
    void setup() {
        appController = new AppController();
    }

    @Test
    void shouldSumTest() {


        int num1 = 1;
        int num2 = 2;

        int result = appController.sum(num1, num2);

        assertEquals(num1 + num2, result);

    }

    @Test
    void shouldMinusTest() {


        int num1 = 1;
        int num2 = 2;

        int result = appController.sub(num1, num2);

        assertEquals(num1 - num2, result);

    }

    @Test
    void shouldMultiTest() {

        int num1 = 1;
        int num2 = 2;

        int result = appController.multi(num1, num2);

        assertEquals(num1 * num2, result);

    }

    @Test
    void shouldDivideTest() {

        int num1 = 8;
        int num2 = 2;

        int result = appController.divide(num1, num2);

        assertEquals((num1 / num2), result);
    }

    @Test
    void shouldDivideZeroExceptionTest() {

        int num1 = 8;
        int num2 = 2;

        assertThrows(ArithmeticException.class, () -> {
            appController.divide(num1, 0);
        });

    }



}
