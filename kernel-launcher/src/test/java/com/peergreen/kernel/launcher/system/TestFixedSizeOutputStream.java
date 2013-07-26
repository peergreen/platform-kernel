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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test of the fixed size output stream.
 * @author Florent Benoit
 */
public class TestFixedSizeOutputStream {


    @Test
    public void testShift() throws IOException {
        int length = 20;
        FixedSizeOutputStream fixedSizeOutputStream = new FixedSizeOutputStream(length);

        Assert.assertEquals(0, fixedSizeOutputStream.toByteArray().length);
        for (int i = 0; i < length; i++) {
            fixedSizeOutputStream.write("a".getBytes());
        }
        Assert.assertEquals(length, fixedSizeOutputStream.toByteArray().length);

        // we should have reached the limit, try to write a new text
        fixedSizeOutputStream.write("florent".getBytes());

        // length shouldn't have changed
        Assert.assertEquals(length, fixedSizeOutputStream.toByteArray().length);

        // get content
        String content = new String(fixedSizeOutputStream.toByteArray());

        // content should have been shifted
        Assert.assertEquals(content, "aaaaaaaaaaaaaflorent");

        // we should have reached the limit, try to write a new text
        fixedSizeOutputStream.write("benoit".getBytes());
        // length shouldn't have changed
        Assert.assertEquals(length, fixedSizeOutputStream.toByteArray().length);
        // get content
        content = new String(fixedSizeOutputStream.toByteArray());
        // content should have been shifted again
        Assert.assertEquals(content, "aaaaaaaflorentbenoit");

        fixedSizeOutputStream.close();
    }

}
