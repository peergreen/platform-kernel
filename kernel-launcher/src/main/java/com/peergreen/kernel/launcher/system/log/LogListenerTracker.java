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

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.peergreen.kernel.launcher.system.LogHandlerService;
import com.peergreen.kernel.system.LogListener;

/**
 * Log listener service tracker.
 * @author Florent Benoit
 */
public class LogListenerTracker extends ServiceTracker<LogListener, LogListener> {

    /**
     * Log Handler service.
     */
    private final LogHandlerService logHandlerService;

    /**
     * Build a tracker for the given class.
     * @param printStreamService the service providing the streams
     * @param clazz the given class instance to track
     */
    public LogListenerTracker(LogHandlerService logHandlerService, Class<LogListener> clazz) {
        super(logHandlerService.getBundleContext(), clazz, null);
        this.logHandlerService = logHandlerService;
    }


    /**
     * Do nothing when the service is modified
     */
    @Override
    public void modifiedService(ServiceReference<LogListener> reference, LogListener service) {

    }



    @Override
    public LogListener addingService(ServiceReference<LogListener> reference) {
        LogListener logListener = context.getService(reference);
        HistoryLoggerHandler historyLoggerHandler = logHandlerService.getHistoryLoggerHandler();
        historyLoggerHandler.addListener(logListener);
        return logListener;
    }

    @Override
    public void removedService(ServiceReference<LogListener> serviceReference, LogListener logListener) {
        if (logListener != null) {
            logHandlerService.getHistoryLoggerHandler().removeListener(logListener);
        }
    }


}
