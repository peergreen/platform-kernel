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

import java.io.File;
import java.util.Properties;

import com.peergreen.kernel.info.JavaInfo;
import com.peergreen.kernel.info.VersionInfo;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 20:48:43
 * To change this template use File | Settings | File Templates.
 */
public class DefaultJavaInfo implements JavaInfo {

    private File executable;

    private VersionInfo specification;

    private VersionInfo implementation;

    @Override
    public File getExecutable() {
        if (executable == null) {
            executable = findExecutable();
        }
        return executable;
    }

    private File findExecutable() {
        String home = System.getProperty("java.home");
        File javaBin = new File(home, "bin");
        return new File(javaBin, "java");
    }

    @Override
    public VersionInfo getSpecificationVersion() {
        if (specification == null) {
            specification = findSpecificationVersion();
        }
        return specification;
    }

    private VersionInfo findSpecificationVersion() {
        String def = System.getProperty("java.specification.version");
        String pack = System.class.getPackage().getSpecificationVersion();
        String version = choose(pack, def);
        return new DefaultVersionInfo(version);
    }

    private String choose(String first, String second) {
        if (first != null) {
            return first;
        } else {
            return second;
        }
    }

    @Override
    public VersionInfo getImplementationVersion() {
        if (implementation == null) {
            implementation = findImplementationVersion();
        }
        return implementation;
    }

    private VersionInfo findImplementationVersion() {
        String def = System.getProperty("java.implementation.version");
        String pack = System.class.getPackage().getImplementationVersion();
        String version = choose(pack, def);
        return new DefaultVersionInfo(version);
    }

    @Override
    public Properties getSystemProperties() {
        return (Properties) System.getProperties().clone();
    }
}
