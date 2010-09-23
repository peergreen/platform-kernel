package org.peergreen.platform.config;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 20:02:54
 * To change this template use File | Settings | File Templates.
 */
public interface ConfigurationProvider {
    Collection<ConfigurationInfo> getConfigurations();
}
