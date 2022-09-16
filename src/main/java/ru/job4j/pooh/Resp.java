package ru.job4j.pooh;

/**
 * Класс ответа от сервиса.
 * Created by Oywayten on 13.09.2022.
 */
public class Resp {
    /**
     * текст ответа
     */
    private final String text;
    /**
     * HTTP response status codes
     */
    private final String status;

    public Resp(String text, String status) {
        this.text = text;
        this.status = status;
    }

    public String text() {
        return text;
    }

    public String status() {
        return status;
    }
}