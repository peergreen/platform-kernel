package org.peergreen.platform.launcher.scanner;

import org.peergreen.platform.launcher.BundleScanner;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
    private File directory;

    public BundleDirectoryScanner(File directory) {
        this.directory = directory;
    }

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

        public boolean accept(File file) {
            return file.getName().endsWith(".jar");
        }
    }
}
