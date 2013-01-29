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

package com.peergreen.kernel.launcher.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.peergreen.kernel.event.Event;
import com.peergreen.kernel.event.EventFilter;
import com.peergreen.kernel.event.EventKeeper;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/01/13
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public class DefaultEventKeeper implements EventKeeper {

    private final List<Event> events = new ArrayList<>();

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
