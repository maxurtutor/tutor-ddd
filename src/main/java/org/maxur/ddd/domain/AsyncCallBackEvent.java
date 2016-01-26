package org.maxur.ddd.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.01.2016</pre>
 */
abstract class AsyncCallBackEvent<T> {

    private Logger logger = LoggerFactory.getLogger(AsyncCallBackEvent.class);

    private Optional<T> response = null;

    private CountDownLatch latch = new CountDownLatch(1);

    public Optional<T> response(long timeoutMS) {
        try {
            latch.await(timeoutMS, MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("Error on AsyncCallBackEvent.response()", e);
        }
        return response;
    }

    public void setResponse(Optional<T> response) {
        this.response = response;
        latch.countDown();
    }
}
