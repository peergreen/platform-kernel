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

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.osgi.framework.ServiceReference;

/**
 * Tracker for PrintStream.
 * @author Florent Benoit
 */
public class PrintStreamTracker extends AbsSystemTracker<PrintStream, PrintStream> {

    /**
     * Tracks the PrintStream objects
     * @param printStreamService the service containing System.*
     */
    public PrintStreamTracker(PrintStreamService printStreamService) {
        super(printStreamService, PrintStream.class);
    }

    @Override
    public PrintStream addingService(ServiceReference<PrintStream> reference) {
        PrintStream printStream = context.getService(reference);
        List<DefaultInterceptPrintStream> matchingStreams = getMatchingStreams(reference);
        for (DefaultInterceptPrintStream matchingStream : matchingStreams) {
            try {
                matchingStream.replay(printStream);
            } catch (IOException e) {
                // cannot perform replay
            }
            matchingStream.addPrintStream(printStream);
        }
        if (matchingStreams.size() > 0) {
            return printStream;
        }
        return null;
    }


    @Override
    public void removedService(ServiceReference<PrintStream> serviceReference, PrintStream printStream) {
        if (printStream != null) {
            getPrintStreamService().getSystemOutStream().removePrintStream(printStream);
            getPrintStreamService().getSystemErrStream().removePrintStream(printStream);
        }
    }

}
