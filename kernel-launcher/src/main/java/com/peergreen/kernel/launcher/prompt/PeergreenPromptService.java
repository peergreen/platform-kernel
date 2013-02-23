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
package com.peergreen.kernel.launcher.prompt;

import javax.security.auth.Subject;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.ow2.shelbie.core.identity.IdentityProvider;
import org.ow2.shelbie.core.prompt.PromptService;
import org.ow2.shelbie.core.prompt.Variables;
/**
 * Peergreen prompt service.
 * @author Florent Benoit
 */
public class PeergreenPromptService implements PromptService, ServiceTrackerCustomizer {

    /**
     * Peergreen constant.
     */
    public static final String PEERGREEN_PLATFORM = "peergreen-platform";

    /**
     * separator constant.
     */
    public static final String SEPARATTOR = "@";

    /**
     * prompt.
     */
    public static final String PROMPT = "$ ";

    /**
     * Bundle Context.
     */
    private final BundleContext bundleContext;

    /**
     * Identity provider.
     */
    private IdentityProvider identityProvider;

    /**
     * This prompt service will use the identity provider in order to get the identity of the user.
     * @param bundleContext the bundle context implementation of the framework
     */
    public PeergreenPromptService(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        new ServiceTracker(bundleContext, IdentityProvider.class.getName(), this).open();
    }


    @Override
    public String getPrompt(Variables variables) {

        String user = null;

        // Try to get a subject
        Subject subject = null;
        Object oSubject = variables.get(Subject.class.getName());
        if (oSubject != null && oSubject.getClass().isAssignableFrom(Subject.class)) {
            subject = (Subject) oSubject;
        }

        // identity provider ?
        if (identityProvider != null && subject != null) {
            user = identityProvider.get(subject);
        }

        // Not able to get username
        if (user == null) {
            user = "Unknown";
        }

        return user.concat(SEPARATTOR).concat(PEERGREEN_PLATFORM).concat(PROMPT);
    }




    @Override
    public Object addingService(ServiceReference reference) {
        this.identityProvider = (IdentityProvider) bundleContext.getService(reference);

        return identityProvider;
    }


    @Override
    public void modifiedService(ServiceReference reference, Object service) {
       // nothing to do when modified

    }


    @Override
    public void removedService(ServiceReference reference, Object service) {
        // remove it
       if (this.identityProvider.equals(service)) {
           this.identityProvider = null;
       }

    }

}
