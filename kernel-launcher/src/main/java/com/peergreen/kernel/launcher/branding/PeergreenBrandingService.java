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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ow2.shelbie.core.branding.BrandingService;
import org.ow2.shelbie.core.branding.Script;

/**
 * User: guillaume
 * Date: 19/02/13
 * Time: 15:46
 */
public class PeergreenBrandingService implements BrandingService {

    private File redirectedSystemOut;
    private File redirectedSystemErr;
    private File redirectedSystemLogs;



    private static final String BANNER = "" +
    " ____                                           ____                           \r\n" +
    "|  _ \\ ___  ___ _ __ __ _ _ __ ___  ___ _ __   / ___|  ___ _ ____   _____ _ __ \r\n" +
    "| |_) / _ \\/ _ \\ '__/ _` | '__/ _ \\/ _ \\ '_ \\  \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|\r\n" +
    "|  __/  __/  __/ | | (_| | | |  __/  __/ | | |  ___) |  __/ |   \\ V /  __/ |   \r\n" +
    "|_|   \\___|\\___|_|  \\__, |_|  \\___|\\___|_| |_| |____/ \\___|_|    \\_/ \\___|_|   \r\n" +
    "                    |___/____                                      _ _         \r\n" +
    "                        / ___|___  _ __ ___  _ __ ___  _   _ _ __ (_) |_ _   _ \r\n" +
    "                       | |   / _ \\| '_ ` _ \\| '_ ` _ \\| | | | '_ \\| | __| | | |\r\n" +
    "                       | |__| (_) | | | | | | | | | | | |_| | | | | | |_| |_| |\r\n" +
    "                        \\____\\___/|_| |_| |_|_| |_| |_|\\__,_|_| |_|_|\\__|\\__, |\r\n" +
    "                                                _____    _ _ _   _       |___/ \r\n" +
    "                                               | ____|__| (_) |_(_) ___  _ __  \r\n" +
    "                                               |  _| / _` | | __| |/ _ \\| '_ \\ \r\n" +
    "                                               | |__| (_| | | |_| | (_) | | | |\r\n" +
    "                                               |_____\\__,_|_|\\__|_|\\___/|_| |_|\r\n";

    @Override
    public String getBanner(boolean ansi) {
        // 1m is bold code
        // 0m is reset of color
        if (ansi) {
            StringBuilder sb = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new StringReader(BANNER))) {

                // Wrap each line in bold/reset instructions
                String line;
                while((line = reader.readLine()) != null) {
                    sb.append("\33[1m");
                    sb.append(line);
                    sb.append("\33[0m");
                    sb.append("\r\n");
                }

                return sb.toString();
            } catch (IOException e) {
                return BANNER;
            }
        }
        return BANNER;
    }

    @Override
    public Script getScript() {

        Script script = new Script() {

            @Override
            public Iterator<String> iterator() {
                List<String> lines = new ArrayList<>();

                lines.add("shelbie:echo \"Welcome on @|bold Peergreen Server|@:\"");
                lines.add("shelbie:echo \"  - Enter @|bold help|@ or hit @|bold TAB|@ key to list available commands.\"");
                lines.add("shelbie:echo \"  - Enter @|bold shutdown|@ or hit @|bold CTRL^D|@ key to shutdown the platform.\"");
                lines.add("shelbie:echo \"\"");
                if (redirectedSystemOut != null) {
                    lines.add("shelbie:echo \"System.out redirected to ".concat(redirectedSystemOut.getPath().replace("\\", "\\\\")).concat("\""));
                }
                if (redirectedSystemErr != null) {
                    lines.add("shelbie:echo \"System.err redirected to ".concat(redirectedSystemErr.getPath().replace("\\", "\\\\")).concat("\""));
                }
                if (redirectedSystemLogs != null) {
                    lines.add("shelbie:echo \"System log redirected to ".concat(redirectedSystemLogs.getPath().replace("\\", "\\\\")).concat("\""));
                }

                if (redirectedSystemOut != null || redirectedSystemErr != null || redirectedSystemLogs != null) {
                    lines.add("shelbie:echo \"\"");
                }
                lines.add("newsfeed:get-news");
                return lines.iterator();
            }

            @Override
            public String getName() {
                return "welcome";
            }
        };
        return script;
    }

    @Override
    public Map<String, Object> getVariables() {
        return Collections.emptyMap();
    }

    public void setRedirectedSystemLogs(File redirectedSystemLogs) {
        this.redirectedSystemLogs = redirectedSystemLogs;
    }


    public void setRedirectedSystemOut(File redirectedSystemOut) {
        this.redirectedSystemOut = redirectedSystemOut;
    }

    public void setRedirectedSystemErr(File redirectedSystemErr) {
        this.redirectedSystemErr = redirectedSystemErr;
    }

}
