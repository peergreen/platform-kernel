package org.peergreen.platform.launcher;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.peergreen.platform.info.PlatformInfo;
import org.peergreen.platform.launcher.info.DefaultPlatformInfo;
import org.peergreen.platform.launcher.scanner.BootstrapJarScanner;
import org.peergreen.platform.launcher.scanner.BundleDirectoryScanner;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 14 sept. 2010
 * Time: 20:12:37
 * To change this template use File | Settings | File Templates.
 */
public class Platform {

    private Framework framework;
    private Collection<URL> installables = new ArrayList<URL>();
    private Collection<Bundle> bundles = new ArrayList<Bundle>();
    private DefaultPlatformInfo info = new DefaultPlatformInfo();
    private BundleContext platformContext;

    public static void main(String[] args) throws Exception {
        Platform platform = new Platform();
        platform.prepare();
        platform.start();
    }

    private void prepare() throws Exception {
        // Create the framework instance
        FrameworkFactory factory = findFrameworkFactory();
        framework = factory.newFramework(null);
        framework.init();

        // Find the bundles to be installed
        List<BundleScanner> scanners = new ArrayList<BundleScanner>();
        scanners.add(new BootstrapJarScanner());
        scanners.add(new BundleDirectoryScanner());

        for (BundleScanner scanner : scanners) {
            installables.addAll(scanner.scan());
        }
        
    }

    public void start() throws Exception {

        platformContext = framework.getBundleContext();

        // Install any discovered bundles
        for (URL resource : installables) {
            Bundle bundle = platformContext.installBundle(resource.toString());
            bundles.add(bundle);
        }

        // Start the framework (going into ACTIVE state)
        framework.start();
        
        // Start the installed bundles
        for (Bundle bundle : bundles) {
            bundle.start();
        }

        // TODO register our services
        long startTime = info.getStartDate().getTime();
        info.setStartupTime(System.currentTimeMillis() - startTime);
        platformContext.registerService(PlatformInfo.class.getName(),
                                        info,
                                        null);


        // Wait for the framework stop indefinitely
        framework.waitForStop(0);
    }

    private FrameworkFactory findFrameworkFactory() throws Exception {
        return new FactoryFinder().find();
    }
}
