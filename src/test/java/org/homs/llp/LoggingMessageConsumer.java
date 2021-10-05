package org.homs.llp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class LoggingMessageConsumer implements Consumer<String> {

    static final Logger LOG = LoggerFactory.getLogger(LoggingMessageConsumer.class);

    final String consumerName;

    public LoggingMessageConsumer(String consumerName) {
        this.consumerName = consumerName;
    }

    @Override
    public void accept(String msg) {
        LOG.info("[" + consumerName + "]: " + msg);
    }
}
