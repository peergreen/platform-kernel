/**
 * Copyright 2012-2013 Peergreen S.Aimport java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.launch.FrameworkFactory;
licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.peergreen.kernel.launcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.launch.FrameworkFactory;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 21:40:45
 * To change this template use File | Settings | File Templates.
 */
class FactoryFinder {

    private final static String RESOURCE = "META-INF/services/" + FrameworkFactory.class.getName();

    public FrameworkFactory find() throws Exception {
        System.out.println("classloader = " + FactoryFinder.class.getClassLoader());
        Enumeration<URL> resources = FactoryFinder.class.getClassLoader().getResources(RESOURCE);
        List<URL> urls = Collections.list(resources);
        if (urls.isEmpty()) {
            throw new Exception("No FrameworkFactory found in System ClassLoader");
        }

        for (URL resource : urls) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.openStream()))) {
                for (String s = br.readLine(); s != null; s = br.readLine()) {
                    s = s.trim();
                    // Try to load first non-empty, non-commented line.
                    if ((s.length() > 0) && (s.charAt(0) != '#')) {
                        return Class.forName(s).asSubclass(FrameworkFactory.class).newInstance();
                    }
                }
            }

        }
        return null;
    }
}
