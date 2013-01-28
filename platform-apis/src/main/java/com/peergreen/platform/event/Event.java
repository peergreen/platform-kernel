package com.peergreen.platform.event;

import javax.print.attribute.standard.Severity;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/01/13
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public interface Event {
    String getId();
    String getMessage();
    long getTimestamp();
}
