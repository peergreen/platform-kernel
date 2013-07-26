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

import java.util.List;

import org.osgi.framework.ServiceReference;

import com.peergreen.kernel.system.SystemStream;

/**
 * Tracker for PrintStream.
 * @author Florent Benoit
 */
public class SystemStreamTracker extends AbsSystemTracker<SystemStream, SystemStream> {

    /**
     * Tracks the SystemStream objects
     * @param printStreamService the service containing System.*
     */
    public SystemStreamTracker(PrintStreamService printStreamService) {
        super(printStreamService, SystemStream.class);
    }

    @Override
    public SystemStream addingService(ServiceReference<SystemStream> reference) {
        SystemStream systemStream = context.getService(reference);
        List<DefaultInterceptPrintStream> matchingStreams = getMatchingStreams(reference);
        for (DefaultInterceptPrintStream matchingStream : matchingStreams) {
            matchingStream.getHistoryLineOutputStream().addAndReplaySystemStream(systemStream);
        }
        if (matchingStreams.size() > 0) {
            return systemStream;
        }
        return null;
    }

    @Override
    public void removedService(ServiceReference<SystemStream> serviceReference, SystemStream systemStream) {
        if (systemStream != null) {
            getPrintStreamService().getSystemOutStream().getHistoryLineOutputStream().removeSystemStream(systemStream);
            getPrintStreamService().getSystemErrStream().getHistoryLineOutputStream().removeSystemStream(systemStream);
        }
    }

}
