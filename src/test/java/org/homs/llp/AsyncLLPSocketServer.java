package org.homs.llp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * High-performance asynchronous LLP socket server.
 *
 * @author homscaum
 */
public class AsyncLLPSocketServer implements AsynchLLPSocketMock {

    static final Logger LOG = LoggerFactory.getLogger(AsyncLLPSocketServer.class);

    final Charset charset;

    int port;
    Consumer<EConnectionStatus> onConnectionStatusChangeListener;

    ServerSocket serverSocket;
    Socket clientSocket;
    PrintWriter out;

    public AsyncLLPSocketServer(Charset charset) {
        super();
        this.charset = charset;
    }

    public AsyncLLPSocketServer() {
        this(StandardCharsets.UTF_8);
    }

    @Override
    public void setConnectionStatusChangeListener(Consumer<EConnectionStatus> onConnectionStatusChangeListener) {
        this.onConnectionStatusChangeListener = onConnectionStatusChangeListener;
        onConnectionStatusChangeListener.accept(getConnectionStatus());
    }

    public EConnectionStatus getConnectionStatus() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            if (clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed()) {
                return EConnectionStatus.CONNECTED;
            } else {
                return EConnectionStatus.AWAITING_CONNECTION;
            }
        } else {
            return EConnectionStatus.DISCONNECTED;
        }
    }

    @Override
    public void start(String uri, Consumer<String> onReceiveListener) throws IOException {

        this.port = Integer.parseInt(uri);
        this.serverSocket = new ServerSocket(port);

        LOG.info("Running Socket server at port " + port);

        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        while (getConnectionStatus() == EConnectionStatus.CONNECTED) {
                            Thread.sleep(100L);
                        }
                        clientSocket = serverSocket.accept();
                        out = new PrintWriter(clientSocket.getOutputStream(), true, charset);
                        if (onConnectionStatusChangeListener != null) {
                            onConnectionStatusChangeListener.accept(getConnectionStatus());
                        }

                        Thread t2 = new Thread() {
                            @Override
                            public void run() {
                                try {

                                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), charset));

                                    int cc;
                                    StringBuilder s = new StringBuilder();
                                    while ((cc = in.read()) >= 0) {
                                        char c = (char) cc;
                                        s.append(c);

                                        if (s.length() >= 2 //
                                                && s.charAt(s.length() - 2) == HL7Utils.HL7LLPCharacters.FS.getCharacter() //
                                                && s.charAt(s.length() - 1) == HL7Utils.HL7LLPCharacters.CR.getCharacter()) {

                                            onReceiveListener.accept(HL7Utils.llpToText(s.toString()));
                                            s.setLength(0);
                                        }
                                    }

                                    in.close();
                                    out.close();
                                    clientSocket.close();
                                    if (onConnectionStatusChangeListener != null) {
                                        onConnectionStatusChangeListener.accept(getConnectionStatus());
                                    }
                                } catch (SocketException e) {
                                    //
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        };
                        t2.start();

                    } catch (SocketException e) {
                        //
                    } catch (Exception e) {
                        e.printStackTrace(); // TODO handle
                    }
                }
            }
        };
        t.start();
        if (onConnectionStatusChangeListener != null) {
            onConnectionStatusChangeListener.accept(getConnectionStatus());
        }
    }

    @Override
    public void stop() throws IOException {
        if (clientSocket != null && !clientSocket.isClosed()) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        serverSocket.close();
        if (onConnectionStatusChangeListener != null) {
            onConnectionStatusChangeListener.accept(getConnectionStatus());
        }
        LOG.info("Stopped Socket server at port " + port);
    }

    @Override
    public void send(String message) {

        if (clientSocket == null || !clientSocket.isConnected() || clientSocket.isClosed()) {
            throw new RuntimeException("no client is connected");
        }

        String llpMessage = HL7Utils.textToLlp(message);
        for (char c2 : llpMessage.toCharArray()) {
            out.write(c2);
        }
        out.flush();
    }

}