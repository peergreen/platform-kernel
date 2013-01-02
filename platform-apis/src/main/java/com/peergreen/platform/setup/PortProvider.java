package com.peergreen.platform.setup;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 20:13:20
 * To change this template use File | Settings | File Templates.
 */
public interface PortProvider {
    Collection<Integer> getPorts();
}
