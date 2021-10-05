package org.homs.llp;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Common interface for the LLP client & server mocks
 *
 * @author homscaum
 */
public interface AsynchLLPSocketMock {

    enum EConnectionStatus {
        DISCONNECTED, AWAITING_CONNECTION, CONNECTED;

        public boolean isConnectionEnabled() {
            return equals(CONNECTED) || equals(AWAITING_CONNECTION);
        }
    }

    void setConnectionStatusChangeListener(Consumer<EConnectionStatus> consumer);

    EConnectionStatus getConnectionStatus();

    void start(String uri, Consumer<String> onReceiveListener) throws IOException;

    void stop() throws IOException;

    void send(String message);
}
