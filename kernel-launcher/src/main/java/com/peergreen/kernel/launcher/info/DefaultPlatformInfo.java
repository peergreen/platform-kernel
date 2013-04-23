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

package com.peergreen.kernel.launcher.info;

import java.util.Date;
import java.util.UUID;

import com.peergreen.kernel.info.JavaInfo;
import com.peergreen.kernel.info.PlatformInfo;

/**
 * Group platform-related information together.
 */
public class DefaultPlatformInfo implements PlatformInfo {

    private final UUID id;
    private final Date startDate;
    private final long startupTime;
    private final JavaInfo javaInfo;

    public DefaultPlatformInfo(long javaBoot, long platformReady) {
        this.id = UUID.randomUUID();
        this.javaInfo = new DefaultJavaInfo();
        this.startDate = new Date(javaBoot);
        this.startupTime = platformReady - javaBoot;
    }

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public long getStartupTime() {
        return startupTime;
    }

    @Override
    public long getUptime() {
        return System.currentTimeMillis() - startDate.getTime();
    }

    @Override
    public JavaInfo getJavaInfo() {
        return javaInfo;
    }
}
