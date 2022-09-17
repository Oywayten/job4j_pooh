package ru.job4j.pooh;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

/**
 * Сервис работы с топиком Topic.
 * Отправитель посылает запрос на добавление данных с указанием топика (weather) и значением параметра (temperature=18).
 * Сообщение помещается в конец каждой индивидуальной очереди получателей.
 * Если топика нет в сервисе, то данные игнорируются.
 * <p>
 * Получатель посылает запрос на получение данных с указанием топика. Если топик отсутствует, то создается новый.
 * А если топик присутствует, то сообщение забирается из начала индивидуальной очереди получателя и удаляется.
 * <p>
 * Когда получатель впервые получает данные топика – для него создается индивидуальная пустая очередь.
 * Все последующие сообщения от отправителей с данными для этого топика помещаются в эту очередь тоже.
 * <p>
 * Таким образом в режиме "topic" для каждого потребителя своя будет уникальная очередь с данными,
 * в отличие от режима "queue", где для все потребители получают данные из одной и той же очереди.
 * <p>
 * Примеры запросов.
 * <p>
 * Отправитель.
 * POST /topic/weather -d "temperature=18"
 * topic указывает на режим «топик».
 * weather указывает на имя топика.
 * <p>
 * Получатель.
 * GET /topic/weather/1
 * 1 - ID получателя.
 * Ответ: temperature=18
 * <p>
 * Created by Oywayten on 13.09.2022.
 */
public class TopicService implements Service {

    public static final String STATUS_OK = "200";
    public static final String NO_CONTENT = "204";
    private final ConcurrentHashMap<String,
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topics = new ConcurrentHashMap<>();

    public static boolean isParam(String param) {
        return param.contains("=");
    }

    public static boolean isValid(String reqType, String qMode) {
        return ("POST".equals(reqType) || "GET".equals(reqType))
                && ("topic".equals(qMode) || "queue".equals(qMode));
    }

    @Override
    public Resp process(Req req) {
        String queueName = req.getSourceName();
        String requestType = req.httpRequestType();
        Resp result = new Resp("", NO_CONTENT);
        String param = req.getParam();
        if (Objects.nonNull(param) && Objects.nonNull(queueName)) {
            if ("GET".equals(requestType)) {
                topics.putIfAbsent(param, new ConcurrentHashMap<>());
                topics.get(param).putIfAbsent(queueName, new ConcurrentLinkedQueue<>());
                String poll = topics.get(param).get(queueName).poll();
                if (Objects.nonNull(poll)) {
                    result = new Resp(poll, STATUS_OK);
                }
            } else if ("POST".equals(requestType)) {
                if (param.contains("=") && !topics.isEmpty()) {
                    Stream.of(topics)
                            .flatMap(map -> map.values().stream())
                            .filter(map -> map.containsKey(queueName))
                            .flatMap(map -> map.values().stream())
                            .forEach(strings -> strings.add(param));
                    result = new Resp(param, STATUS_OK);
                }
            }
        }
        return result;
    }
}
