package com.peergreen.platform.launcher.report;

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
    private Map<Severity, List<Message>> messages = new HashMap<>();

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
