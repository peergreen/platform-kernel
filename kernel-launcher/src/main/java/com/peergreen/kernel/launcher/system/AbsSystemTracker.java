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

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.peergreen.kernel.system.StreamType;

/**
 * Common stuff for the service tracker.
 * @author Florent Benoit
 */
public abstract class AbsSystemTracker<S, T> extends ServiceTracker<S, T> {

    /**
     * Print service.
     */
    private final PrintStreamService printStreamService;

    /**
     * Build a tracker for the given class.
     * @param printStreamService the service providing the streams
     * @param clazz the given class instance to track
     */
    public AbsSystemTracker(PrintStreamService printStreamService, Class<S> clazz) {
        super(printStreamService.getBundleContext(), clazz, null);
        this.printStreamService = printStreamService;
    }

    /**
     * Gets matching streams for a given service reference.
     * @param serviceReference the service reference to use
     * @return the matching streams
     */
    protected List<DefaultInterceptPrintStream> getMatchingStreams(ServiceReference<?> serviceReference) {
        List<DefaultInterceptPrintStream> matchingStreams = new ArrayList<>();

        List<String> streamTypes = new ArrayList<>();
        Object streamType = serviceReference.getProperty("stream.type");
        if (streamType instanceof String[]) {
            String[] types = (String[]) streamType;
            for (String type : types) {
                streamTypes.add(type);
            }
        } else if (streamType instanceof String) {
            streamTypes.add((String) streamType);
        }


        if (streamTypes.contains(StreamType.OUT.getValue())) {
            matchingStreams.add(getPrintStreamService().getSystemOutStream());
        }
        if (streamTypes.contains(StreamType.ERR.getValue())) {
            matchingStreams.add(getPrintStreamService().getSystemErrStream());
        }
        return matchingStreams;
    }

    /**
     * Do nothing when the service is modified
     */
    @Override
    public void modifiedService(ServiceReference<S> reference, T service) {

    }

    /**
     * @return print stream service.
     */
    protected PrintStreamService getPrintStreamService() {
        return printStreamService;
    }

}
