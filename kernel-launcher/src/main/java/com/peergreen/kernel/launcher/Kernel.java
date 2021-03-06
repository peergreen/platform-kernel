/**
 * Copyright 2012-2013 Peergreen S.A.S.
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

package com.peergreen.kernel.launcher;

import static com.peergreen.kernel.launcher.event.Constants.Bootstrap.MAIN_INVOKE;
import static com.peergreen.kernel.launcher.event.Constants.Bootstrap.SCAN_BEGIN;
import static com.peergreen.kernel.launcher.event.Constants.Bootstrap.SCAN_END;
import static com.peergreen.kernel.launcher.event.Constants.Java.BEGIN;
import static com.peergreen.kernel.launcher.event.Constants.Java.BOOT;
import static com.peergreen.kernel.launcher.event.Constants.OSGi.BUNDLES_INSTALL;
import static com.peergreen.kernel.launcher.event.Constants.OSGi.BUNDLES_START;
import static com.peergreen.kernel.launcher.event.Constants.OSGi.OSGI_INIT;
import static com.peergreen.kernel.launcher.event.Constants.OSGi.OSGI_START;
import static com.peergreen.kernel.launcher.event.Constants.Platform.PLATFORM_PREPARE;
import static com.peergreen.kernel.launcher.event.Constants.Platform.PLATFORM_READY;
import static com.peergreen.kernel.launcher.event.Constants.Properties.PROPERTY_BOOTSTRAP_BEGIN;
import static com.peergreen.kernel.system.StreamType.ERR;
import static com.peergreen.kernel.system.StreamType.OUT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import javax.management.MBeanServer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.service.log.LogListener;
import org.ow2.shelbie.core.branding.BrandingService;
import org.ow2.shelbie.core.identity.IdentityProvider;
import org.ow2.shelbie.core.prompt.PromptService;
import org.ow2.shelbie.core.system.SystemService;

import com.peergreen.kernel.event.Event;
import com.peergreen.kernel.event.EventFilter;
import com.peergreen.kernel.event.EventKeeper;
import com.peergreen.kernel.info.PlatformInfo;
import com.peergreen.kernel.launcher.branding.PeergreenBrandingService;
import com.peergreen.kernel.launcher.event.Constants;
import com.peergreen.kernel.launcher.event.DefaultEvent;
import com.peergreen.kernel.launcher.event.DefaultEventKeeper;
import com.peergreen.kernel.launcher.info.DefaultPlatformInfo;
import com.peergreen.kernel.launcher.ipojo.IPOJOWaiter;
import com.peergreen.kernel.launcher.ipojo.WaitingException;
import com.peergreen.kernel.launcher.prompt.PeergreenPromptService;
import com.peergreen.kernel.launcher.report.Message;
import com.peergreen.kernel.launcher.report.Reporter;
import com.peergreen.kernel.launcher.report.Severity;
import com.peergreen.kernel.launcher.scanner.BootstrapJarScanner;
import com.peergreen.kernel.launcher.scanner.BundleDirectoryScanner;
import com.peergreen.kernel.launcher.shell.Events;
import com.peergreen.kernel.launcher.shell.Infos;
import com.peergreen.kernel.launcher.system.DefaultInterceptPrintStream;
import com.peergreen.kernel.launcher.system.LogHandlerService;
import com.peergreen.kernel.launcher.system.PeergreenSystemService;
import com.peergreen.kernel.launcher.system.PrintStreamService;
import com.peergreen.kernel.launcher.system.log.HistoryLoggerHandler;
import com.peergreen.kernel.launcher.system.log.LogListenerInvocationHandler;
import com.peergreen.kernel.launcher.system.log.LogReaderServiceTracker;
import com.peergreen.kernel.launcher.system.log.OSGiLogEntryListener;
import com.peergreen.kernel.launcher.thread.PeergreenThreadGroup;
import com.peergreen.kernel.launcher.util.Lists;
import com.peergreen.kernel.launcher.util.Maps;
import com.peergreen.kernel.system.SystemStream;

/**
 * Kernel is the root class for embedding/starting Peergreen platform kernel.
 *
 * @author Florent Benoit
 * @author Guillaume Sauthier
 */
public class Kernel {

    /**
     * Should the console be activated, disabled or automatically activated (best effort, depends on the system).
     */
    public enum ConsoleMode {
        AUTO {
            @Override
            boolean launchConsole() {
                return System.console() != null;
            }
        },
        ACTIVE {
            @Override
            boolean launchConsole() {
                return true;
            }
        },
        DISABLED {
            @Override
            boolean launchConsole() {
                return false;
            }
        };

        abstract boolean launchConsole();
    }

    /**
     * Pattern used to extract the start level from the directory path
     */
    public static final Pattern STARTLEVEL_PATTERN = Pattern.compile(".*/bundles/(\\d+)/.*");

    /**
     * Underlying OSGi Framework
     */
    private Framework framework;

    /**
     * Bundle context of the platform
     */
    private BundleContext platformContext;

    /**
     * Reporter used to send alert (error, warning) messages
     */
    private final Reporter reporter = new Reporter();

    /**
     * Manage events.
     */
    private final EventKeeper eventKeeper = new DefaultEventKeeper();

    /**
     * Shelbie Branding service;
     */
    private PeergreenBrandingService brandingService;

    /**
     * Shelbie Prompt service;
     */
    private PromptService promptService;

    /**
     * Shelbie System service;
     */
    private SystemService systemService;

    /**
     * First boot ? If the framework is initialized the first time (no cache), this flag will be true.
     */
    private boolean firstBoot;

    private ConsoleMode consoleMode = ConsoleMode.DISABLED;

    /**
     * Bundles installed by the kernel and to be started at first boot.
     */
    private Collection<Bundle> installedBundles;

    /**
     * Working directory.
     */
    private File workDirectory;

    /**
     * Directory used to store bundles.
     */
    private File storage;

    /**
     * Directory used to extract {@link Pack200} files.
     */
    private File unpackBundleDir;

    /**
     * Framework StartLevel value provided by the user;
     */
    private int userFrameworkStartLevel = -1;
    private PeergreenThreadGroup threadGroup;

    /**
     * Original InputStream for reading.
     */
    private final InputStream consoleSystemIn;

    /**
     * Original PrintStream for output.
     */
    private final PrintStream consoleSystemOut;

    /**
     * Original PrintStream for errors.
     */
    private final PrintStream consoleSystemErr;

    /**
     * JDK Console Handler.
     */
    private ConsoleHandler jdkConsoleHandler;

    /**
     * History logger handler.
     */
    private HistoryLoggerHandler historyLoggerHandler;

    /**
     * Peergreen InputStream for reading.
     */
    private InputStream in;

    /**
     * Peergreen PrintStream for output.
     */
    private final DefaultInterceptPrintStream out;

    /**
     * Peergreen PrintStream for errors.
     */
    private final DefaultInterceptPrintStream err;

    /**
     * Peergreen file which contains the JDK logs
     */
    private File fileJDKlogs;

    /**
     * Peergreen file which contains System.out
     */
    private File fileSystemOut;

    /**
     * Peergreen file which contains System.err
     */
    private File fileSystemErr;

    /**
     * Image of the Bundle of the Console.
     */
    private ConsoleBundle consoleBundle;


    /**
     * Starts a default kernel with a console.
     *
     * @param args some options
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // FIXME : allows to disable the console from arguments ?
        Kernel kernel = new Kernel();
        kernel.consoleMode = ConsoleMode.AUTO;
        kernel.startKernel(true);
    }

    public Kernel() throws MalformedURLException, URISyntaxException {

        // Store System.*
        this.consoleSystemIn = System.in;
        this.consoleSystemOut = System.out;
        this.consoleSystemErr = System.err;

        // And now change System.out and System.err to a pluggable System stream
        DefaultInterceptPrintStream interceptSystemOut = new DefaultInterceptPrintStream(OUT);
        System.setOut(interceptSystemOut);
        interceptSystemOut.addPrintStream(consoleSystemOut);
        this.out = interceptSystemOut;
        DefaultInterceptPrintStream interceptSystemErr = new DefaultInterceptPrintStream(ERR);
        this.err = interceptSystemErr;
        interceptSystemErr.addPrintStream(consoleSystemErr);
        System.setErr(interceptSystemErr);

        initEventKeeper();
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
        prepare(new HashMap<String, String>());
    }

    /**
     * Creates the OSGi {@link Framework} from the OSGi {@link FrameworkFactory}<br>
     * Also add peergreen configuration for exporting some system packages
     *
     * @param configuration the map used to store configuration
     * @return the Framework that has been initialized.
     * @throws Exception if Framework cannot be instantiated
     */
    private Framework prepare(final Map<String, String> configuration) throws Exception {
        // Create the framework instance
        FrameworkFactory factory = findFrameworkFactory();


        String existingFrameworkStorage = configuration.get(org.osgi.framework.Constants.FRAMEWORK_STORAGE);
        if (existingFrameworkStorage != null) {
            workDirectory = new File(existingFrameworkStorage);
        }

        // Set directories
        if (workDirectory == null) {
            // Where is the current jar ?
            URL location = Kernel.class.getProtectionDomain().getCodeSource().getLocation();
            URL path = new URL(location.getPath());
            File locationFile = new File(path.toURI()).getParentFile().getParentFile().getParentFile();
            this.workDirectory = new File(locationFile, "peergreen");
        }
        this.unpackBundleDir = new File(workDirectory, "bundles");
        this.storage = new File(workDirectory, "storage");

        initStreams();

        // Adapt system exported packages
        List<String> packages = new ArrayList<>();

        //FIXME: needed ?
        packages.add("org.w3c.dom.traversal");
        packages.add("javax.transaction;version=1.0.0");
        packages.add("javax.transaction.xa;version=1.0.0");
        packages.add(EventKeeper.class.getPackage().getName() + ";version=1.0");
        packages.add(PlatformInfo.class.getPackage().getName() + ";version=1.0");
        packages.add(SystemStream.class.getPackage().getName() + ";version=1.0");

        // Shelbie packages as we provide our own implementation of these services
        // And that these services should be used instead of default one
        packages.add(BrandingService.class.getPackage().getName() + ";version=2.0");
        packages.add(PromptService.class.getPackage().getName() + ";version=2.0");
        packages.add(SystemService.class.getPackage().getName() + ";version=2.0");
        packages.add(IdentityProvider.class.getPackage().getName() + ";version=2.0");
        Maps.merge(configuration, org.osgi.framework.Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, Lists.join(packages, ","));

        // Storage
        Maps.set(configuration, org.osgi.framework.Constants.FRAMEWORK_STORAGE, storage.getPath());
        Maps.set(configuration, "osgi.install.area", configuration.get(org.osgi.framework.Constants.FRAMEWORK_STORAGE));

        // I need to force the Framework StartLevel here, we will move up the FSL after initialisation
        Maps.set(configuration, org.osgi.framework.Constants.FRAMEWORK_BEGINNING_STARTLEVEL, "1");

        framework = factory.newFramework(configuration);

        // Change Thread name
        Thread.currentThread().setName("Peergreen Kernel Main Thread");

        fireEvent(PLATFORM_PREPARE, "Platform is prepared");

        return framework;
    }

    private void initStreams() {
        if (consoleMode.launchConsole()) {
            File logs = new File(workDirectory, "logs");
            logs.mkdirs();

            // Remove Console handler for the System.* logs
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                if (handler instanceof ConsoleHandler) {
                    rootLogger.removeHandler(handler);
                    this.jdkConsoleHandler = (ConsoleHandler) handler;
                }
            }

            // Adds a system.log file
            this.fileJDKlogs = new File(logs, "system.log");
            FileHandler fileHandler;
            try {
                fileHandler = new FileHandler(fileJDKlogs.getPath());
                fileHandler.setFormatter(new SimpleFormatter());
            } catch (SecurityException | IOException e) {
                throw new IllegalStateException("Cannot create log file", e);
            }
            rootLogger.addHandler(fileHandler);

            // Adds history logger
            this.historyLoggerHandler = new HistoryLoggerHandler(200);
            rootLogger.addHandler(historyLoggerHandler);



            // Wrap the streams
            this.in = new ByteArrayInputStream(new byte[0]);
            System.setIn(this.in);
            this.fileSystemOut = new File(logs, "system.out");
            PrintStream printSystemOut = null;
            try {
                printSystemOut = new PrintStream(fileSystemOut);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Cannot create log file", e);
            }
            this.out.addPrintStream(printSystemOut);
            this.fileSystemErr = new File(logs, "system.err");
            PrintStream printSystemErr = null;
            try {
                printSystemErr = new PrintStream(fileSystemErr);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Cannot create log file", e);
            }
            this.err.addPrintStream(printSystemErr);

            // we don't send anymore on the console
            this.out.removePrintStream(consoleSystemOut);
            this.err.removePrintStream(consoleSystemErr);

        }
    }

    private void initEventKeeper() {

        long bootTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        fireEvent(BOOT, bootTime, "Java Virtual Machine started");

        String jvmBegin = System.getProperty(PROPERTY_BOOTSTRAP_BEGIN);
        if (jvmBegin != null) {
            fireEvent(BEGIN, Long.valueOf(jvmBegin), "Java starts executing the application");
        }

        String scanBegin = System.getProperty(Constants.Properties.PROPERTY_BOOTSTRAP_SCAN_BEGIN);
        if (scanBegin != null) {
            fireEvent(SCAN_BEGIN, Long.valueOf(scanBegin), "Bootstrap begins scan of resources");
        }

        String scanEnd = System.getProperty(Constants.Properties.PROPERTY_BOOTSTRAP_SCAN_END);
        if (scanEnd != null) {
            fireEvent(SCAN_END, Long.valueOf(scanEnd), "Bootstrap ends scan of resources");
        }

        String mainInvoke = System.getProperty(Constants.Properties.PROPERTY_BOOTSTRAP_MAIN_INVOKE);
        if (mainInvoke != null) {
            fireEvent(MAIN_INVOKE, Long.valueOf(mainInvoke), "Bootstrap starts Peergreen Platform");
        }
    }

    /**
     * @return user bundle directory from {@code System.getProperty("user.dir")}
     */
    private File getUserBundlesDirectory() {
        File user = new File(System.getProperty("user.dir"));
        return new File(user, "bundles");
    }

    /**
     * Initialize the underlying OSGi framework and install platform bundles and services.
     *
     * @throws Exception if initialization fails
     */
    private void init() throws Exception {

        // Init the framework
        // After this point, persisted bundles are re-installed (but not started)

        // Avoid to get framework System.out/System.err traces
        PrintStream previousErrorStream = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream tempStream = new PrintStream(baos);
        System.setErr(tempStream);
        try {
            framework.init();
        } finally {
            //TODO: Print trace to a logger
            System.setErr(previousErrorStream);
        }
        platformContext = framework.getBundleContext();

        // No cache, initial boot (we only have the system bundle installed)
        firstBoot = (platformContext.getBundles().length == 1);

        // register services / listener callbacks
        registerServices();

        Bundle console = null;
        // First boot, needs to install bundles
        // If not, framework has restored them
        if (firstBoot) {
            // Find the bundles to be installed
            List<BundleScanner> scanners = new ArrayList<BundleScanner>();
            scanners.add(new BootstrapJarScanner());
            scanners.add(new BundleDirectoryScanner(getUserBundlesDirectory()));

            // Gets result from scanner
            List<URL> resources = new ArrayList<>();
            for (BundleScanner scanner : scanners) {
                resources.addAll(scanner.scan());
            }

            // Install any discovered bundles
            installedBundles = installBundles(resources);

            console = findConsoleBundle(installedBundles);
            if (console != null) {
                // Do not keep it in the bundles list
                installedBundles.remove(console);
            }

            fireEvent(BUNDLES_INSTALL, "Bundles installed");
        } else {
            console = findConsoleBundle(Arrays.asList(platformContext.getBundles()));
        }

        // It may be possible that a kernel do not have the console installed
        if (console != null) {
            consoleBundle = new ConsoleBundle(console);
        }

        fireEvent(OSGI_INIT, "OSGi Framework initialized");
    }

    private Bundle findConsoleBundle(Collection<Bundle> bundles) {
        for (Bundle bundle : bundles) {
            String symbolicName = bundle.getSymbolicName();
            if (symbolicName.contains("local-console") || symbolicName.contains("startup-console")) {
                return bundle;
            }
        }

        return null;
    }

    /**
     * Register services of peergreen platform and listeners.
     */
    private void registerServices() {
        // create missing services
        this.brandingService = new PeergreenBrandingService();
        brandingService.setRedirectedSystemLogs(fileJDKlogs);
        brandingService.setRedirectedSystemOut(fileSystemOut);
        brandingService.setRedirectedSystemErr(fileSystemErr);

        this.promptService = new PeergreenPromptService(platformContext);
        this.systemService = new PeergreenSystemService(platformContext);
        this.systemService.setIn(consoleSystemIn);
        this.systemService.setOut(consoleSystemOut);
        this.systemService.setErr(consoleSystemErr);

        // Stream service
        PrintStreamService streamService = new PrintStreamService(out, err, platformContext);

        // Log service
        LogHandlerService logService = new LogHandlerService(historyLoggerHandler, platformContext);

        initLogListener();

        // register them
        platformContext.registerService(PrintStreamService.class.getName(), streamService, null);
        platformContext.registerService(LogHandlerService.class.getName(), logService, null);

        platformContext.registerService(BrandingService.class.getName(), brandingService, null);
        platformContext.registerService(PromptService.class.getName(), promptService, null);
        platformContext.registerService(SystemService.class.getName(), systemService, null);
        platformContext.registerService(EventKeeper.class, eventKeeper, null);
        platformContext.registerService(MBeanServer.class, ManagementFactory.getPlatformMBeanServer(), null);

        // Register platform related commands
        Dictionary<String, Object> dict = new Hashtable<String, Object>();
        dict.put("osgi.command.scope", "info");
        dict.put("osgi.command.function", Events.FUNCTIONS);
        platformContext.registerService(Events.class.getName(), new Events(eventKeeper), dict);

        // Register Thread related services
        threadGroup = new PeergreenThreadGroup(platformContext);
        threadGroup.open();

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

    }


    /**
     *
     */
    protected void initLogListener() {

        OSGiLogEntryListener logEntryListener = new OSGiLogEntryListener();
        InvocationHandler invocationHandler = new LogListenerInvocationHandler(logEntryListener);

        // Try to get Synchronous Listener of Eclipse
        Class<?> logListenerInterface = null;
        try {
            logListenerInterface = Kernel.class.getClassLoader().loadClass("org.eclipse.equinox.log.SynchronousLogListener");
        } catch (ClassNotFoundException e) {
            // Not available, switch back to LogListener
            logListenerInterface = LogListener.class;
        }

        LogListener interceptedLogListener = (LogListener) Proxy.newProxyInstance(Kernel.class.getClassLoader(), new Class[] {logListenerInterface}, invocationHandler);

        LogReaderServiceTracker logReaderServiceTracker = new LogReaderServiceTracker(interceptedLogListener, platformContext);
        logReaderServiceTracker.open();


    }


    /**
     * Register info-related services of peergreen platform.
     */
    private void registerInfoServices() {

        // Compute startup time (platform is ready - jvm started)
        Event bootEvent = getMandatoryEvent(BOOT);
        Event readyEvent = getMandatoryEvent(PLATFORM_READY);
        DefaultPlatformInfo info = new DefaultPlatformInfo(bootEvent.getTimestamp(),
                readyEvent.getTimestamp());
        platformContext.registerService(PlatformInfo.class.getName(), info, null);

        // Register platform related commands
        Dictionary<String, Object> dict = new Hashtable<String, Object>();
        dict.put("osgi.command.scope", "info");
        dict.put("osgi.command.function", Infos.FUNCTIONS);
        platformContext.registerService(Infos.class.getName(), new Infos(info), dict);
    }

    /**
     * Return a named Event from the event keeper.
     * We assume the event is found (no verification is performed).
     *
     * @param eventId Event identity
     * @return the found Event or {@literal null} if not found
     */
    private Event getMandatoryEvent(final String eventId) {
        return eventKeeper.getEvents(new EventFilter() {
            @Override
            public boolean accept(Event event) {
                return eventId.equals(event.getId());
            }
        }).iterator().next();
    }

    /**
     * Starts the OSGi {@link Framework}.
     *
     * @param waitForStop allows to wait the shutdown of the platform if true
     * @throws Exception if start fails
     */
    private void start(boolean waitForStop) throws Exception {
        // Start the framework (going into ACTIVE state)
        // Framework will move its StartLevel value up to 1
        // Persisted bundles may be started depending of their own persisted start level
        framework.start();
        fireEvent(OSGI_START, "OSGi Framework started");

        // start the installed bundles at the init phase
        if (firstBoot) {
            // Start the installed bundles
            startBundles(installedBundles);

            fireEvent(BUNDLES_START, "Bundles started");
        }

        // Activate the console if requested (and console bundle is available)
        if (consoleMode.launchConsole() && (consoleBundle != null)) {
            consoleBundle.start();
        } else {
            // No startup console so display the banner instead
            out.println(brandingService.getBanner(false));
        }

        setFrameworkStartLevel();

        // Wait that iPOJO has terminated initial tasks
        ServiceReference<?>[] queueServiceRefs = platformContext.getServiceReferences("org.apache.felix.ipojo.extender.queue.QueueService", "(ipojo.queue.mode=async)");
        if (queueServiceRefs != null) {
            Object queueService = platformContext.getService(queueServiceRefs[0]);
            try {
                new IPOJOWaiter(queueService).waitForStability();
            } catch (WaitingException e) {
                // cannot wait for stability
                this.consoleSystemErr.println("Cannot reach 'ready' state.");
                e.printStackTrace(this.consoleSystemErr);
            }
        }


        // Wait for the framework to stop indefinitely
        if (waitForStop) {
            framework.waitForStop(0);

            this.out.flush();
            this.err.flush();

            // restore streams
            System.setIn(consoleSystemIn);
            System.setOut(consoleSystemOut);
            System.setErr(consoleSystemErr);

            // Restore JDK console handler
            if (jdkConsoleHandler != null) {
                Logger rootLogger = LogManager.getLogManager().getLogger("");
                rootLogger.addHandler(jdkConsoleHandler);
            }

        }
    }


    /**
     * Unpack the given file through its location
     *
     * @param location the path of the pack200 file.
     * @return the path where the file has been unpacked
     */
    protected String unpack200(URL location) {
        Unpacker unpacker = Pack200.newUnpacker();

        // Get a connection on the URL
        URLConnection connection = null;
        try {
            connection = location.openConnection();
        } catch (IOException e) {
            reporter.addMessage(new Message(Severity.ERROR, e));
            return null;
        }
        connection.setDefaultUseCaches(false);


        try (InputStream is = connection.getInputStream(); GZIPInputStream gzipInputStream = new GZIPInputStream(is)) {

            // get name
            String loc = location.toString();
            int index = loc.lastIndexOf("/");
            String name = loc.substring(index + 1, loc.length() - ".pack.gz".length());

            File output = new File(unpackBundleDir, name);

            try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(output))) {
                unpacker.unpack(gzipInputStream, jarOutputStream);
            } catch (IOException e) {
                reporter.addMessage(new Message(Severity.ERROR, e));
                return null;
            }

            try {
                return output.toURI().toURL().toString();
            } catch (MalformedURLException e) {
                reporter.addMessage(new Message(Severity.ERROR, e));
                return null;
            }
        } catch (IOException e) {
            reporter.addMessage(new Message(Severity.ERROR, e));
            return null;
        }
    }

    /**
     * Install the given collection of bundles
     *
     * @param resources the collection of URL to install
     * @return the list of installed bundles that needs to be started
     * @throws BundleException
     */
    private Collection<Bundle> installBundles(Collection<URL> resources) throws BundleException {

        unpackBundleDir.mkdirs();
        Collection<Bundle> bundles = new ArrayList<Bundle>();
        for (URL resource : resources) {

            String location = resource.toString();

            // pack200 ?
            if (location.endsWith(".pack.gz")) {
                // unpack
                String newLocation = unpack200(resource);
                if (newLocation == null) {
                    continue;
                }
                // Add reference:
                location = "reference:".concat(newLocation);
            }

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

            // Do not store fragment bundles (they can't be started)
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
        if (bundles == null) {
            return;
        }

        // Start the installed bundles, respecting the activation policy if needed
        for (Bundle bundle : bundles) {
            startBundle(bundle, 0);
        }
    }

    private void startBundle(final Bundle bundle, int options) {
        try {
            bundle.start(options | Bundle.START_ACTIVATION_POLICY);
        } catch (BundleException e) {
            // Should not happen now since the framework start level is not
            // high enough to start this bundle
            reporter.addMessage(new Message(Severity.ERROR, e, bundle));
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
        return bundle.getHeaders().get(org.osgi.framework.Constants.FRAGMENT_HOST) != null;
    }

    private static FrameworkFactory findFrameworkFactory() throws Exception {
        return new FactoryFinder().find();
    }


    /**
     * Platform has failed. Prints the errors.
     */
    private void failedStartup() {
        System.out.printf("Peergreen Kernel started with error(s) (details below).%n");
        List<Message> warnings = reporter.getWarnings();
        if (!warnings.isEmpty()) {
            for (Message warning : warnings) {
                consoleSystemOut.printf("  * %s%n", warning.toString());
            }
        }
        List<Message> errors = reporter.getErrors();
        if (!errors.isEmpty()) {
            for (Message error : errors) {
                consoleSystemErr.printf("  * %s%n", error.toString());
            }
        }
    }

    private class StartupFrameworkListener implements FrameworkListener {
        @Override
        public void frameworkEvent(FrameworkEvent event) {
            fireEvent(PLATFORM_READY, "Platform is ready (all bundles have been started)");
            switch (event.getType()) {
                case FrameworkEvent.ERROR:
                    // Could not reach the expected start level because of some error
                    reporter.addMessage(new Message(Severity.ERROR, event.getThrowable(), event.getBundle()));
                    break;
                case FrameworkEvent.STARTLEVEL_CHANGED:
                    registerInfoServices();
                    if (!reporter.getErrors().isEmpty()) {
                        failedStartup();
                    }
            }
        }
    }

    private void fireEvent(String id, long timestamp, String message) {
        eventKeeper.logEvent(
                new DefaultEvent(id,
                        (timestamp == 0) ? System.currentTimeMillis() : timestamp,
                        message)
        );
    }

    private void fireEvent(String id, String message) {
        fireEvent(id, 0, message);
    }

    /**
     * Public method used to start the kernel
     *
     * @param wait (if true wait at the end of the start)
     * @throws Exception if start of the kernel fails
     */
    public void startKernel(boolean wait) throws Exception {
        prepare();
        init();
        start(wait);
    }


    public void enableConsoleAtStartup() {
        this.consoleMode = ConsoleMode.ACTIVE;
    }

    private class ConsoleBundle {
        private final Bundle bundle;

        public ConsoleBundle(Bundle bundle) {
            this.bundle = bundle;
        }

        public void start() {
            if (bundle != null) {
                // Console should be started transiently
                // At next reboot, it will NOT be automatically started
                startBundle(bundle, Bundle.START_TRANSIENT);
            }
        }

    }


}
