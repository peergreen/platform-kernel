/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.kernel.launcher.system;

import org.osgi.framework.BundleContext;

/**
 * Tracking Service for adding System.out/System.err PrintStreams
 * @author Florent Benoit
 */
public class PrintStreamService {

    /**
     * Bundle Context.
     */
    private final BundleContext bundleContext;

    /**
     * System.out Intercept print stream.
     */
    private final DefaultInterceptPrintStream systemOutStream;

    /**
     * System.err Intercept print stream.
     */
    private final DefaultInterceptPrintStream systemErrStream;

    /**
     * Tracks the PrintStream that are interested by being notified on system streams
     * @param bundleContext the bundle context implementation of the framework
     */
    public PrintStreamService(DefaultInterceptPrintStream systemOutStream, DefaultInterceptPrintStream systemErrStream, BundleContext bundleContext) {
        this.systemOutStream = systemOutStream;
        this.systemErrStream = systemErrStream;
        this.bundleContext = bundleContext;
        // start to track
        new PrintStreamTracker(this).open();
        new SystemStreamTracker(this).open();
    }

    /**
     * @return the bundle context.
     */
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    /**
     * @return the intercepted System.out
     */
    public DefaultInterceptPrintStream getSystemOutStream() {
        return systemOutStream;
    }

    /**
     * @return the intercepted System.err
     */
    public DefaultInterceptPrintStream getSystemErrStream() {
        return systemErrStream;
    }

}


