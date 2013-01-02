package com.peergreen.platform.launcher;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import com.peergreen.platform.info.PlatformInfo;
import com.peergreen.platform.launcher.info.DefaultPlatformInfo;
import com.peergreen.platform.launcher.scanner.BootstrapJarScanner;
import com.peergreen.platform.launcher.scanner.BundleDirectoryScanner;
import com.peergreen.platform.launcher.shell.Infos;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 14 sept. 2010
 * Time: 20:12:37
 * To change this template use File | Settings | File Templates.
 */
public class Platform {

    private Framework framework;
    private final Collection<URL> installables = new ArrayList<URL>();
    private final Collection<Bundle> bundles = new ArrayList<Bundle>();
    private final DefaultPlatformInfo info = new DefaultPlatformInfo();
    private BundleContext platformContext;

    public static void main(String[] args) throws Exception {
        Platform platform = new Platform();
        platform.prepare();
        platform.start();
    }

    private void prepare() throws Exception {
        // Create the framework instance
        FrameworkFactory factory = findFrameworkFactory();
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "org.w3c.dom.traversal,javax.transaction;version=1.1.0,javax.transaction.xa;version=1.1.0");

        framework = factory.newFramework(configuration);
        framework.init();

        // Find the bundles to be installed
        List<BundleScanner> scanners = new ArrayList<BundleScanner>();
        scanners.add(new BootstrapJarScanner());
        scanners.add(new BundleDirectoryScanner(getUserBundlesDirectory()));

        for (BundleScanner scanner : scanners) {
            installables.addAll(scanner.scan());
        }

    }

    private File getUserBundlesDirectory() {
        File user = new File(System.getProperty("user.dir"));
        return new File(user, "bundles");
    }

    public void start() throws Exception {

        platformContext = framework.getBundleContext();

        // Start the framework (going into ACTIVE state)
        framework.start();

        // Install any discovered bundles
        for (URL resource : installables) {
            Bundle bundle = platformContext.installBundle(resource.toString());
            bundles.add(bundle);
        }

        // Start the installed bundles, respecting the activation policy if needed
        for (Bundle bundle : bundles) {
            bundle.start(Bundle.START_ACTIVATION_POLICY);
        }

        // TODO register our services
        long startTime = info.getStartDate().getTime();
        info.setStartupTime(System.currentTimeMillis() - startTime);
        platformContext.registerService(PlatformInfo.class.getName(),
                                        info,
                                        null);

        registerShell(info);


        // Wait for the framework stop indefinitely
        framework.waitForStop(0);
    }

    private void registerShell(PlatformInfo info) {
        Dictionary<String, Object> dict = new Hashtable<String, Object>();
        dict.put("osgi.command.scope", "info");
        dict.put("osgi.command.function", Infos.FUNCTIONS);

        platformContext.registerService(Infos.class.getName(), new Infos(info), dict);
    }

    private FrameworkFactory findFrameworkFactory() throws Exception {
        return new FactoryFinder().find();
    }
}
