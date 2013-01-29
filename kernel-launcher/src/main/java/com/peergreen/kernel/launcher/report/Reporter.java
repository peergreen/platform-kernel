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

package com.peergreen.kernel.launcher.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 23/01/13
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
public class Reporter {
    private final Map<Severity, List<Message>> messages = new HashMap<>();

    public Reporter() {
        messages.put(Severity.WARNING, new ArrayList<Message>());
        messages.put(Severity.ERROR, new ArrayList<Message>());
    }

    public void addMessage(Message message) {
        messages.get(message.getSeverity()).add(message);
    }

    public List<Message> getWarnings() {
        return messages.get(Severity.WARNING);
    }

    public List<Message> getErrors() {
        return messages.get(Severity.ERROR);
    }

}
