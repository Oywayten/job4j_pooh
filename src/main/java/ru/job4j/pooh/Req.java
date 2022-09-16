package ru.job4j.pooh;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Класс для парсинга входящего запроса.
 * Created by Oywayten on 13.09.2022.
 */
public class Req {
    /**
     * GET или POST. Он указывает на тип запроса.
     */
    private final String httpRequestType;
    /**
     * указывает на режим работы: queue или topic
     */
    private final String poohMode;
    /**
     * имя очереди или топика
     */
    private final String sourceName;
    /**
     * содержимое запроса
     */
    private final String param;

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    /**
     * Метод создает объект типа Req на основе парсинга строки входящего запроса.
     * Разбивает строку на части и выбирает те, которые совпадают по условию.
     *
     * @param content строка входящего запроса
     * @return возвращает объект типа Req
     */
    public static Req of(String content) {
        Scanner sc = new Scanner(content);
        List<String> strings = sc.tokens()
                .filter(s -> "GET".equals(s)
                        || "POST".equals(s)
                        || s.startsWith("/queue/")
                        || s.startsWith("/topic/")
                        || s.matches(".*=.*"))
                .flatMap(s -> Arrays.stream(s.split("/")).filter(s1 -> s1.length() > 0))
                .toList();
        String param = "";
        String reqType = strings.get(0);
        String mode = strings.get(1);
        if ("GET".equals(reqType) && "topic".equals(mode) || "POST".equals(reqType)) {
            param = strings.get(3);
        }
        return new Req(reqType, mode, strings.get(2), param);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }
}