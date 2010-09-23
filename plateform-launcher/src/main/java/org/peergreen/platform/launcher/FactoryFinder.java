package org.peergreen.platform.launcher;

import org.osgi.framework.launch.FrameworkFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static java.lang.ClassLoader.*;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 21:40:45
 * To change this template use File | Settings | File Templates.
 */
class FactoryFinder {

    private final static String RESOURCE = "META-INF/services/" + FrameworkFactory.class.getName();

    public FrameworkFactory find() throws Exception {
        Enumeration<URL> resources = getSystemClassLoader().getResources(RESOURCE);
        List<URL> urls = Collections.list(resources);
        if (urls.isEmpty()) {
            throw new Exception("No FrameworkFactory found in System ClassLoader");
        }

        for (URL resource : urls) {
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.openStream()));
            try {
                for (String s = br.readLine(); s != null; s = br.readLine()) {
                    s = s.trim();
                    // Try to load first non-empty, non-commented line.
                    if ((s.length() > 0) && (s.charAt(0) != '#')) {
                        return Class.forName(s).asSubclass(FrameworkFactory.class).newInstance();
                    }
                }
            } finally {
                if (br != null) {
                    br.close();
                }
            }

        }
        return null;
    }
}
