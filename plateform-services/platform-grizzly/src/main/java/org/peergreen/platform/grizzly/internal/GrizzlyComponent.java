package org.peergreen.platform.grizzly.internal;

import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.osgi.httpservice.HttpServiceFactory;
import com.sun.grizzly.osgi.httpservice.util.Logger;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 14 sept. 2010
 * Time: 20:47:30
 * To change this template use File | Settings | File Templates.
 */
@Component
public class GrizzlyComponent {

    @Property(name = "http.port", value = "9002")
    private int port;

    @Property(name = "threads", value = "5")
    private int threads;

    private ServiceTracker logTracker;

    private ServiceRegistration registration;
    private Logger logger;
    private GrizzlyWebServer ws;
    private HttpServiceFactory serviceFactory;

    private BundleContext bundleContext;

    public GrizzlyComponent(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    @Validate
    public void start() throws IOException {
        logTracker = new ServiceTracker(bundleContext, LogService.class.getName(), null);
        logTracker.open();
        logger = new Logger(logTracker);

        ws = new GrizzlyWebServer(port);
        ws.setMaxThreads(threads);
        ws.useAsynchronousWrite(true);  // TODO make this configurable
        ws.start();

        serviceFactory = new HttpServiceFactory(ws, logger, bundleContext.getBundle());
        registration = bundleContext.registerService(
                HttpService.class.getName(),
                serviceFactory,
                null);

    }

    @Invalidate
    public void stop() {
        serviceFactory.stop();
        if (registration != null) {
            registration.unregister();
        }
        ws.stop();
        logTracker.close();
    }

}
