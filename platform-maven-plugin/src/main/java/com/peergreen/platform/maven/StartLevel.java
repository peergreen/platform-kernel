package com.peergreen.platform.maven;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 24/01/13
 * Time: 22:49
 * To change this template use File | Settings | File Templates.
 */
public class StartLevel {
    private int level = 1;
    private List<String> bundles = new ArrayList<>();

    public int getLevel() {
        return level;
    }

    public List<String> getBundles() {
        return bundles;
    }
}
