package org.peergreen.platform.info;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 20:24:01
 * To change this template use File | Settings | File Templates.
 */
public interface PlatformInfo {
    String getId();
    Date getStartDate();
    long getStartupTime();
    long getUptime();
    JavaInfo getJavaInfo();
}
