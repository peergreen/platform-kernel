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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import java.io.IOException;
import java.io.PrintStream;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test of the printstream tracker.
 * @author Florent Benoit
 */
public class TestPrintStreamTracker {

    @Mock
    private PrintStreamService printStreamService;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private DefaultInterceptPrintStream outIntercepted;

    @Mock
    private DefaultInterceptPrintStream errIntercepted;

    @Mock
    private ServiceReference<PrintStream> serviceReference;

    @Mock
    private PrintStream printStream;

    @Mock
    private HistoryLineOutputStream outHistory;

    @Mock
    private HistoryLineOutputStream errHistory;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);

        doReturn(bundleContext).when(printStreamService).getBundleContext();
        doReturn(printStream).when(bundleContext).getService(serviceReference);

        doReturn(outIntercepted).when(printStreamService).getSystemOutStream();

        doReturn(errIntercepted).when(printStreamService).getSystemErrStream();

    }

    @Test
    public void testRegisterNoMatch() throws IOException {
        PrintStreamTracker printStreamTracker = new PrintStreamTracker(printStreamService);
        PrintStream found = printStreamTracker.addingService(serviceReference);
        // found should be null as it doesn't match
        assertNull(found);
    }


    @Test
    public void testMatchingString() throws IOException {

        doReturn("System.out").when(serviceReference).getProperty("stream.type");

        PrintStreamTracker printStreamTracker = new PrintStreamTracker(printStreamService);
        PrintStream found = printStreamTracker.addingService(serviceReference);
        // found should be null as it doesn't match
        assertSame(found, printStream);

        verify(outIntercepted, times(1)).replay(printStream);
        verify(errIntercepted, never()).replay(printStream);

    }

    @Test
    public void testMatchingArrayString() throws IOException {

        doReturn(new String[] { "System.out", "System.err"}).when(serviceReference).getProperty("stream.type");

        PrintStreamTracker printStreamTracker = new PrintStreamTracker(printStreamService);
        PrintStream found = printStreamTracker.addingService(serviceReference);
        // found should be null as it doesn't match
        assertSame(found, printStream);

        verify(outIntercepted, times(1)).replay(printStream);
        verify(errIntercepted, times(1)).replay(printStream);

    }


}
