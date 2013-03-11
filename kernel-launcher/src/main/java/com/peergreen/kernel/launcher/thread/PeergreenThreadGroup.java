/*
 * Copyright 2013 Peergreen S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peergreen.kernel.launcher.thread;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Publish the base {@link ThreadGroup} to be used by all Peergreen's components as an OSGi service.
 */
public class PeergreenThreadGroup extends ThreadGroup {

    /**
     * Service property used to identify this ThreadGroup among others.
     */
    public static final String GROUP_NAME_PROPERTY = "group.name";

    /**
     * Default ThreadGroup name.
     */
    public static final String PEERGREEN = "peergreen";

    private final BundleContext bundleContext;
    private ServiceRegistration<ThreadGroup> registration;

    public PeergreenThreadGroup(BundleContext bundleContext) {
        super(PEERGREEN);
        this.bundleContext = bundleContext;
    }

    public void open() {
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put(GROUP_NAME_PROPERTY, getName());
        registration = bundleContext.registerService(ThreadGroup.class, this, properties);
    }

    public void close() {
        if (registration != null) {
            registration.unregister();
        }
    }
}
