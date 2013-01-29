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

package com.peergreen.kernel.launcher.shell;

import com.peergreen.kernel.info.JavaInfo;
import com.peergreen.kernel.info.PlatformInfo;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 12/01/12
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
public class Infos {

    public static final String[] FUNCTIONS = {"platform", "runtime"};

    private final PlatformInfo platformInfo;

    public Infos(PlatformInfo platformInfo) {
        this.platformInfo = platformInfo;
    }

    public void platform() {
        System.out.printf("Platform Information (%s)%n", platformInfo.getId());
        System.out.printf("* Started %tc%n", platformInfo.getStartDate());
        System.out.printf("* Boot time %s%n", Times.printDuration(platformInfo.getStartupTime()));
        System.out.printf("* Uptime %s%n", Times.printDuration(platformInfo.getUptime()));

    }

    public void runtime() {
        JavaInfo info = platformInfo.getJavaInfo();
        System.out.printf("Java %s%n", info.getSpecificationVersion().getText());
        System.out.printf("* Executable path: %s%n", info.getExecutable());
        System.out.printf("* Implementation version: %s%n", info.getImplementationVersion().getText());
    }

}
