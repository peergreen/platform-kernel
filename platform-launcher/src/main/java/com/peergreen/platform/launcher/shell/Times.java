package com.peergreen.platform.launcher.shell;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 28/01/13
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class Times {
    /**
     * Prints the duration in a human readable format as X days Y hours Z minutes etc.
     *
     * @param uptime the uptime in millis
     * @return the time used for displaying on screen or in logs
     */
    public static String printDuration(double uptime) {
        // Code taken from Karaf
        // https://svn.apache.org/repos/asf/felix/trunk/karaf/shell/commands/src/main/java/org/apache/felix/karaf/shell/commands/InfoAction.java

        NumberFormat fmtI = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.ENGLISH));
        NumberFormat fmtD = new DecimalFormat("###,##0.000", new DecimalFormatSymbols(Locale.ENGLISH));

        if (uptime < 1000) {
            return fmtI.format(uptime) + " ms";
        }
        uptime /= 1000;
        if (uptime < 60) {
            return fmtD.format(uptime) + " seconds";
        }
        uptime /= 60;
        if (uptime < 60) {
            long minutes = (long) uptime;
            return fmtI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
        }
        uptime /= 60;
        if (uptime < 24) {
            long hours = (long) uptime;
            long minutes = (long) ((uptime - hours) * 60);
            String s = fmtI.format(hours) + (hours > 1 ? " hours" : " hour");
            if (minutes != 0) {
                s += " " + fmtI.format(minutes) + (minutes > 1 ? " minutes" : "minute");
            }
            return s;
        }
        uptime /= 24;
        long days = (long) uptime;
        long hours = (long) ((uptime - days) * 60);
        String s = fmtI.format(days) + (days > 1 ? " days" : " day");
        if (hours != 0) {
            s += " " + fmtI.format(hours) + (hours > 1 ? " hours" : "hour");
        }
        return s;
    }
}
