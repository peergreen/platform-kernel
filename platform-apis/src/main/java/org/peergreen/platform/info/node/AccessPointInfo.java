package org.peergreen.platform.info.node;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 20:09:03
 * To change this template use File | Settings | File Templates.
 */
public interface AccessPointInfo {
    String getProtocol();
    String getHostname();
    int getPort();
}
