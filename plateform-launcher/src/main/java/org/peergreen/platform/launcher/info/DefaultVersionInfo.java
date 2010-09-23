package org.peergreen.platform.launcher.info;

import org.peergreen.platform.info.VersionInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 21:28:28
 * To change this template use File | Settings | File Templates.
 */
public class DefaultVersionInfo implements VersionInfo {

    private String text;

    private List<String> elements;

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

    public String getText() {
        return text;
    }

    public int getMajor() {
        return Integer.valueOf(elements.get(0));
    }

    public int getMinor() {
        return Integer.valueOf(elements.get(1));
    }
}
