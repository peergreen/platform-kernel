/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.kernel.system;

import java.util.logging.LogRecord;

/**
 * Allows to be notified each time that a JDK log entry is received
 * @author Florent Benoit
 */
public interface LogListener {

    /**
     * Gets a log record.
     * @param logRecord the given record
     */
    void log(LogRecord logRecord);

}
