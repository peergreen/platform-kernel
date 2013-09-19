/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.kernel.launcher.system.log;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;


/**
 * Register the log listener on the log reader service.
 * @author Florent Benoit
 */
public class LogReaderServiceTracker extends ServiceTracker<LogReaderService, LogReaderService> {

    /**
     * Log listener.
     */
    private final LogListener logListener;

    /**
     * Build a tracker for the given class.
     * @param printStreamService the service providing the streams
     * @param clazz the given class instance to track
     */
    public LogReaderServiceTracker(LogListener logListener, BundleContext bundleContext) {
        super(bundleContext, LogReaderService.class, null);
        this.logListener = logListener;
    }


    /**
     * Do nothing when the service is modified
     */
    @Override
    public void modifiedService(ServiceReference<LogReaderService> reference, LogReaderService service) {

    }


    @Override
    public LogReaderService addingService(ServiceReference<LogReaderService> reference) {
        LogReaderService logReaderService = context.getService(reference);

        // Register the log listener
        logReaderService.addLogListener(logListener);

        return logReaderService;
    }

    @Override
    public void removedService(ServiceReference<LogReaderService> serviceReference, LogReaderService logReaderService) {
        if (logReaderService != null) {
            logReaderService.removeLogListener(logListener);
        }
    }

}
