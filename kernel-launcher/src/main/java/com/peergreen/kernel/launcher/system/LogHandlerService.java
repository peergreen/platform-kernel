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

import com.peergreen.kernel.launcher.system.log.HistoryLoggerHandler;
import com.peergreen.kernel.launcher.system.log.LogListenerTracker;
import com.peergreen.kernel.system.LogListener;

/**
 * Tracking Service for redirecting System logs.
 * @author Florent Benoit
 */
public class LogHandlerService {

    /**
     * History log handler.
     */
    private final HistoryLoggerHandler historyLoggerHandler;

    /**
     * Bundle Context.
     */
    private final BundleContext bundleContext;

    /**
     * Tracks the PrintStream that are interested by being notified on system streams
     * @param bundleContext the bundle context implementation of the framework
     */
    public LogHandlerService(HistoryLoggerHandler historyLoggerHandler, BundleContext bundleContext) {
        this.historyLoggerHandler = historyLoggerHandler;
        this.bundleContext = bundleContext;
        // start to track
        new LogListenerTracker(this, LogListener.class).open();
    }

    /**
     * @return the bundle context.
     */
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public HistoryLoggerHandler getHistoryLoggerHandler() {
        return historyLoggerHandler;
    }


}


