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

import com.peergreen.kernel.system.StreamLine;
import com.peergreen.kernel.system.StreamType;

/**
 * Basic implementation of a line of stream
 * @author Florent Benoit
 */
public class DefaultStreamLine implements StreamLine {

    /**
     * Type : System.out/System.err
     */
    private StreamType type;

    /**
     * Timestamp when the line has been written
     */
    private long timestamp;

    /**
     * Text/content of the entry.
     */
    private String text;

    private int sourceLineNumber;

    private String sourceClassName;

    private String sourceMethodName;

    /**
     * @param type the System.* type
     */
    public void setType(StreamType type) {
        this.type = type;
    }

    /**
     * @param timestamp the date
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @param text the content
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return content
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * @return System.* type
     */
    @Override
    public StreamType getType() {
        return type;
    }

    /**
     * @return timestamp
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int getSourceLineNumber() {
        return sourceLineNumber;
    }

    @Override
    public String getSourceClassName() {
        return sourceClassName;
    }

    @Override
    public String getSourceMethodName() {
        return sourceMethodName;
    }

    public void setSourceLineNumber(int sourceLineNumber) {
        this.sourceLineNumber = sourceLineNumber;
    }

    public void setSourceClassName(String sourceClassName) {
        this.sourceClassName = sourceClassName;
    }

    public void setSourceMethodName(String sourceMethodName) {
        this.sourceMethodName = sourceMethodName;
    }

}
