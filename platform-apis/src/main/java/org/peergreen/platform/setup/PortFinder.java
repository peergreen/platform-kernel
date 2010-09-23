package org.peergreen.platform.setup;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 14 sept. 2010
 * Time: 19:57:41
 * To change this template use File | Settings | File Templates.
 */
public interface PortFinder {
    int findFreePort(PortProvider provider);
}
