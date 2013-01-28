package com.peergreen.platform.event;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/01/13
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
public interface EventFilter {
    boolean accept(Event event);

    public static final EventFilter ALL = new EventFilter() {
        @Override
        public boolean accept(Event event) {
            return true;
        }
    };
}
