package com.peergreen.platform.launcher.shell;

import com.peergreen.platform.event.Event;
import com.peergreen.platform.event.EventFilter;
import com.peergreen.platform.event.EventKeeper;
import com.peergreen.platform.launcher.event.Constants;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/01/13
 * Time: 18:32
 * To change this template use File | Settings | File Templates.
 */
public class Events {
    public static final String[] FUNCTIONS = {"event", "events", "elapsed"};
    public static final String EVENT_HEADER_FORMAT = "%-15s   %-30s   %s%n";
    public static final String EVENT_FORMAT = "%-15d | %-30s | %s%n";

    private EventKeeper eventKeeper;

    public Events(EventKeeper eventKeeper) {
        this.eventKeeper = eventKeeper;
    }

    public void events() {
        events(".*");
    }

    public void events(String pattern) {
        Pattern p = Pattern.compile(pattern);
        System.out.printf(EVENT_HEADER_FORMAT, "Timestamp", "Id", "Message");
        for (Event event : eventKeeper.getEvents(new PatternEventFilter(p))) {
            System.out.printf(EVENT_FORMAT, event.getTimestamp(), event.getId(), event.getMessage());
        }
    }

    public void elapsed(String to) {
        Event one = event(Constants.Java.BEGIN);
        Event two = event(to);
        elapsed(one, two);
    }

    public Event event(String id) {
        Collection<Event> candidates = eventKeeper.getEvents(new PatternEventFilter(id));
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("Cannot find Event with id '" + id + "'");
        }
        return candidates.iterator().next();
    }

    public void elapsed(String from, String to) {
        Event one = event(from);
        Event two = event(to);
        elapsed(one, two);
    }

    public void elapsed(Event from, Event to) {
        System.out.printf("%-20s | %s -> %s%n",
                Times.printDuration(to.getTimestamp() - from.getTimestamp()),
                from.getMessage(),
                to.getMessage());
    }

    private class PatternEventFilter implements EventFilter {
        private Pattern pattern;

        public PatternEventFilter(String p) {
            this(Pattern.compile(p));
        }
        public PatternEventFilter(Pattern p) {
            pattern = p;
        }

        @Override
        public boolean accept(Event event) {
            return pattern.matcher(event.getId()).matches();
        }
    }

}
