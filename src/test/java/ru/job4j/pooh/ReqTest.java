package ru.job4j.pooh;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by Oywayten on 13.09.2022.
 */
public class ReqTest {

    @Test
    public void whenQueueModePostMethod() {
        String ls = System.lineSeparator();
        String content = "POST /queue/weather HTTP/1.1"
                + ls + "Host: localhost:9000"
                + ls + "User-Agent: curl/7.72.0"
                + ls + "Accept: */*"
                + ls + "Content-Length: 14"
                + ls + "Content-Type: application/x-www-form-urlencoded"
                + ls + ""
                + ls + "temperature=18" + ls;
        Req req = Req.of(content);
        assertThat(req.httpRequestType(), is("POST"));
        assertThat(req.getPoohMode(), is("queue"));
        assertThat(req.getSourceName(), is("weather"));
        assertThat(req.getParam(), is("temperature=18"));
    }

    @Test
    public void whenQueueModeGetMethod() {
        String ls = System.lineSeparator();
        String content = "GET /queue/weather HTTP/1.1" + ls
                + "Host: localhost:9000" + ls
                + "User-Agent: curl/7.72.0" + ls
                + "Accept: */*" + ls + ls + ls;
        System.out.println(content);
        Req req = Req.of(content);
        assertThat(req.httpRequestType(), is("GET"));
        assertThat(req.getPoohMode(), is("queue"));
        assertThat(req.getSourceName(), is("weather"));
        assertThat(req.getParam(), is(""));
    }

    @Test
    public void whenTopicModePostMethod() {
        String ls = System.lineSeparator();
        String content = "POST /topic/weather HTTP/1.1"
                + ls + "Host: localhost:9000"
                + ls + "User-Agent: curl/7.72.0"
                + ls + "Accept: */*"
                + ls + "Content-Length: 14"
                + ls + "Content-Type: application/x-www-form-urlencoded"
                + ls + ""
                + ls + "temperature=18" + ls;
        Req req = Req.of(content);
        assertThat(req.httpRequestType(), is("POST"));
        assertThat(req.getPoohMode(), is("topic"));
        assertThat(req.getSourceName(), is("weather"));
        assertThat(req.getParam(), is("temperature=18"));
    }

    @Test
    public void whenTopicModeGetMethod() {
        String ls = System.lineSeparator();
        String content = "GET /topic/weather/client407 HTTP/1.1" + ls
                + "Host: localhost:9000" + ls
                + "User-Agent: curl/7.72.0" + ls
                + "Accept: */*" + ls + ls + ls;
        Req req = Req.of(content);
        assertThat(req.httpRequestType(), is("GET"));
        assertThat(req.getPoohMode(), is("topic"));
        assertThat(req.getSourceName(), is("weather"));
        assertThat(req.getParam(), is("client407"));
    }

    @Test
    public void whenModeIsWrong() {
        String ls = System.lineSeparator();
        String content = "GET /bbb/weather/client407 HTTP/1.1" + ls
                + "Host: localhost:9000" + ls
                + "User-Agent: curl/7.72.0" + ls
                + "Accept: */*" + ls + ls + ls;
        assertThatIndexOutOfBoundsException().isThrownBy(() -> Req.of(content));
    }

    @Test
    public void whenModeIsAbsent() {
        String ls = System.lineSeparator();
        String content = "GET //weather/client407 HTTP/1.1" + ls
                + "Host: localhost:9000" + ls
                + "User-Agent: curl/7.72.0" + ls
                + "Accept: */*" + ls + ls + ls;
        assertThatIndexOutOfBoundsException().isThrownBy(() -> Req.of(content));
    }

    @Test
    public void whenReqTypeWrong() {
        String ls = System.lineSeparator();
        String content = "BBB /topic/weather/client407 HTTP/1.1" + ls
                + "Host: localhost:9000" + ls
                + "User-Agent: curl/7.72.0" + ls
                + "Accept: */*" + ls + ls + ls;
        Req req = Req.of(content);
        assertThat(req.httpRequestType(), is("topic"));
        assertThat(req.getPoohMode(), is("weather"));
        assertThat(req.getSourceName(), is("client407"));
        assertThat(req.getParam(), is(""));
    }

    @Test
    public void whenWithoutClient() {
        String ls = System.lineSeparator();
        String content = "BBB /topic/weather/" + ls
                + "Host: localhost:9000" + ls
                + "User-Agent: curl/7.72.0" + ls
                + "Accept: */*" + ls + ls + ls;
        assertThatIndexOutOfBoundsException().isThrownBy(() -> Req.of(content));
    }

}