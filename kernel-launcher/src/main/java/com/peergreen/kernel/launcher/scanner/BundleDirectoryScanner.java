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
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.peergreen.kernel.launcher.BundleScanner;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 22 sept. 2010
 * Time: 21:54:35
 * To change this template use File | Settings | File Templates.
 */
public class BundleDirectoryScanner implements BundleScanner {

    /**
     * Browsed directory.
     */
    private final File directory;

    public BundleDirectoryScanner(File directory) {
        this.directory = directory;
    }

    @Override
    public Collection<URL> scan() {
        if (!directory.isDirectory()) {
            return Collections.emptyList();
        }

        Collection<URL> bundles = new ArrayList<URL>();

        for (File child : directory.listFiles(new JarFileFilter())) {
            try {
                bundles.add(child.toURI().toURL());
            } catch (MalformedURLException e) {
                // TODO prints a warning
            }
        }

        return bundles;
    }

    private class JarFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            return file.getName().endsWith(".jar");
        }
    }
}
