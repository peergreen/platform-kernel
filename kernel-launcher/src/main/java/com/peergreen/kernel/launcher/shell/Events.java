/**
 * Copyright 2012-2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.peergreen.kernel.launcher.shell;

import java.util.Collection;
import java.util.regex.Pattern;

import com.peergreen.kernel.event.Event;
import com.peergreen.kernel.event.EventFilter;
import com.peergreen.kernel.event.EventKeeper;
import com.peergreen.kernel.launcher.event.Constants;

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

    private final EventKeeper eventKeeper;

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
        private final Pattern pattern;

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
