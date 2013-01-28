package com.peergreen.platform.launcher.event;

import com.peergreen.platform.event.Event;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/01/13
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
public class DefaultEvent implements Event {
    private final String id;
    private final String message;
    private final long timestamp;

    public DefaultEvent(String id, long timestamp) {
        this(id, timestamp, null);
    }

    public DefaultEvent(String id, long timestamp, String message) {
        this.id = id;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
