package org.peergreen.platform.launcher.scanner;

import org.peergreen.platform.launcher.BundleScanner;

import java.io.File;
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
    public Collection<URL> scan() {
        File base = new File(System.getProperty("user.dir"));
        File bundlesDir = new File(base, "bundles");
        if (!bundlesDir.isDirectory()) {
            return Collections.emptyList();
        }

        Collection<URL> bundles = new ArrayList<URL>();

        for (File child : bundlesDir.listFiles()) {
            if (child.getName().endsWith(".jar")) {
                try {
                    bundles.add(child.toURI().toURL());
                } catch (MalformedURLException e) {
                    // TODO prints a warning
                }
            }
        }

        return bundles;
    }
}
