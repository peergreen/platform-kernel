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

import org.osgi.framework.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 23/01/13
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class Message {
    private final Bundle bundle;
    private final Throwable throwable;
    private final Severity severity;

    public Message(Severity severity, Throwable throwable) {
        this(severity, throwable, null);
    }

    public Message(Severity severity, Throwable throwable, Bundle bundle) {
        this.severity = severity;
        this.throwable = throwable;
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Severity getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(severity.name().charAt(0))
          .append(" ");
        if (bundle != null) {
            sb.append("[")
              .append(bundle.getSymbolicName())
              .append("/")
              .append(bundle.getVersion())
              .append("] ");
        }
        sb.append(throwable.getClass().getSimpleName())
          .append(": ")
          .append(throwable.getMessage())
          .append(".");
        return sb.toString();
    }
}
