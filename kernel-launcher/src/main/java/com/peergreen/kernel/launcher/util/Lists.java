/**
 * Copyright 2013 Peergreen S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peergreen.kernel.launcher.util;

import java.util.List;

/**
 * List related toolkit.
 * @author Guillaume Sauthier
 */
public class Lists {

    private Lists() {}

    /**
     * Join all the values of the given list with the provided separator
     * @param values distinct values to be joined
     * @param separator separator
     * @return a concatenation of the provided values, separated with {@literal separator}.
     */
    public static String join(List<String> values, String separator) {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (sb.length() != 0) {
                sb.append(separator);
            }
            sb.append(value);
        }
        return sb.toString();
    }
}
