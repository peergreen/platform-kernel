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

/**
 * This output stream has a limited size. If there are more writes on the stream than the buffer, data are skipped
 * @author Florent Benoit
 */
public class FixedSizeOutputStream extends ByteArrayOutputStream {

    private final int size;

    public FixedSizeOutputStream(int size) {
        super(size);
        this.size = size;
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

        // Write content if we din't have reached the limit
        if (count + len <= size) {
            System.arraycopy(b, off, buf, count, len);
            count += len;
        } else {
            // we've reached the limit so we need to skip previous content
            int recopyStart = count - (size - len);
            int shift = size - len;

            // shift the current content
            System.arraycopy(buf, recopyStart, buf, 0, shift);

            // write the new content
            System.arraycopy(b, off, buf, shift, len);

           // change the value of all data to the current size
           count = size;

        }
    }
}
