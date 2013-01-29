/**
 * Copyright 2012-2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.peergreen.kernel.launcher.event;

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
