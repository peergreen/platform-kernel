/**
 * Copyright 2012 Peergreen S.A.S.
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
package com.peergreen.platform.bootstrap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;

public class Bootstrap {

    private final String[] args;

    protected Bootstrap(String[] args) {
        this.args = args;
    }


    public void start() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, URISyntaxException {

        // Register our factory
        //FIXME: try hack if existing factory or if we can accept Felix/Equinox factories
        URL.setURLStreamHandlerFactory(new BootstrapURLStreamHandlerFactory());


        // Create classloader with embedded jars
        ClassLoader classLoader = getClassLoader();

        // Load framework launcher
        Class<?> mainClass = classLoader.loadClass("com.peergreen.platform.launcher.Platform");
        if (mainClass == null) {
            System.out.println("class not found");
            return;
        }
        Method mainMethod = mainClass.getMethod("main",String[].class);

        // call main
        mainMethod.invoke(null, (Object) args);

    }


    protected ClassLoader getClassLoader() throws IOException, URISyntaxException {

        // get the bundles of the launcher
        URL url = new File("/Users/benoitf/Documents/workspace/platform/platform-bootstrap/target/platform-bootstrap-1.0-SNAPSHOT.jar").toURI().toURL();


        ClassLoader urlClassLoader = new InsideJarClassLoader(url, Thread.currentThread().getContextClassLoader());


        return urlClassLoader;
    }



    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap(args);
        bootstrap.start();
    }

}
