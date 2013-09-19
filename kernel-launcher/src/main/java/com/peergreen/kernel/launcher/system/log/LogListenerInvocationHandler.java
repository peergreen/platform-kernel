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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.osgi.service.log.LogListener;

/**
 * Invocation Handler that will delegate calls to the wrapped Log listener
 * @author Florent Benoit
 */
public class LogListenerInvocationHandler implements InvocationHandler {

    private final LogListener wrappedLogListener;

    public LogListenerInvocationHandler(LogListener logListener) {
        this.wrappedLogListener = logListener;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(wrappedLogListener, args);
    }

}
