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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.peergreen.kernel.system.LogListener;

/**
 * This handler keeps an history of the last log records that have occurs.
 *
 * @author Florent Benoit
 */
public class HistoryLoggerHandler extends Handler {

    /**
     * Listeners.
     */
    private final List<LogListener> listeners;

    /**
     * History of the log record to keep.
     */
    private final List<LogRecord> records;

    /**
     * Size of the history.
     */
    private final int size;

    public HistoryLoggerHandler(int size) {
        this.size = size;
        this.records = new CopyOnWriteArrayList<>();
        this.listeners = new ArrayList<>();
    }

    /**
     * Register a listener.
     * @param listener the log listener to add
     */
    public void addListener(LogListener listener) {
        // replay
        for (LogRecord record : records) {
            listener.log(record);
        }
        listeners.add(listener);
    }

    /**
     * Register a listener.
     * @param listener the log listener to add
     */
    public void removeListener(LogListener listener) {
        listeners.remove(listener);
    }


    @Override
    public void publish(LogRecord record) {
        // delete first if size reached
        if (records.size() == size) {
            records.remove(0);
        }
        // add the record
        records.add(record);

        for (LogListener listener : listeners) {
            listener.log(record);
        }
    }

    @Override
    public void flush() {
        // do nothing
    }

    @Override
    public void close() throws SecurityException {
        // do nothing
    }

}
