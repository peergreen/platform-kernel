package com.peergreen.platform.info;

import java.io.File;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 20:25:44
 * To change this template use File | Settings | File Templates.
 */
public interface JavaInfo {
    File getExecutable();
    VersionInfo getSpecificationVersion();
    VersionInfo getImplementationVersion();
    Properties getSystemProperties();
}
