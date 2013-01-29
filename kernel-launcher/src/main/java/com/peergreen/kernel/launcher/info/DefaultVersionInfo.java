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

package com.peergreen.kernel.launcher.info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.peergreen.kernel.info.VersionInfo;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 21:28:28
 * To change this template use File | Settings | File Templates.
 */
public class DefaultVersionInfo implements VersionInfo {

    private final String text;

    private final List<String> elements;

    public DefaultVersionInfo(String version) {
        this.text = version;
        this.elements = new ArrayList<String>();
        parse();
    }

    private void parse() {
        String[] parts = text.split("\\.");
        elements.addAll(Arrays.asList(parts));

        // Append a trailing 0 if required
        if (elements.size() == 1) {
            elements.add("0");
        }
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int getMajor() {
        return Integer.valueOf(elements.get(0));
    }

    @Override
    public int getMinor() {
        return Integer.valueOf(elements.get(1));
    }
}
