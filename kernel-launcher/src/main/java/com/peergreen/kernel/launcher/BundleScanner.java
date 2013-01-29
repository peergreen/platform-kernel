/**
 * Copyright 2012-2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.peergreen.kernel.launcher;

import java.net.URL;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 22 sept. 2010
 * Time: 21:36:14
 * To change this template use File | Settings | File Templates.
 */
public interface BundleScanner {
    Collection<URL> scan();
}
