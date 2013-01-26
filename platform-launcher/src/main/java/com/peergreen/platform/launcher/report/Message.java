package com.peergreen.platform.launcher.report;

import org.osgi.framework.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 23/01/13
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class Message {
    private Bundle bundle;
    private Throwable throwable;
    private Severity severity;

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
