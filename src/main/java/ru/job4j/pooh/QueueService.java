package ru.job4j.pooh;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Сервис работы с очередью Queue.
 * Отправитель посылает запрос на добавление данных с указанием очереди (weather) и
 * значением параметра (temperature=18). Сообщение помещается в конец очереди.
 * Если очереди нет в сервисе, то нужно создать новую и поместить в нее сообщение.
 * <p>
 * Получатель посылает запрос на получение данных с указанием очереди. Сообщение забирается из начала очереди и удаляется.
 * Если в очередь приходят несколько получателей, то они поочередно получают сообщения из очереди.
 * Каждое сообщение в очереди может быть получено только одним получателем.
 * <p>
 * Примеры запросов.
 * POST запрос должен добавить элементы в очередь weather.
 * curl -X POST -d "temperature=18" localhost:9000/queue/weather
 * queue указывает на режим «очередь».
 * weather указывает на имя очереди.
 * <p>
 * GET запрос должен получить элементы из очереди weather.
 * curl -X GET localhost:9000/queue/weather
 * Ответ: temperature=18
 * <p>
 * Created by Oywayten on 13.09.2022.
 */
public class QueueService implements Service {
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    /**
     * Метод обработки запроса для режима работы queue.
     * Метод работает так, как указано в описании класса.
     * Если поле text ответа Resp == null, то метод сообщает, что .
     * Если тип запроса не GET и не POST, то приходит ответ о неверном запросе.
     * В случае запроса GET или POST, и наличии корректных данных по нему, метод отправляет строку и ответ 200.
     *
     * @param req принимает запрос Req
     * @return возвращает ответ Resp
     */
    @Override
    public Resp process(Req req) {
        String queueName = req.getSourceName();
        String requestType = req.httpRequestType();
        Resp result = new Resp("", Assistant.NOT_IMPLEMENTED);
        if (isValid(req)) {
            String param = req.getParam();
            if ("POST".equals(requestType)) {
                queue.putIfAbsent(queueName, new ConcurrentLinkedQueue<>());
                queue.get(queueName).add(param);
                result = new Resp(param, Assistant.STATUS_OK);
            } else if ("GET".equals(requestType)) {
                String poll = queue.getOrDefault(queueName, new ConcurrentLinkedQueue<>()).poll();
                if (Objects.isNull(poll)) {
                    result = new Resp("", Assistant.NO_CONTENT);
                } else {
                    result = new Resp(poll, Assistant.STATUS_OK);
                }
            }
        } else {
            result = new Resp("", Assistant.BAD_REQUEST);
        }
        return result;
    }

    public boolean isValid(Req req) {
        String queueName = req.getSourceName();
        String requestType = req.httpRequestType();
        boolean isValid = false;
        if (Objects.nonNull(queueName) && Objects.nonNull(requestType)) {
            String param = req.getParam();
            if ("POST".equals(requestType)) {
                if (Objects.nonNull(param) && param.contains("=")) {
                    isValid = true;
                }
            } else if ("GET".equals(requestType)) {
                if (Objects.isNull(param)) {
                    isValid = true;
                }
            } else {
                isValid = true;
            }
        }
        return isValid;
    }
}
