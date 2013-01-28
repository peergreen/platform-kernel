package com.peergreen.platform.launcher.event;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/01/13
 * Time: 18:38
 * To change this template use File | Settings | File Templates.
 */
public interface Constants {

    public static interface Java {
        String BEGIN = "java/begin";
    }

    public static interface Bootstrap {
        String SCAN_BEGIN = "bootstrap/scan/begin";
        String SCAN_END = "bootstrap/scan/end";
        String MAIN_INVOKE = "bootstrap/main/invoke";
    }

    public static interface Platform {
        String PLATFORM_PREPARE = "platform/prepare";
        String PLATFORM_READY = "platform/ready";
    }

    public static interface OSGi {
        String OSGI_INIT = "platform/osgi/init";
        String OSGI_START = "platform/osgi/start";
        String BUNDLES_INSTALL = "platform/bundles/install";
        String BUNDLES_START = "platform/bundles/start";
    }

    public static interface Properties {
        String NAMESPACE = "com.peergreen.platform.bootstrap:";
        String PROPERTY_BOOTSTRAP_BEGIN = NAMESPACE + "begin";
        String PROPERTY_BOOTSTRAP_SCAN_BEGIN = NAMESPACE + "scan.begin";
        String PROPERTY_BOOTSTRAP_SCAN_END = NAMESPACE + "scan.end";
        String PROPERTY_BOOTSTRAP_MAIN_INVOKE = NAMESPACE + "main.invoke";
    }
}
