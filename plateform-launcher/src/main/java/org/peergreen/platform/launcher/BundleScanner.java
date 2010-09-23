package org.peergreen.platform.launcher;

import java.net.URL;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 22 sept. 2010
 * Time: 21:36:14
 * To change this template use File | Settings | File Templates.
 */
public interface BundleScanner {
    Collection<URL> scan();
}
