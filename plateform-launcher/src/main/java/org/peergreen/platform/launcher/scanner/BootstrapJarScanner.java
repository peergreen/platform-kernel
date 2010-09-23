package org.peergreen.platform.launcher.scanner;

import org.peergreen.platform.launcher.BundleScanner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 22 sept. 2010
 * Time: 21:43:20
 * To change this template use File | Settings | File Templates.
 */
public class BootstrapJarScanner implements BundleScanner {
    public Collection<URL> scan() {
        String classpath = System.getProperty("java.class.path");
        String separator = File.pathSeparator;
        String[] paths = classpath.split(separator);
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
                        if (entry.getName().endsWith(".jar")) {
                            URL fileUrl = file.toURI().toURL();
                            URL resource = new URL("jar:" + fileUrl.toString() + "!/" + entry.getName());
                            bundles.add(resource);
                        }
                    }
                } catch (IOException e) {
                    // TODO print a warning
                }
            }
        }

        return bundles;
    }
}
