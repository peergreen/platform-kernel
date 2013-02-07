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

package com.peergreen.kernel.launcher.scanner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.peergreen.kernel.launcher.BundleScanner;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 22 sept. 2010
 * Time: 21:43:20
 * To change this template use File | Settings | File Templates.
 */
public class BootstrapJarScanner implements BundleScanner {
    @Override
    public Collection<URL> scan() {
        String classpath = System.getProperty("java.class.path");
        String[] paths = classpath.split(File.pathSeparator);
        Collection<URL> bundles = new ArrayList<URL>();

        for (String path : paths) {
            if (path.endsWith(".jar")) {
                // Only handle elements of the path that are jar files
                File file = new File(path);
                JarFile jarFile = null;
                try {
                    jarFile = new JarFile(file);
                    List<JarEntry> entries = Collections.list(jarFile.entries());
                    for (JarEntry entry : entries) {
                        if (entry.getName().startsWith("bundles/") && (entry.getName().endsWith(".jar") || entry.getName().endsWith(".pack.gz"))) {
                            URL fileUrl = file.toURI().toURL();
                            URL resource = new URL("jar:" + fileUrl.toString() + "!/" + entry.getName());
                            bundles.add(resource);
                        }
                    }
                    jarFile.close();
                } catch (IOException e) {
                    // TODO print a warning
                }
            }
        }

        return bundles;
    }
}
