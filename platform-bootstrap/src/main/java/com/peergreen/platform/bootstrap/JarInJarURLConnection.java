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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class JarInJarURLConnection extends URLConnection {

    private final String firstJarName;
    private final String subJarName;
    private final String subjarEntryName;
    private InputStream is;
    private long length = -1;

    protected JarInJarURLConnection(URL url) {
        super(url);

        String urlString = "/".concat(url.getHost()).concat(url.getPath());

        // parse url
        // First jar
        int firstJarIndex = urlString.indexOf("!");
        firstJarName = urlString.substring(0, firstJarIndex);

        // subjar
        int embeddedEntry = urlString.lastIndexOf("!");
        subJarName = urlString.substring(firstJarIndex + 1, embeddedEntry);

        // entry in subjar
        subjarEntryName = urlString.substring(embeddedEntry + 1);



    }

    @Override
    public void connect() throws IOException {
        if (!connected) {
            //Open jar
            JarFile jarFile = new JarFile(firstJarName);

            ZipEntry entry = jarFile.getEntry(subJarName);

            // read jar input stream
            ZipInputStream jarInputStream = new ZipInputStream(jarFile.getInputStream(entry));
            ZipEntry foundEntry = jarInputStream.getNextEntry();
            boolean found = false;
            while (foundEntry != null && !found) {
                if (subjarEntryName.equals(foundEntry.getName())) {
                    found = true;
                    break;
                }
                foundEntry = jarInputStream.getNextEntry();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[4096];
            if (found) {
                int len;
                while ((len = jarInputStream.read(b, 0, b.length)) != -1) {
                    baos.write(b, 0, len);
                }
                is = new ByteArrayInputStream(baos.toByteArray());
                this.length = foundEntry.getSize();
            }

            jarInputStream.close();
            jarFile.close();
            if (!found) {
                throw new IOException("Unable to find entry " + url + "");
            }

        }

    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!connected) {
            connect();
        }

        return is;
    }




    public ByteEntry readBytes() throws IOException {
        //Open jar
        JarFile jarFile = new JarFile(firstJarName);

        ZipEntry entry = jarFile.getEntry(subJarName);

        // read jar input stream
        ZipInputStream jarInputStream = new ZipInputStream(jarFile.getInputStream(entry));
        ZipEntry foundEntry = jarInputStream.getNextEntry();
        boolean found = false;
        while (foundEntry != null && !found) {
            if (subjarEntryName.equals(foundEntry.getName())) {
                found = true;
                break;
            }
            foundEntry = jarInputStream.getNextEntry();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        if (found) {
            int len;
            while ((len = jarInputStream.read(b, 0, b.length)) != -1) {
                baos.write(b, 0, len);
            }
            ByteEntry byteEntry = new ByteEntry();
            byteEntry.bytes = baos.toByteArray();
            return byteEntry;
        }

        jarInputStream.close();
        jarFile.close();
        if (!found) {
            return null;
        }
        return null;

    }



    @Override
    public long getContentLengthLong() {
        return length;
    }
}
