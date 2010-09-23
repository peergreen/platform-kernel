package org.peergreen.platform.launcher.info;

import org.peergreen.platform.info.JavaInfo;
import org.peergreen.platform.info.PlatformInfo;

import java.util.Date;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 20:41:45
 * To change this template use File | Settings | File Templates.
 */
public class DefaultPlatformInfo implements PlatformInfo {

    private UUID id;
    private Date startDate;
    private long startupTime;
    private JavaInfo javaInfo;

    public DefaultPlatformInfo() {
        this.id = UUID.randomUUID();
        this.startDate = new Date();
        this.javaInfo = new DefaultJavaInfo();
    }

    public String getId() {
        return id.toString();
    }

    public Date getStartDate() {
        return startDate;
    }

    public long getStartupTime() {
        return startupTime;
    }

    public void setStartupTime(long startupTime) {
        this.startupTime = startupTime;
    }

    public long getUptime() {
        return System.currentTimeMillis() - startDate.getTime();
    }

    public JavaInfo getJavaInfo() {
        return javaInfo;
    }
}
