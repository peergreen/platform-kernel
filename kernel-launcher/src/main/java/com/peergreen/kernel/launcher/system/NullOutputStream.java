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
import java.io.OutputStream;

/**
 * Empty output stream.
 * @author Florent Benoit
 */
public class NullOutputStream extends OutputStream {

    @Override
    public void write(int b) throws IOException {
        // Do nothing
    }

    @Override
    public void write(byte b[]) throws IOException {
        // Do nothing
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        // Do nothing
    }
}
