/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.kernel.system;

/**
 * Line written to the console
 * @author Florent Benoit
 */
public interface StreamLine {

    /**
     * @return text which has been printed
     */
    String getText();

    /**
     * @return type of the System stream : OUT or ERR
     */
    StreamType getType();

    /**
     * @return timestamp of the entry
     */
    long getTimestamp();

    /**
     * @return line number of the caller
     */
    int getSourceLineNumber();

    /**
     * @return classname of the caller
     */
    String getSourceClassName();

    /**
     * @return methodName of the caller
     */
    String getSourceMethodName();
}
