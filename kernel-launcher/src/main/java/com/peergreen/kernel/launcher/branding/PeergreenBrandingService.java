/**
 * Copyright 2013 Peergreen S.A.S.
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

package com.peergreen.kernel.launcher.branding;

import org.ow2.shelbie.core.branding.BrandingService;
import org.ow2.shelbie.core.branding.Script;

import java.util.Collections;
import java.util.Map;

/**
 * User: guillaume
 * Date: 19/02/13
 * Time: 15:46
 */
public class PeergreenBrandingService implements BrandingService {

    private static final String BANNER = "" +
            "     ___                                                       ___  _         _     __                         \n" +
            "    / _ \\  ___   ___  _ __   __ _  _ __   ___   ___  _ __     / _ \\| |  __ _ | |_  / _|  ___   _ __  _ __ ___  \n" +
            "   / /_)/ / _ \\ / _ \\| '__| / _` || '__| / _ \\ / _ \\| '_ \\   / /_)/| | / _` || __|| |_  / _ \\ | '__|| '_ ` _ \\ \n" +
            "  / ___/ |  __/|  __/| |   | (_| || |   |  __/|  __/| | | | / ___/ | || (_| || |_ |  _|| (_) || |   | | | | | |\n" +
            "  \\/      \\___| \\___||_|    \\__, ||_|    \\___| \\___||_| |_| \\/     |_| \\__,_| \\__||_|   \\___/ |_|   |_| |_| |_|\n" +
            "                            |___/\n" +
            "                                           ___                          _ _          ___    _ _ _   _          \n" +
            "                                          / __|___ _ __  _ __ _  _ _ _ (_) |_ _  _  | __|__| (_) |_(_)___ _ _  \n" +
            "                                         | (__/ _ \\ '  \\| '  \\ || | ' \\| |  _| || | | _|/ _` | |  _| / _ \\ ' \\ \n" +
            "                                          \\___\\___/_|_|_|_|_|_\\_,_|_||_|_|\\__|\\_, | |___\\__,_|_|\\__|_\\___/_||_|\n" +
            "                                                                              |__/";

    @Override
    public String getBanner(boolean ansi) {
        // 0;34m is BLUE color code
        // 0m is no color (reset)
        return ansi ? "\33[0;34m" + BANNER + "\33[0m" : BANNER;
    }

    @Override
    public Script getScript() {
        return null;
    }

    @Override
    public Map<String, Object> getVariables() {
        return Collections.emptyMap();
    }
}
