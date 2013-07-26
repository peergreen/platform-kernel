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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.peergreen.kernel.system.StreamLine;
import com.peergreen.kernel.system.StreamType;
import com.peergreen.kernel.system.SystemStream;

/**
 * Manages an history of lines for a given System.* stream
 * @author Florent Benoit
 */
public class HistoryLineOutputStream extends OutputStream {

    /**
     * History of the lines to keep.
     */
    private final LinkedList<StreamLine> bufferLines;

    /**
     * Size of the history.
     */
    private final int size;

    /**
     * Carriage return character.
     */
    private static final int CR = '\r';

    /**
     * Line Feed character.
     */
    private static final int LF = '\n';

    /**
     * Type of stream.
     */
    private final StreamType streamType;

    /**
     * Callbacks that may be interested
     */
    private final List<SystemStream> systemStreamsCallback;

    /**
     * Byte Array OutputStream used to store each line
     */
    private final ByteArrayOutputStream byteArrayOutputStream;

    private final  List<String> stackMethods;

    /**
     * Build an history outputstream
     * @param streamType the System.* type
     * @param size the lines to to keep in the history
     */
    public HistoryLineOutputStream(StreamType streamType, int size) {
        this.streamType = streamType;
        this.size = size;
        this.bufferLines = new LinkedList<>();
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.systemStreamsCallback = new CopyOnWriteArrayList<>();

        this.stackMethods = new ArrayList<>();
        initStackMethods();
    }

    /**
     * Adds a callback stream.
     * @param systemStream the system stream to add
     */
    public void addSystemStream(SystemStream systemStream) {
        systemStreamsCallback.add(systemStream);
    }

    /**
     * Adds a printstream and also replay the content.
     * @param systemStream the stream to use
     */
    public void addAndReplaySystemStream(SystemStream systemStream) {
        for (StreamLine line : bufferLines) {
            systemStream.newLine(line);
        }
        systemStreamsCallback.add(systemStream);
    }

    /**
     * Replay for a given printstream.
     * @param printStream
     * @throws IOException
     */
    public void replaySystemStream(PrintStream printStream) throws IOException {
        for (StreamLine line : bufferLines) {
            printStream.write(line.getText().getBytes());
            printStream.write(System.lineSeparator().getBytes());
        }
        // writes also the current buffer
        printStream.write(byteArrayOutputStream.toByteArray());
    }


    /**
     * Removes the given system stream.
     * @param systemStream the stream to remove
     */
    public void removeSystemStream(SystemStream systemStream) {
        systemStreamsCallback.remove(systemStream);
    }

    protected void initStackMethods() {
        Method[] methods = PrintStream.class.getMethods();
        for (Method method : methods) {
            stackMethods.add(method.getName());
        }
    }


    @Override
    public void write(int b) {
        write(new byte[] {(byte) b});
    }


    @Override
    public void write(byte b[]) {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte b[], int off, int len) {

        // perform the check
        if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }

        int toProcess = len;
        int index = off;
        int indexBuffer = index;
        while (toProcess > 0) {

            // skip all CRLF characters in order to know where to go
            while (toProcess > 0 && b[index] != CR && b[index] != LF) {
                index++;
                toProcess--;
            }
            // either end of buffer or a line separator char
            int subLen = index - indexBuffer;
            if (subLen > 0) {
                byteArrayOutputStream.write(b, indexBuffer, subLen);
            }
            // handle special case for CR/LF
            while (toProcess > 0 && (b[index] == CR || b[index] == LF)) {
                // push line if \n
                if (b[index] == LF) {
                    newLine(new String(byteArrayOutputStream.toByteArray()));
                    byteArrayOutputStream.reset();
                }
                index++;
                toProcess--;
            }
            indexBuffer = index;
        }
    }

    /**
     * Notify the listeners that we've a new line
     * @param line
     */
    protected void newLine(String line) {
        // delete if size reached
        if (bufferLines.size() == size) {
            bufferLines.removeFirst();
        }
        DefaultStreamLine streamLine = new DefaultStreamLine();
        streamLine.setText(line);
        streamLine.setType(streamType);
        streamLine.setTimestamp(System.currentTimeMillis());

        // Try to find the caller
        Throwable throwable = new Throwable();
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        int i = stackTraceElements.length -1;
        StackTraceElement stackTraceElement = stackTraceElements[i];
        while (!(stackTraceElement.getClassName().startsWith(HistoryLineOutputStream.class.getPackage().getName()) && stackMethods.contains(stackTraceElement.getMethodName()))) {
            stackTraceElement = stackTraceElements[i];
            i--;
        }
        if (i > 0) {
            stackTraceElement = stackTraceElements[i + 2];
        }

        // found caller
        streamLine.setSourceClassName(stackTraceElement.getClassName());
        streamLine.setSourceMethodName(stackTraceElement.getMethodName());
        streamLine.setSourceLineNumber(stackTraceElement.getLineNumber());


        bufferLines.addLast(streamLine);
        for (SystemStream systemStream : systemStreamsCallback) {
            systemStream.newLine(streamLine);
        }
    }

    /**
     * @return the history
     */
    protected List<StreamLine> getLines() {
        return bufferLines;
    }

}


