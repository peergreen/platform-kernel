package com.peergreen.platform.config;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 20:04:13
 * To change this template use File | Settings | File Templates.
 */
public interface ConfigurationInfo {
    String getFactoryPid();
    String getPid();
    boolean isFactory();
    Map<String, Object> getConfiguration();
}
