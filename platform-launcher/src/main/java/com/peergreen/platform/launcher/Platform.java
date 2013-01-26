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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.peergreen.platform.launcher.report.Message;
import com.peergreen.platform.launcher.report.Reporter;
import com.peergreen.platform.launcher.report.Severity;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import com.peergreen.platform.info.PlatformInfo;
import com.peergreen.platform.launcher.info.DefaultPlatformInfo;
import com.peergreen.platform.launcher.scanner.BootstrapJarScanner;
import com.peergreen.platform.launcher.scanner.BundleDirectoryScanner;
import com.peergreen.platform.launcher.shell.Infos;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 14 sept. 2010
 * Time: 20:12:37
 * To change this template use File | Settings | File Templates.
 */
public class Platform {

    public static final Pattern STARTLEVEL_PATTERN = Pattern.compile(".*/bundles/(\\d+)/.*");

    private Framework framework;
    private final DefaultPlatformInfo info = new DefaultPlatformInfo();
    private BundleContext platformContext;
    private Reporter reporter = new Reporter();

    /**
     * Framework StartLevel value provided by the user;
     */
    private int userFrameworkStartLevel = -1;

    public static void main(String[] args) throws Exception {
        Platform platform = new Platform();
        platform.prepare();
        platform.start();
    }

    public void setUserFrameworkStartLevel(int userFrameworkStartLevel) {
        if (userFrameworkStartLevel < 1) {
            throw new IllegalArgumentException(
                    String.format("Framework StartLevel value has to be higher (or equal) than 1 (actual value:%d).",
                                  userFrameworkStartLevel)
            );
        }
        this.userFrameworkStartLevel = userFrameworkStartLevel;
    }

    private void prepare() throws Exception {
        // Create the framework instance
        FrameworkFactory factory = findFrameworkFactory();
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "org.w3c.dom.traversal,javax.transaction;version=1.1.0,javax.transaction.xa;version=1.1.0");
        // I need to force the Framework StartLevel here, we will move up the FSL after initialisation
        configuration.put(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, "1");

        framework = factory.newFramework(configuration);
    }

    private boolean isFirstBoot() {
        // No cache, initial boot (we only have the system bundle installed)
        return platformContext.getBundles().length == 1;
    }

    private File getUserBundlesDirectory() {
        File user = new File(System.getProperty("user.dir"));
        return new File(user, "bundles");
    }

    public void start() throws Exception {

        // Init the framework
        // After this point, persisted bundles are re-installed (but not started)
        framework.init();
        platformContext = framework.getBundleContext();

        // Start the framework (going into ACTIVE state)
        // Framework will move its StartLevel value up to 1
        // Persisted bundles may be started depending of their own persisted start level
        framework.start();

        // Avoid scanning for bundles for subsequent boots
        // They are already there because of the OSGi persistence
        if (isFirstBoot()) {
            // Find the bundles to be installed
            List<BundleScanner> scanners = new ArrayList<BundleScanner>();
            scanners.add(new BootstrapJarScanner());
            scanners.add(new BundleDirectoryScanner(getUserBundlesDirectory()));

            List<URL> resources = new ArrayList<>();
            for (BundleScanner scanner : scanners) {
                resources.addAll(scanner.scan());
            }

            // Install any discovered bundles
            Collection<Bundle> bundles = installBundles(resources);

            // Start the installed bundles
            startBundles(bundles);

        }

        // Register a FrameworkListener to be notified of Bundles
        // failures when we'll set the framework StartLevel
        platformContext.addFrameworkListener(new FrameworkListener() {
            @Override
            public void frameworkEvent(FrameworkEvent event) {
                if (FrameworkEvent.ERROR == event.getType()) {
                    reporter.addMessage(new Message(Severity.ERROR, event.getThrowable(), event.getBundle()));
                }
            }
        });

        setFrameworkStartLevel();

        // Wait for the framework to stop indefinitely
        framework.waitForStop(0);
    }

    private Collection<Bundle> installBundles(List<URL> resources) throws BundleException {

        Collection<Bundle> bundles = new ArrayList<Bundle>();
        for (URL resource : resources) {

            String location = resource.toString();
            Bundle bundle = null;
            try {
                bundle = platformContext.installBundle(location);
            } catch (BundleException e) {
                if (BundleException.DUPLICATE_BUNDLE_ERROR == e.getType()) {
                    reporter.addMessage(new Message(Severity.WARNING, e));
                    continue;
                } else {
                    reporter.addMessage(new Message(Severity.ERROR, e));
                    continue;
                }
            }

            // Do not store fragment bundles (they can't be installed)
            if (!isFragment(bundle)) {

                // Set the appropriate Bundle StartLevel (if required)
                int level = getBundleStartLevel(location);
                if (level != -1) {
                    BundleStartLevel bsl = bundle.adapt(BundleStartLevel.class);
                    bsl.setStartLevel(level);
                }
                bundles.add(bundle);
            }
        }
        return bundles;
    }

    private void startBundles(Collection<Bundle> bundles) {

        // Start the installed bundles, respecting the activation policy if needed
        for (Bundle bundle : bundles) {
            try {
                bundle.start(Bundle.START_ACTIVATION_POLICY);
            } catch (BundleException e) {
                // Should not happen now since the framework start level is not high enough to start this bundle
                reporter.addMessage(new Message(Severity.ERROR, e, bundle));
            }
        }
    }

    private int getBundleStartLevel(String location) {
        Matcher matcher = STARTLEVEL_PATTERN.matcher(location);
        if (matcher.matches()) {
            String level = matcher.group(1);
            return Integer.valueOf(level);
        }
        return -1;
    }

    private void setFrameworkStartLevel() {
        // Framework StartLevel is not persisted, so we need to increase it manually
        FrameworkStartLevel fsl = framework.adapt(FrameworkStartLevel.class);
        int frameworkLevel = getFrameworkStartLevel();
        fsl.setStartLevel(frameworkLevel, new StartupFrameworkListener());
    }

    private int getFrameworkStartLevel() {
        return (userFrameworkStartLevel != -1) ? userFrameworkStartLevel : getHighestBundleStartLevel();
    }

    private int getHighestBundleStartLevel() {
        int max = 0;

        for (Bundle bundle : platformContext.getBundles()) {
            BundleStartLevel bsl = bundle.adapt(BundleStartLevel.class);
            // Store the max
            max = Math.max(max, bsl.getStartLevel());
        }

        return max;
    }

    private static boolean isFragment(Bundle bundle) {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }

    private static FrameworkFactory findFrameworkFactory() throws Exception {
        return new FactoryFinder().find();
    }

    private void terminateStartup() {

        // Register our services
        long startTime = info.getStartDate().getTime();
        info.setStartupTime(System.currentTimeMillis() - startTime);
        platformContext.registerService(PlatformInfo.class.getName(),
                info,
                null);

        // Register platform related commands
        Dictionary<String, Object> dict = new Hashtable<String, Object>();
        dict.put("osgi.command.scope", "info");
        dict.put("osgi.command.function", Infos.FUNCTIONS);

        platformContext.registerService(Infos.class.getName(), new Infos(info), dict);

        printSuccessfulStartup();
    }

    private void printSuccessfulStartup() {
        FrameworkStartLevel fsl = framework.adapt(FrameworkStartLevel.class);
        System.out.printf("Peergreen Kernel started in %s (Bundles:%d, StartLevel:%d).%n",
                Infos.printDuration(info.getStartupTime()),
                platformContext.getBundles().length,
                fsl.getStartLevel());
        List<Message> warnings = reporter.getWarnings();
        if (!warnings.isEmpty()) {
            for (Message warning : warnings) {
                System.out.printf("  * %s%n", warning.toString());
            }
        }
    }

    private void printFailedStartup() {
        System.out.printf("Peergreen Kernel started with error(s) (details below).%n");
        List<Message> warnings = reporter.getWarnings();
        if (!warnings.isEmpty()) {
            for (Message warning : warnings) {
                System.out.printf("  * %s%n", warning.toString());
            }
        }
        List<Message> errors = reporter.getErrors();
        if (!errors.isEmpty()) {
            for (Message error : errors) {
                System.out.printf("  * %s%n", error.toString());
            }
        }
    }

    private class StartupFrameworkListener implements FrameworkListener {
        @Override
        public void frameworkEvent(FrameworkEvent event) {
            switch (event.getType()) {
                case FrameworkEvent.ERROR:
                    // Could not reach the expected start level because of some error
                    reporter.addMessage(new Message(Severity.ERROR, event.getThrowable(), event.getBundle()));
                    break;
                case FrameworkEvent.STARTLEVEL_CHANGED:
                    if (reporter.getErrors().isEmpty()) {
                        terminateStartup();
                    } else {
                        printFailedStartup();
                    }
            }
        }
    }
}
