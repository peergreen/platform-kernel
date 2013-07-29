/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.peergreen.kernel.launcher.ipojo;

import static java.lang.String.format;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is used to wait that we no longer have waiters on iPOJO queue and that we've something that has already be finished.
 * It is using java.lang.reflect as the kernel can't use iPOJO API
 * @author Florent Benoit
 */
public class IPOJOWaiter {

    /**
     * 30 seconds
     */
    public static final int THIRTY_SECONDS = 30000;

    /**
     * Default timeout.
     */
    public static final int DEFAULT_TIMEOUT = THIRTY_SECONDS;

    /**
     * At the beginning, we
     */
    public static final int INCREMENT_IN_MS = 100;

    /**
     * getWaiters() method name
     */
    private static final String GETWAITERS_METHOD_NAME = "getWaiters";

    /**
     * getWaiters() method
     */
    private Method getWaitersMethod;

    /**
     * getFinished() method name
     */
    private static final String GETFINISHED_METHOD_NAME = "getFinished";

    /**
     * getFinished() method
     */
    private Method getFinishedMethod;

    /**
     * iPOJO queue service
     */
    private final Object queueService;

    public IPOJOWaiter(final Object queueService) throws WaitingException {
        this.queueService = queueService;
        try {
            this.getWaitersMethod = queueService.getClass().getMethod(GETWAITERS_METHOD_NAME);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new WaitingException("Unable to get iPOJO method", e);
        }
        try {
            this.getFinishedMethod = queueService.getClass().getMethod(GETFINISHED_METHOD_NAME);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new WaitingException("Unable to get iPOJO method", e);
        }
    }

    public void waitForStability() throws WaitingException {
        waitForStability(DEFAULT_TIMEOUT);
    }

    /**
     * This should be moved into chameleon osgi-helper module
     * @param timeout milliseconds
     * @throws Exception
     */
    public void waitForStability(long timeout) throws WaitingException {

        long sleepTime = 0;
        long startupTime = System.currentTimeMillis();
        do {
            long elapsedTime = System.currentTimeMillis() - startupTime;
            if (isStable()) {
                return;
            }

            if (elapsedTime >= timeout) {
                throw new WaitingException(format("Stability not reached after %d ms%n", timeout));
            }

            // Not stable, re-compute sleep time
            long nextSleepTime = sleepTime + INCREMENT_IN_MS;
            if ((elapsedTime + nextSleepTime) > timeout) {
                // Last increment is too large, shrink it to fit in the timeout boundaries
                sleepTime = timeout - sleepTime;
            } else {
                sleepTime = nextSleepTime;
            }

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new WaitingException("Unable to wait on the thread", e);
            }
        } while (true);

    }

    /**
     * Stability is reached when waiters == 0 and finished > 0.
     * @return true if these two criterias are OK
     */
    private boolean isStable() {
        return getWaiters() == 0 && getFinished() > 0;
    }

    /**
     * @return waiters
     */
    protected int getWaiters() {
        try {
            return (int) this.getWaitersMethod.invoke(queueService);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * @return finished
     */
    protected int getFinished() {
        try {
            return (int) this.getFinishedMethod.invoke(queueService);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return Integer.MIN_VALUE;
        }
    }


}
