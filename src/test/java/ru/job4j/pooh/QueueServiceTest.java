package ru.job4j.pooh;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Oywayten on 13.09.2022.
 */
public class QueueServiceTest {
    QueueService queueService = new QueueService();

    @Test
    public void whenPostParamThenGetQueue() {
        String paramForPostMethod = "temperature=18";
        /* Добавляем данные в очередь weather. Режим queue */
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        /* Забираем данные из очереди weather. Режим queue */
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is("temperature=18"));
    }

    @Test
    public void whenPostParamIsClient() {
        String paramForPostMethod = "client407";
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is("Нет данных"));
    }

    @Test
    public void whenPostParamNull() {
        queueService.process(
                new Req("POST", "queue", "weather", null)
        );
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is("Нет данных"));
    }

    @Test
    public void whenReqTypeIsNull() {
        String paramForPostMethod = "temperature=18";
        Resp process = queueService.process(
                new Req(null, "queue", "weather", paramForPostMethod)
        );
        Resp result = queueService.process(
                new Req(null, "queue", "weather", null)
        );
        assertThat(process.text(), is("Нет данных"));
        assertThat(result.text(), is("Нет данных"));
    }

    @Test
    public void whenPostModeIsNull() {
        String paramForPostMethod = "temperature=18";
        Resp process = queueService.process(
                new Req("POST", null, "weather", paramForPostMethod)
        );
        Resp result = queueService.process(
                new Req("GET", null, "weather", null)
        );
        assertThat(process.text(), is("Нет данных"));
        assertThat(result.text(), is("Нет данных"));
    }

    @Test
    public void whenGetParamNotNull() {
        String paramForPostMethod = "temperature=18";
        Resp process = queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", paramForPostMethod)
        );
        assertThat(process.text(), is("temperature=18"));
        assertThat(result.text(), is("Нет данных"));
    }

    @Test
    public void whenSourceIsNull() {
        String paramForPostMethod = "temperature=18";
        Resp process = queueService.process(
                new Req("POST", "queue", null, paramForPostMethod)
        );
        Resp result = queueService.process(
                new Req("GET", "queue", null, paramForPostMethod)
        );
        assertThat(process.text(), is("Нет данных"));
        assertThat(result.text(), is("Нет данных"));
    }
}