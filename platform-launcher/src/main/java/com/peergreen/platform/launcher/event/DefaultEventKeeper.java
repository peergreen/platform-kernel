package com.peergreen.platform.launcher.event;

import com.peergreen.platform.event.Event;
import com.peergreen.platform.event.EventFilter;
import com.peergreen.platform.event.EventKeeper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/01/13
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public class DefaultEventKeeper implements EventKeeper {

    private List<Event> events = new ArrayList<>();

    @Override
    public void logEvent(Event event) {
        events.add(event);
    }

    @Override
    public Collection<Event> getEvents() {
        return getEvents(EventFilter.ALL);
    }

    @Override
    public Collection<Event> getEvents(EventFilter filter) {
        List<Event> filtered = new ArrayList<>();
        for (Event event : events) {
            if (filter.accept(event)) {
                filtered.add(event);
            }
        }
        return filtered;
    }


}
