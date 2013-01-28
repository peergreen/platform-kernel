package com.peergreen.platform.event;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/01/13
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public interface EventKeeper {
    void logEvent(Event event);
    Collection<Event> getEvents();
    Collection<Event> getEvents(EventFilter filter);
}
