package com.peergreen.platform.launcher.info;

import com.peergreen.platform.info.JavaInfo;
import com.peergreen.platform.info.VersionInfo;

import java.io.File;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 20:48:43
 * To change this template use File | Settings | File Templates.
 */
public class DefaultJavaInfo implements JavaInfo {

    private File executable;

    private VersionInfo specification;

    private VersionInfo implementation;

    public File getExecutable() {
        if (executable == null) {
            executable = findExecutable();
        }
        return executable;
    }

    private File findExecutable() {
        String home = System.getProperty("java.home");
        File javaBin = new File(home, "bin");
        return new File(javaBin, "java");
    }

    public VersionInfo getSpecificationVersion() {
        if (specification == null) {
            specification = findSpecificationVersion();
        }
        return specification;
    }

    private VersionInfo findSpecificationVersion() {
        String def = System.getProperty("java.specification.version");
        String pack = System.class.getPackage().getSpecificationVersion();
        String version = choose(pack, def);
        return new DefaultVersionInfo(version);
    }

    private String choose(String first, String second) {
        if (first != null) {
            return first;
        } else {
            return second;
        }
    }

    public VersionInfo getImplementationVersion() {
        if (implementation == null) {
            implementation = findImplementationVersion();
        }
        return implementation;
    }

    private VersionInfo findImplementationVersion() {
        String def = System.getProperty("java.implementation.version");
        String pack = System.class.getPackage().getImplementationVersion();
        String version = choose(pack, def);
        return new DefaultVersionInfo(version);
    }

    public Properties getSystemProperties() {
        return (Properties) System.getProperties().clone();
    }
}
