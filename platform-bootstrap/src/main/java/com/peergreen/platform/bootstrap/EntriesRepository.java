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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EntriesRepository {

    private final URL rootURL;
    private final Map<String, List<URL>> urlEntries;
    private final Map<String, ByteEntry> buffers;


    public EntriesRepository(final URL rootURL) {
        this.rootURL = rootURL;
        this.urlEntries = new HashMap<String, List<URL>>();
        this.buffers = new HashMap<String, ByteEntry>();
    }

    //FIXME : no close of resources
    public void scan() throws IOException, URISyntaxException {
        // It's a jar file so scan entries
        JarFile jarFile = new JarFile(rootURL.toURI().getPath());

        // We search jar in jars
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.getName().startsWith("lib")) {
                continue;
            }

            // we have inner jars
            if (!entry.getName().endsWith(".jar")) {
                continue;
            }

            // Use Zip and not Jar else it skip MANIFEST.MF entry
            ZipInputStream jarInputStream = new ZipInputStream(jarFile.getInputStream(entry));
            ZipEntry jarEntry = jarInputStream.getNextEntry();
            while (jarEntry != null) {
                String name = jarEntry.getName();
                URL url = getURL(rootURL, entry.getName(), jarEntry.getName());

                List<URL> currentList = urlEntries.get(name);
                if (currentList == null) {
                    currentList = new ArrayList<URL>();
                    urlEntries.put(name, currentList);
                }
                currentList.add(url);
                if (name.endsWith(".class")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] b = new byte[4096];

                    int len;
                    while ((len = jarInputStream.read(b, 0, b.length)) != -1) {
                        baos.write(b, 0, len);
                    }
                    byte[] bytes = baos.toByteArray();
                    String entryName = name.substring(0, name.length() - 6).replace("/", ".");

                    ByteEntry byteEntry = new ByteEntry();
                    byteEntry.bytes = bytes;
                    URL jarURL = new URL("jar:" + rootURL.toExternalForm() + "!/" + entry.getName());
                    byteEntry.codesource = new CodeSource(jarURL, (CodeSigner[]) null);

                    buffers.put(entryName, byteEntry);
                }
                jarEntry = jarInputStream.getNextEntry();

            }





        }


    }

    public void removeEntry(String name) {
        buffers.remove(name);
    }

    public ByteEntry readBytes(String name) throws IOException {
        ByteEntry byteEntry = buffers.get(name);
        if (byteEntry != null) {
            return byteEntry;
        }

        String resourceName = name.replace(".", "/").concat(".class");

        List<URL> urls = urlEntries.get(resourceName);
        if (urls == null || urls.size() == 0) {
            return null;
        }
        URL url = urls.get(0);
        URLConnection connection = url.openConnection();
        JarInJarURLConnection jarInJarURLConnection = (JarInJarURLConnection) connection;

        return jarInJarURLConnection.readBytes();
    }

    public URL getURL(String name) {
        List<URL> urls = urlEntries.get(name);
        if (urls == null || urls.size() == 0) {
            return null;
        }
        return urls.get(0);

    }

    public Enumeration<URL> getURLs(String name) {
        List<URL> urls = urlEntries.get(name);
        if (urls == null || urls.size() == 0) {
            return Collections.emptyEnumeration();
        }
        return Collections.enumeration(urls);

    }



    protected URL getURL(URL rootURL, String jar, String name) throws MalformedURLException {
        URL url =  new URL("jarinjar:/" + rootURL.getPath() + "!"  + jar + "!" + name);
        return url;
    }

}
