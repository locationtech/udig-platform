package eu.udig.omsbox.utils;

import java.util.HashMap;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class OmsBoxConstants {
    public static final String CATEGORY_OTHERS = "Others";
    public static final String RASTERPROCESSING = "Raster Processing";
    public static final String VECTORPROCESSING = "Vector Processing";
    public static final String GENERICREADER = "Generic Reader";
    public static final String GENERICWRITER = "Generic Writer";
    public static final String HASHMAP_READER = "HashMap Data Reader";
    public static final String HASHMAP_WRITER = "HashMap Data Writer";
    public static final String LIST_READER = "List Data Reader";
    public static final String LIST_WRITER = "List Data Writer";
    public static final String GRIDGEOMETRYREADER = "Grid Geometry Reader";
    public static final String RASTERREADER = "Raster Reader";
    public static final String RASTERWRITER = "Raster Writer";
    public static final String VECTORREADER = "Vector Reader";
    public static final String VECTORWRITER = "Vector Writer";
    public static final String BASIN = "Basin";
    public static final String DEMMANIPULATION = "Dem Manipulation";
    public static final String GEOMORPHOLOGY = "Geomorphology";
    public static final String HYDROGEOMORPHOLOGY = "Hydro-Geomorphology";
    public static final String HILLSLOPE = "Hillslope";
    public static final String NETWORK = "Network";
    public static final String STATISTICS = "Statistics";

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

    public static String[] HEAPLEVELS = {"64", "128", "250", "500", "1000", "1500", "2000", "4000", "8000"};

    public static final int LISTHEIGHT = 8;

    // vars labels
    public static final String HIDE_UI_HINT = "hide";
    public static final String FILEIN_UI_HINT = "infile";
    public static final String FOLDERIN_UI_HINT = "infolder";
    public static final String FILEOUT_UI_HINT = "outfile";
    public static final String FOLDEROUT_UI_HINT = "outfolder";
    public static final String FILESPATHLIST_UI_HINT = "filespathlist";
    public static final String CRS_UI_HINT = "crs";
    public static final String ITERATOR_UI_HINT = "iterator";
    public static final String EASTINGNORTHING_UI_HINT = "eastnorth";
    public static final String NORTHING_UI_HINT = "northing";
    public static final String EASTING_UI_HINT = "easting";
    public static final String MULTILINE_UI_HINT = "multiline";
    public static final String MAPCALC_UI_HINT = "mapcalc";
    public static final String PROCESS_NORTH_UI_HINT = "process_north";
    public static final String PROCESS_SOUTH_UI_HINT = "process_south";
    public static final String PROCESS_EAST_UI_HINT = "process_east";
    public static final String PROCESS_WEST_UI_HINT = "process_west";
    public static final String PROCESS_COLS_UI_HINT = "process_cols";
    public static final String PROCESS_ROWS_UI_HINT = "process_rows";
    public static final String PROCESS_XRES_UI_HINT = "process_xres";
    public static final String PROCESS_YRES_UI_HINT = "process_yres";
    public static final String GRASSFILE_UI_HINT = "grassfile";
    
    /**
     * Key used to set and retrieve the grass installation location.
     * 
     * <p>Example on linux: /usr/lib/grass64
     */
    public static String GRASS_ENVIRONMENT_GISBASE_KEY = "jgt-grass.gisbase";
    public static String GRASS_ENVIRONMENT_SHELL_KEY = "jgt-grass.shell";

    public static String MAPCALCHISTORY_KEY = "mapcalc-history";
    public static String MAPCALCHISTORY_SEPARATOR = "@@@";

}
