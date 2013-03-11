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
package com.peergreen.kernel.launcher.system;

import java.io.InputStream;
import java.io.PrintStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.ow2.shelbie.core.system.SystemService;

/**
 * Allows to plug our own System.out / System.err
 * @author Florent Benoit
 */
public class PeergreenSystemService implements SystemService {

    /**
     * System bundle
     */
    private final Bundle systemBundle;

    /**
     * Stopping ?
     */
    private boolean stopping = false;

    /**
     * InputStream for reading.
     */
    private InputStream in;

    /**
     * PrintStream for output.
     */
    private PrintStream out;

    /**
     * PrintStream for errors.
     */
    private PrintStream err;


    /**
     * Constructor.
     * @param bundleContext the bundle context of the platform
     */
    public PeergreenSystemService(BundleContext platformContext) {
        this.systemBundle = platformContext.getBundle(0);

        // Default values
        this.in = System.in;
        this.out = System.out;
        this.err = System.err;
    }

    @Override
    public void shutdown() {
        try {
            // AFAIU, this is not strictly required to stop bundle[0] in a separate Thread,
            // because the spec says that stop() for the system bundle must spawn a Thread
            // to do its work (and returns as quickly as possible)
            systemBundle.stop();
            stopping = true;
        } catch (BundleException e) {
            // Should not happen
        }
    }

    @Override
    public boolean isStopping() {
        return stopping;
    }



    /**
     * Sets the Stream for reading.
     * @param in the given inputstream
     */
    @Override
    public void setIn(InputStream in) {
        this.in = in;
    }

    /**
     * @return the inputstream {@link System.in}.
     */
    @Override
    public InputStream getIn() {
        return in;
    }

    /**
     * Sets the Stream for writing {@link System.out}.
     * @param out the given printstream
     */
    @Override
    public void setOut(PrintStream out) {
        this.out = out;
    }

    /**
     * @return the printstream {@link System.out}.
     */
    @Override
    public PrintStream getOut() {
        return out;
    }

    /**
     * Sets the Stream for writing {@link System.err}.
     * @param err the given printstream
     */
    @Override
    public void setErr(PrintStream err) {
        this.err = err;
    }

    /**
     * @return the printstream {@link System.out}.
     */
    @Override
    public PrintStream getErr() {
        return err;
    }

}
