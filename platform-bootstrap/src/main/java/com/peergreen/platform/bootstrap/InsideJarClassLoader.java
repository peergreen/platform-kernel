/**
 * Copyright 2012 Peergreen S.A.S.
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
package com.peergreen.platform.bootstrap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.Enumeration;


public class InsideJarClassLoader extends SecureClassLoader {

    private final EntriesRepository entriesRepository;
    private final URL rootJar;
    private final AccessControlContext accessControlContext;

    public InsideJarClassLoader(URL rootJar, ClassLoader parent) throws IOException, URISyntaxException {
        super(parent);
        this.entriesRepository = new EntriesRepository(rootJar);
        long t0 = System.currentTimeMillis();
        entriesRepository.scan();
        long tEnd = System.currentTimeMillis();
        System.out.println("diff = " + (tEnd - t0) + " ms");
        this.accessControlContext = AccessController.getContext();
        this.rootJar = rootJar;

    }


    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        try {
            return AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Class<?>>() {
                        public Class<?> run() throws ClassNotFoundException {
                            ByteEntry byteEntry;
                            try {
                                byteEntry = entriesRepository.readBytes(name);
                            } catch (IOException e) {
                                throw new ClassNotFoundException("Unable to find class", e);
                            }

                            if (byteEntry != null) {
                                //CodeSource cs = new CodeSource(rootJar, (CodeSigner[]) null);
                                try {
                                return defineClass(name, byteEntry.bytes, 0, byteEntry.bytes.length, byteEntry.codesource);
                                } finally {
                                    entriesRepository.removeEntry(name);
                                }
                            }
                            return null;
                        }
                    }, accessControlContext);
        } catch (PrivilegedActionException e) {
           throw new ClassNotFoundException("error", e);
        }

    }

    @Override
    protected URL findResource(String name) {
        return entriesRepository.getURL(name);

    }


    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        return entriesRepository.getURLs(name);
    }

}
