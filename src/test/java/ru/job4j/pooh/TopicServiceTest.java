package ru.job4j.pooh;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Oywayten on 13.09.2022.
 */
public class TopicServiceTest {
    TopicService topicService = new TopicService();
    String paramForPublisher = "temperature=18";
    String paramForSubscriber1 = "client407";

    @Test
    public void whenTopic() {
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
        /* Режим topic. Подписываемся на топик weather. client407. */
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Добавляем данные в топик weather. */
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407. */
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client6565.
        Очередь отсутствует, т.к. еще не был подписан - получит пустую строку */
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
        assertThat(result1.text(), is("temperature=18"));
        assertThat(result2.text(), is(""));
    }

    @Test
    public void whenSubscriberIsParam() {
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1));
        Resp result1 = topicService.process(
                new Req("POST", "topic", "weather", paramForSubscriber1)
        );
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1));
        assertThat(result1.text(), is(""));
        assertThat(result2.text(), is(""));
    }

    @Test
    public void whenSubscribeAndReqTypeIsNull() {
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1));
        Resp process = topicService.process(
                new Req(null, "topic", "weather", paramForPublisher)
        );
        Resp result2 = topicService.process(
                new Req(null, "topic", "weather", paramForSubscriber1));
        assertThat(process.text(), is(""));
        assertThat(result2.text(), is(""));
    }

    @Test
    public void whenSubscribeAndReqTypeIsWrong() {
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1));
        Resp process = topicService.process(
                new Req("null", "topic", "weather", paramForPublisher)
        );
        Resp result2 = topicService.process(
                new Req("null", "topic", "weather", paramForSubscriber1));
        assertThat(process.text(), is(""));
        assertThat(result2.text(), is(""));
    }

    @Test
    public void whenSubscribeAndSourceIsNull() {
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1));
        Resp process = topicService.process(
                new Req("POST", "null", null, paramForPublisher)
        );
        Resp result2 = topicService.process(
                new Req("GET", "null", null, paramForSubscriber1));
        assertThat(process.text(), is(""));
        assertThat(result2.text(), is(""));
    }


    @Test
    public void whenSubscribeAndParamIsNull() {
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1));
        Resp process = topicService.process(
                new Req("POST", "topic", "weather", null)
        );
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1));
        assertThat(process.text(), is(""));
        assertThat(result2.text(), is(""));
    }

    @Test
    public void whenSubscribeAndPost1AndGet2() {
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1));
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1));
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1));
        assertThat(result1.text(), is("temperature=18"));
        assertThat(result2.text(), is(""));
    }
}