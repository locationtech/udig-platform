package org.locationtech.udig.jconsole.util;

import static org.joda.time.format.DateTimeFormat.forPattern;

import org.joda.time.format.DateTimeFormatter;

public class JConsoleConstants {

    public static final String[] HEAPLEVELS = { "64", "128", "250", "500", "1000", "1500", "2000",
            "4000", "8000" };

    public static final String LOGLEVEL_GUI_ON = "ON";

    public static final String LOGLEVEL_GUI_OFF = "OFF";

    public static final String[] LOGLEVELS_GUI = { LOGLEVEL_GUI_OFF, LOGLEVEL_GUI_ON };

    public static final String dateTimeFormatterYYYYMMDDHHMMSS_string = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter dateTimeFormatterYYYYMMDDHHMMSS = forPattern(dateTimeFormatterYYYYMMDDHHMMSS_string);

}
