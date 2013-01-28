package com.peergreen.platform.launcher.shell;

import com.peergreen.platform.event.Event;
import com.peergreen.platform.event.EventFilter;
import com.peergreen.platform.event.EventKeeper;
import com.peergreen.platform.info.JavaInfo;
import com.peergreen.platform.info.PlatformInfo;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 12/01/12
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
public class Infos {

    public static final String[] FUNCTIONS = {"platform", "runtime"};
    
    private PlatformInfo platformInfo;

    public Infos(PlatformInfo platformInfo) {
        this.platformInfo = platformInfo;
    }

    public void platform() {
        System.out.printf("Platform Information (%s)%n", platformInfo.getId());
        System.out.printf("* Started %tc%n", platformInfo.getStartDate());
        System.out.printf("* Boot time %s%n", Times.printDuration((double) platformInfo.getStartupTime()));
        System.out.printf("* Uptime %s%n", Times.printDuration((double) platformInfo.getUptime()));

    }

    public void runtime() {
        JavaInfo info = platformInfo.getJavaInfo();
        System.out.printf("Java %s%n", info.getSpecificationVersion().getText());
        System.out.printf("* Executable path: %s%n", info.getExecutable());
        System.out.printf("* Implementation version: %s%n", info.getImplementationVersion().getText());
    }

}
