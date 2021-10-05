package org.homs.llp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.homs.llp.AsynchLLPSocketMock.EConnectionStatus.*;

public class AsyncLLPSocketsIntegrationTest {

    @DisplayName("Integration test of LLP Server & Client doing a simple flow")
    @Test
    void testName() throws Exception {

        var order = //
                /**/"MSH|^~\\&|||LISTestTool||20210201||OML^O21^OML_O21|MSGID-10000047||2.5|0\n" +
                /**/"PID|1|MN-10000000|||Lopez^Roger^John||19800101|M\n" +
                /**/"ORC|NW|CASE-100||||E\n" +
                /**/"OBR||CASE-100;A;1;1||5^TEST^STAIN|||||||||||||||CASE-100;A;1;1^1|CASE-100;A;1^1|CASE-100;A^A";

        var ack = //
                /**/"MSH|^~\\&|Ventana Transmit|APLab|Ventana|APLab|20210129213546||ACK^O21|MCI-11774fdbe129|P|2.4\n" +
                /**/"MSA|AA|MSGID-10000047||||";

        List<String> serverReceivedMessages = new ArrayList<>();
        List<String> clientReceivedMessages = new ArrayList<>();

        var server = new AsyncLLPSocketServer();
        var client = new AsyncLLPSocketClient();

        List<AsynchLLPSocketMock.EConnectionStatus> serverStatusesList = new ArrayList<>();
        server.setConnectionStatusChangeListener(serverStatusesList::add);
        List<AsynchLLPSocketMock.EConnectionStatus> clientStatusesList = new ArrayList<>();
        client.setConnectionStatusChangeListener(clientStatusesList::add);

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(DISCONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        server.start("5555", (msg) -> {
            serverReceivedMessages.add(msg);
            server.send(ack);
        });

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(AWAITING_CONNECTION);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        client.start("127.0.0.1:5555", clientReceivedMessages::add);

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(CONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(CONNECTED);

        client.send(order);

        Thread.sleep(100L);

        client.stop();
        server.stop();

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(DISCONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        assertThat(serverReceivedMessages).hasSize(1);
        assertThat(serverReceivedMessages.get(0)).isEqualTo(order);

        assertThat(clientReceivedMessages).hasSize(1);
        assertThat(clientReceivedMessages.get(0)).isEqualTo(ack);
    }

    @DisplayName("Tests the connection handling: client stops before the server")
    @Test
    void testConnectionStatus() throws Exception {
        var server = new AsyncLLPSocketServer();
        var client = new AsyncLLPSocketClient();

        assertThat(server.getConnectionStatus()).isEqualTo(DISCONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        server.start("5555", (msg) -> {
        });

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(AWAITING_CONNECTION);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        client.start("127.0.0.1:5555", (msg) -> {
        });

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(CONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(CONNECTED);

        client.stop();

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(AWAITING_CONNECTION);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        server.stop();

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(DISCONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);
    }

    @DisplayName("Tests the connection handling: server stops before the client")
    @Test
    void testConnectionStatus2() throws Exception {
        var server = new AsyncLLPSocketServer();
        var client = new AsyncLLPSocketClient();

        assertThat(server.getConnectionStatus()).isEqualTo(DISCONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        server.start("5555", (msg) -> {
        });

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(AWAITING_CONNECTION);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        client.start("127.0.0.1:5555", (msg) -> {
        });

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(CONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(CONNECTED);

        server.stop();

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(DISCONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        client.stop();

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(DISCONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);
    }

    @DisplayName("Tests the connection handling: client stops and reconnects to server")
    @Test
    void testConnectionStatus3() throws Exception {

        var server = new AsyncLLPSocketServer();
        var client = new AsyncLLPSocketClient();

        assertThat(server.getConnectionStatus()).isEqualTo(DISCONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        server.start("5555", (msg) -> {
        });

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(AWAITING_CONNECTION);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        client.start("127.0.0.1:5555", (msg) -> {
        });

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(CONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(CONNECTED);

        client.stop();

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(AWAITING_CONNECTION);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        client.start("127.0.0.1:5555", (msg) -> {
        });

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(CONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(CONNECTED);

        client.stop();
        server.stop();

        Thread.sleep(100L);
        assertThat(server.getConnectionStatus()).isEqualTo(DISCONNECTED);
        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);
    }


    @DisplayName("Tests the connection handling: as a client we try to send a message, but its not connected")
    @Test
    void testNoConnection() {

        var client = new AsyncLLPSocketClient();

        assertThat(client.getConnectionStatus()).isEqualTo(DISCONNECTED);

        try {
            client.send("aklsdjfhaskdh");
            fail("an exception should be thrown.");
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("Client is not connected");
        }
    }

    @DisplayName("Performance test of LLP Server & Client doing a simple flow")
    @Test
    void testPerformance() throws Exception {

        final Logger LOG = LoggerFactory.getLogger(AsyncLLPSocketsIntegrationTest.class);

        var order = //
                /**/"MSH|^~\\&|||LISTestTool||20210201||OML^O21^OML_O21|MSGID-10000047||2.5|0\n" +
                /**/"PID|1|MN-10000000|||Lopez^Roger^John||19800101|M\n" +
                /**/"ORC|NW|CASE-100||||E\n" +
                /**/"OBR||CASE-100;A;1;1||5^TEST^STAIN|||||||||||||||CASE-100;A;1;1^1|CASE-100;A;1^1|CASE-100;A^A";

        var ack = //
                /**/"MSH|^~\\&|Ventana Transmit|APLab|Ventana|APLab|20210129213546||ACK^O21|MCI-11774fdbe129|P|2.4\n" +
                /**/"MSA|AA|MSGID-10000047||||";

        List<String> serverReceivedMessages = new ArrayList<>();
        List<String> clientReceivedMessages = new ArrayList<>();

        var server = new AsyncLLPSocketServer();
        var client = new AsyncLLPSocketClient();

        server.start("5555", (msg) -> {
            serverReceivedMessages.add(msg);
            server.send(ack);
        });

        client.start("127.0.0.1:5555", clientReceivedMessages::add);

        int numberOfRequests = 50000;
        long thresholdMs = 50L;

        long timeOutMs = thresholdMs * numberOfRequests;

        //int responsesOK = 0;
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < numberOfRequests; i++) {

            client.send(order);

            if (i % (numberOfRequests / 10) == 0) {
                LOG.info("Processed " + i + " of " + numberOfRequests + " ProcessOrder+ACK's");
            }
        }

        long elapsedTime = -1L;
        while (clientReceivedMessages.size() < numberOfRequests) {
            elapsedTime = System.currentTimeMillis() - t1;
            if (elapsedTime >= timeOutMs) {
                LOG.info("TIMEOUT");
                break;
            }
            Thread.sleep(10L);
        }

        LOG.info("=========================================");
        LOG.info(numberOfRequests + " in " + elapsedTime + " ms");
        LOG.info("" + (elapsedTime * 1.0 / numberOfRequests) + " ms/request");
        LOG.info("=========================================");

        assertThat(clientReceivedMessages.size()).isEqualTo(numberOfRequests);

        client.stop();
        server.stop();
    }
}
