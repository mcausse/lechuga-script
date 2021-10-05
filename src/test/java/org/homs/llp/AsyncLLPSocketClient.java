package org.homs.llp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * High-performance asynchronous LLP socket client.
 *
 * @author homscaum
 */
public class AsyncLLPSocketClient implements AsynchLLPSocketMock {

    static final Logger LOG = LoggerFactory.getLogger(AsyncLLPSocketClient.class);

    final Charset charset;

    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;
    Consumer<String> onReceiveListener;
    Consumer<EConnectionStatus> onConnectionStatusChangeListener;

    public AsyncLLPSocketClient(Charset charset) {
        super();
        this.charset = charset;
    }

    public AsyncLLPSocketClient() {
        this(StandardCharsets.UTF_8);
    }

    @Override
    public void setConnectionStatusChangeListener(Consumer<EConnectionStatus> onConnectionStatusChangeListener) {
        this.onConnectionStatusChangeListener = onConnectionStatusChangeListener;
        onConnectionStatusChangeListener.accept(getConnectionStatus());
    }

    public EConnectionStatus getConnectionStatus() {
        if (clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed()) {
            return EConnectionStatus.CONNECTED;
        } else {
            return EConnectionStatus.DISCONNECTED;
        }
    }

    @Override
    public void start(String uri, Consumer<String> onReceiveListener) throws IOException {

        String[] uriParts = uri.split(":");
        String ip = uriParts[0];
        int port = Integer.parseInt(uriParts[1]);

        this.clientSocket = new Socket(ip, port);
        this.out = new PrintWriter(clientSocket.getOutputStream(), true, charset);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), charset));
        this.onReceiveListener = onReceiveListener;
        new ReaderThread().start();
        if (onConnectionStatusChangeListener != null) {
            onConnectionStatusChangeListener.accept(getConnectionStatus());
        }
        LOG.info("Connection started for " + clientSocket.getRemoteSocketAddress());
    }

    class ReaderThread extends Thread {
        @Override
        public void run() {
            StringBuilder s = new StringBuilder();
            while (true) {
                int cc = -1;
                try {
                    cc = in.read();
                    if (cc < 0) {
                        break;
                    }
                } catch (SocketException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                char c = (char) cc;
                s.append(c);

                if (s.length() >= 2 && s.charAt(s.length() - 2) == HL7Utils.HL7LLPCharacters.FS.getCharacter() && s.charAt(s.length() - 1) == HL7Utils.HL7LLPCharacters.CR.getCharacter()) {
                    String textMessage = HL7Utils.llpToText(s.toString());
                    LOG.debug("Receiving LLP message from " + clientSocket.getRemoteSocketAddress() + ":\n" + textMessage);
                    onReceiveListener.accept(textMessage);
                    s.delete(0, s.length());
                }
            }
            LOG.info("Connection closed for " + clientSocket.getRemoteSocketAddress());
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (onConnectionStatusChangeListener != null) {
                onConnectionStatusChangeListener.accept(getConnectionStatus());
            }
        }
    }

    @Override
    public void stop() throws IOException {
        clientSocket.close();
        if (onConnectionStatusChangeListener != null) {
            onConnectionStatusChangeListener.accept(getConnectionStatus());
        }
    }

    @Override
    public void send(String textMessage) {

        if (!getConnectionStatus().isConnectionEnabled()) {
            throw new RuntimeException("Client is not connected");
        }
        LOG.debug("Sending LLP message to " + clientSocket.getRemoteSocketAddress() + ":\n" + textMessage);

        String llpMessage = HL7Utils.textToLlp(textMessage);
        for (char c : llpMessage.toCharArray()) {
            out.write(c);
        }
        out.flush();
    }
}
