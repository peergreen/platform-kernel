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
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import com.peergreen.kernel.system.StreamType;

/**
 * This class allows to plug other Printstreams.
 * It will delegate all the calls to the sub printstream entries.
 * @author Florent Benoit
 */
public class DefaultInterceptPrintStream extends PrintStream {

    /**
     * Maximum size of the replay.
     */
    private static final int MAX_LINES = Integer.valueOf(System.getProperty("com.peergreen.kernel.launcher.system.max_lines", "100"));

    /**
     * List of streams on which to send calls.
     */
    private final List<PrintStream> streams;

    /**
     * History of all lines printed
     */
    private final HistoryLineOutputStream historyLineOutputStream;

    /**
     * Default constructor.
     */
    public DefaultInterceptPrintStream(StreamType streamType) {
        super(new NullOutputStream());
        this.streams = new CopyOnWriteArrayList<>();
        this.historyLineOutputStream = new HistoryLineOutputStream(streamType, MAX_LINES);
        this.streams.add(new PrintStream(historyLineOutputStream, true));
    }

    /**
     * Adds the given print stream.
     * @param printStream
     */
    public void addPrintStream(PrintStream printStream) {
        streams.add(printStream);
    }

    /**
     * Replay the current lines on the given printstream.
     * @param printStream
     * @throws IOException
     */
    public void replay(PrintStream printStream) throws IOException {
        historyLineOutputStream.replaySystemStream(printStream);
    }

    /**
     * Remove the given printstream.
     * @param printStream
     */
    public void removePrintStream(PrintStream printStream) {
        streams.remove(printStream);
    }

    /**
     * @return the history line output stream.
     */
    public HistoryLineOutputStream getHistoryLineOutputStream() {
        return historyLineOutputStream;
    }

    @Override
    public void flush() {
        for (PrintStream printStream : streams) {
            printStream.flush();
        }
    }

    @Override
    public void close() {
        for (PrintStream printStream : streams) {
            printStream.close();
        }
    }

    @Override
    public boolean checkError() {
        for (PrintStream printStream : streams) {
            if (printStream.checkError()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void write(int b) {
        for (PrintStream printStream : streams) {
            printStream.write(b);
        }
    }

    @Override
    public void write(byte buf[], int off, int len) {
        for (PrintStream printStream : streams) {
            printStream.write(buf, off, len);
        }
    }



    @Override
    public void print(boolean b) {
        for (PrintStream printStream : streams) {
            printStream.print(b);
        }
    }


    @Override
    public void print(char c) {
        for (PrintStream printStream : streams) {
            printStream.print(c);
        }
    }


    @Override
    public void print(int i) {
        for (PrintStream printStream : streams) {
            printStream.print(i);
        }
    }


    @Override
    public void print(long l) {
        for (PrintStream printStream : streams) {
            printStream.print(l);
        }
    }


    @Override
    public void print(float f) {
        for (PrintStream printStream : streams) {
            printStream.print(f);
        }
    }


    @Override
    public void print(double d) {
        for (PrintStream printStream : streams) {
            printStream.print(d);
        }
    }


    @Override
    public void print(char s[]) {
        for (PrintStream printStream : streams) {
            printStream.print(s);
        }
    }

    @Override
    public void print(String s) {
        for (PrintStream printStream : streams) {
            printStream.print(s);
        }
    }

    @Override
    public void print(Object obj) {
        for (PrintStream printStream : streams) {
            printStream.print(obj);
        }
    }

    @Override
    public void println() {
        for (PrintStream printStream : streams) {
            printStream.println();
        }
    }


    @Override
    public void println(boolean x) {
        for (PrintStream printStream : streams) {
            printStream.println(x);
        }
    }

    @Override
    public void println(char x) {
        for (PrintStream printStream : streams) {
            printStream.println(x);
        }
    }


    @Override
    public void println(int x) {
        for (PrintStream printStream : streams) {
            printStream.println(x);
        }
    }

    @Override
    public void println(long x) {
        for (PrintStream printStream : streams) {
            printStream.println(x);
        }
    }


    @Override
    public void println(float x) {
        for (PrintStream printStream : streams) {
            printStream.println(x);
        }
    }


    @Override
    public void println(double x) {
        for (PrintStream printStream : streams) {
            printStream.println(x);
        }
    }


    @Override
    public void println(char x[]) {
        for (PrintStream printStream : streams) {
            printStream.println(x);
        }
    }


    @Override
    public void println(String x) {
        for (PrintStream printStream : streams) {
            printStream.println(x);
        }
    }


    @Override
    public void println(Object x) {
        for (PrintStream printStream : streams) {
            printStream.println(x);
        }
    }

    @Override
    public PrintStream printf(String format, Object ... args) {
        for (PrintStream printStream : streams) {
            printStream.printf(format, args);
        }
        return this;
    }


    @Override
    public PrintStream printf(Locale l, String format, Object ... args) {
        for (PrintStream printStream : streams) {
            printStream.printf(l, format, args);
        }
        return this;
    }


    @Override
    public PrintStream format(String format, Object ... args) {
        for (PrintStream printStream : streams) {
            printStream.format(format, args);
        }
        return this;
    }


    @Override
    public PrintStream format(Locale l, String format, Object ... args) {
        for (PrintStream printStream : streams) {
            printStream.format(l, format, args);
        }
        return this;
    }


    @Override
    public PrintStream append(CharSequence csq) {
        for (PrintStream printStream : streams) {
            printStream.append(csq);
        }
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        for (PrintStream printStream : streams) {
            printStream.append(csq, start, end);
        }
        return this;
    }


    @Override
    public PrintStream append(char c) {
        for (PrintStream printStream : streams) {
            printStream.append(c);
        }
        return this;
    }

}
