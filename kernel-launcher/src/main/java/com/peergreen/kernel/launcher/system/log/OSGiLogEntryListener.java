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

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static org.osgi.service.log.LogService.LOG_DEBUG;
import static org.osgi.service.log.LogService.LOG_ERROR;
import static org.osgi.service.log.LogService.LOG_INFO;
import static org.osgi.service.log.LogService.LOG_WARNING;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

/**
 * Log Listener that will redirect.
 * @author Florent Benoit
 */
public class OSGiLogEntryListener implements LogListener {

    /**
     * Logger.
     */
    private final Logger logger = Logger.getLogger(OSGiLogEntryListener.class.getName());

    /**
     * Redirect the log entry to the JDK logger.
     */
    @Override
    public void logged(LogEntry entry) {

        Level level = INFO;
        switch (entry.getLevel()) {

        case LOG_DEBUG:
            level = FINE;
            break;
        case LOG_ERROR:
            level = SEVERE;
            break;
        case LOG_INFO:
            level = INFO;
            break;
        case LOG_WARNING:
            level = WARNING;
            break;
        }

        if (entry.getException() != null) {
            logger.log(level, entry.getMessage(), entry.getException());
        } else {
            logger.log(level, entry.getMessage());
        }
    }

}
