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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.peergreen.kernel.system.StreamLine;
import com.peergreen.kernel.system.StreamType;
import com.peergreen.kernel.system.SystemStream;

/**
 * Test the history line output stream
 * @author Florent Benoit
 */
public class TestHistoryLineOutputStream {



    @Test
    public void testShift() throws IOException {
        int loop = 5;
        int length = 5;
        HistoryLineOutputStream historyLineOutputStream = new HistoryLineOutputStream(StreamType.OUT, length);

        List<StreamLine> lines = historyLineOutputStream.getLines();

        Assert.assertEquals(0, lines.size());
        for (int i = 0; i < loop; i++) {
            historyLineOutputStream.write("a".getBytes());
        }
        // still 0 as there is no new line
        Assert.assertEquals(0, lines.size());

        // write CRLF
        historyLineOutputStream.write("\r\n".getBytes());

        // should have one item
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals(lines.get(0).getText(), "aaaaa");

        // write 4 lines CRLF or LF
        historyLineOutputStream.write("a\r\n".getBytes());
        historyLineOutputStream.write("ab\n".getBytes());
        historyLineOutputStream.write("abc\r\n".getBytes());
        historyLineOutputStream.write("abcd\n".getBytes());

        // get 5 ?
        Assert.assertEquals(length, lines.size());

        // we should still have get 5 as limit has been reached
        historyLineOutputStream.write("abcde\r\n".getBytes());
        Assert.assertEquals(length, historyLineOutputStream.getLines().size());

        // check content
        Assert.assertEquals(lines.get(0).getText(), "a");
        Assert.assertEquals(lines.get(1).getText(), "ab");
        Assert.assertEquals(lines.get(2).getText(), "abc");
        Assert.assertEquals(lines.get(3).getText(), "abcd");
        Assert.assertEquals(lines.get(4).getText(), "abcde");

        historyLineOutputStream.close();
    }


    @Test
    public void testCaller() throws IOException {
        DefaultInterceptPrintStream defaultInterceptPrintStream = new DefaultInterceptPrintStream(StreamType.OUT);
        TestListener testListener = new TestListener();
        defaultInterceptPrintStream.getHistoryLineOutputStream().addSystemStream(testListener);
        PrintStream old = System.out;
        try {
            System.setOut(defaultInterceptPrintStream);
            System.out.println("This is a test");
        } finally {
            System.setOut(old);
        }


        List<StreamLine> lines = testListener.getLines();
        assertNotNull(lines);
        assertEquals(lines.size(), 1);

        StreamLine streamLine = lines.get(0);
        String className = streamLine.getSourceClassName();
        String methodsName = streamLine.getSourceMethodName();

        assertEquals(className, TestHistoryLineOutputStream.class.getName());
        assertEquals(methodsName, "testCaller");


    }


}

class TestListener implements SystemStream {


    private final List<StreamLine> lines;


    public TestListener() {
        this.lines = new ArrayList<>();
    }

    @Override
    public void newLine(StreamLine line) {
        lines.add(line);
    }

    public List<StreamLine> getLines() {
        return lines;
    }

}
