package org.locationtech.udig.jconsole.internal;

import java.util.HashMap;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class JConsoleConstants {

    public static final String PREFSTORE_HEAP_KEY = "HEAP_RAM_IN_MB";

    public static final String PREFSTORE_LOG_KEY = "LOG_LEVEL";
    
    public static String[] HEAPLEVELS = {"64", "128", "250", "500", "1000", "1500", "2000", "4000", "8000"};
    public static String dateTimeFormatterYYYYMMDDHHMMSS_string = "yyyy-MM-dd HH:mm:ss";
    public static DateTimeFormatter dateTimeFormatterYYYYMMDDHHMMSS = DateTimeFormat
            .forPattern(dateTimeFormatterYYYYMMDDHHMMSS_string);
    public static String LOGLEVEL_GUI_ON = "ON";
    public static String LOGLEVEL_GUI_OFF = "OFF";
    public static String[] LOGLEVELS_GUI = {LOGLEVEL_GUI_OFF, LOGLEVEL_GUI_ON};
    public static HashMap<String, String> LOGLEVELS_MAP = new HashMap<String, String>(2);
    static {
        LOGLEVELS_MAP.put(LOGLEVEL_GUI_OFF, "OFF");
        LOGLEVELS_MAP.put(LOGLEVEL_GUI_ON, "ALL");
    }
}
