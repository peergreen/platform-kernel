package org.peergreen.platform.info.node;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 14 sept. 2010
 * Time: 21:00:07
 * To change this template use File | Settings | File Templates.
 */
public interface NodeInfo {
    String getId();
    Collection<AccessPointInfo> getAccessPoints();
}
