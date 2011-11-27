/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.udig.tools.jgrass.profile.borrowedfromjgrasstools;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Constant values and novalues handling.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class JGTConstants {
    /*
     * constants for models
     */
    /**
     * The default double novalue. 
     * 
     * <p>Note: if this changes, also the checker methods like 
     * {@link #isNovalue(double)} have to be changed.
     */
    public static final double doubleNovalue = Double.NaN;

    /**
     * Checker for default double novalue.
     * 
     * <p>
     * This was done since with NaN the != check doesn't work.
     * This has to be strict in line with the {@link #doubleNovalue}.
     * </p>
     * 
     * @param value the value to check.
     * @return true if the passed value is a novalue.
     */
    public static boolean isNovalue( double value ) {
        return Double.isNaN(value);
    }

    /**
     * Checker for a list of default double novalues.
     * 
     * @param values the list of values to check.
     * @return true if one of the passes values is a novalue.
     * 
     * @see #isNovalue(double)
     */
    public static boolean isOneNovalue( double... values ) {
        for( double value : values ) {
            if (Double.isNaN(value))
                return true;
        }
        return false;
    }

    /**
     * The default float novalue. 
     */
    public static final float floatNovalue = Float.NaN;

    /**
     * Checker for default float novalue.
     * 
     * <p>
     * This was done since with NaN the != check doesn't work.
     * This has to be strict in line with the {@link #floatNovalue}.
     * </p>
     * 
     * @param value the value to check.
     * @return true if the passed value is a novalue.
     */
    public static boolean isNovalue( float value ) {
        return Float.isNaN(value);
    }

    /**
     * The default int novalue. 
     */
    public static final int intNovalue = Integer.MAX_VALUE;

    /**
     * Checker for default int novalue.
     * 
     * <p>
     * This was done since with NaN the != check doesn't work.
     * This has to be strict in line with the {@link #intNovalue}.
     * </p>
     * 
     * @param value the value to check.
     * @return true if the passed value is a novalue.
     */
    public static boolean isNovalue( int value ) {
        return Integer.MAX_VALUE == value;
    }

    /**
     * Check if the width and height of a raster would lead to a numeric overflow.
     * 
     * @param width width of the matrix or raster.
     * @param height height of the matrix or raster.
     * @return true if there is overfow.
     */
    public static boolean doesOverFlow( int width, int height ) {
        if ((long) width * (long) height < Integer.MAX_VALUE) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Global formatter for joda datetime (yyyy-MM-dd HH:mm:ss).
     */
    public static String dateTimeFormatterYYYYMMDDHHMMSS_string = "yyyy-MM-dd HH:mm:ss";
    public static DateTimeFormatter dateTimeFormatterYYYYMMDDHHMMSS = DateTimeFormat
            .forPattern(dateTimeFormatterYYYYMMDDHHMMSS_string);

    /**
    * Global formatter for joda datetime (yyyy-MM-dd HH:mm).
    */
    public static String dateTimeFormatterYYYYMMDDHHMM_string = "yyyy-MM-dd HH:mm";
    public static DateTimeFormatter dateTimeFormatterYYYYMMDDHHMM = DateTimeFormat
            .forPattern(dateTimeFormatterYYYYMMDDHHMM_string);

    public static String utcDateFormatterYYYYMMDDHHMMSS_string = "yyyy-MM-dd HH:mm:ss";
    public static DateTimeFormatter utcDateFormatterYYYYMMDDHHMMSS = DateTimeFormat.forPattern(
            utcDateFormatterYYYYMMDDHHMMSS_string).withZone(DateTimeZone.UTC);
    public static String utcDateFormatterYYYYMMDDHHMM_string = "yyyy-MM-dd HH:mm";
    public static DateTimeFormatter utcDateFormatterYYYYMMDDHHMM = DateTimeFormat.forPattern(utcDateFormatterYYYYMMDDHHMM_string)
            .withZone(DateTimeZone.UTC);

    /**
     * Enumeration defining meteo types.
     */
    public static int TEMPERATURE = 0;
    public static int PRESSURE = 1;
    public static int HUMIDITY = 2;
    public static int WIND = 3;
    /**
     * Average daily range temperature.
     */
    public static int DTDAY = 4;
    /**
     * Average monthly range temperature.
     */
    public static int DTMONTH = 5;

    /**
     * Earth rotation [rad/h].
     */
    public final static double omega = 0.261799388; /* velocita' di rotazione terrestre [rad/h] */
    /**
     * Zero celsius degrees in Kelvin.
     */
    public final static double tk = 273.15; /* =0 C in Kelvin */
    /**
     * Von Karman constant.
     */
    public final static double ka = 0.41; /* costante di Von Karman */
    /**
     * Freezing temperature [C]
     */
    public final static double Tf = 0.0; /* freezing temperature [C] */
    /**
     * Solar constant [W/m2].
     */
    public final static double Isc = 1367.0; /* Costante solare [W/m2] */
    /**
     * Water density [kg/m3].
     */
    public final static double rho_w = 1000.0; /* densita' dell'acqua [kg/m3] */
    /**
     * Ice density [kg/m3].
     */
    public final static double rho_i = 917.0; /* densita' del ghiaccio [kg/m3] */
    /**
     * Latent heat of melting [J/kg].
     */
    public final static double Lf = 333700.00; /* calore latente di fusione [J/kg] */
    /**
     * Latent heat of sublimation [J/kg].
     */
    public final static double Lv = 2834000.00; /* calore latente di sublimazione [J/kg] */
    /**
     * Heat capacity of water [J/(kg/K)].
     */
    public final static double C_liq = 4188.00; /* heat capacity of water       [J/(kg/K)] */
    /**
     * Heat capacity of ice [J/(kg/K)].
     */
    public final static double C_ice = 2117.27; /* heat capacity of ice     [J/(kg/K)] */
    /**
     * Adiabatic lapse rate [K/m].
     */
    public final static double GAMMA = 0.006509; /* adiabatic lapse rate [K/m]*/
    /**
     * Costante di Stefan-Boltzmann [W/(m2 K4)].
     */
    public final static double sigma = 5.67E-8; /* costante di Stefan-Boltzmann [W/(m2 K4)]*/

    /*
     * FILE EXTENTIONS
     */
    public static final String AIG = "adf";
    public static final String ESRIGRID = "asc";
    public static final String GEOTIFF = "tiff";
    public static final String GEOTIF = "tif";
    public static final String GRASS = "grass";
    public static final String SHP = "shp";

    /*
     * modules categories
     */
    // IO
    public static final String GENERICREADER = "Generic Reader";
    public static final String GENERICWRITER = "Generic Writer";
    public static final String HASHMAP_READER = "HashMap Data Reader";
    public static final String HASHMAP_WRITER = "HashMap Data Writer";
    public static final String LIST_READER = "List Data Reader";
    public static final String LIST_WRITER = "List Data Writer";
    public static final String RASTERREADER = "Raster Reader";
    public static final String GRIDGEOMETRYREADER = "Grid Geometry Reader";
    public static final String RASTERWRITER = "Raster Writer";
    public static final String FEATUREREADER = "Vector Reader";
    public static final String FEATUREWRITER = "Vector Writer";
    // processing
    public static final String RASTERPROCESSING = "Raster Processing";
    public static final String VECTORPROCESSING = "Vector Processing";
    // horton
    public static final String BASIN = "HortonMachine/Basin";
    public static final String DEMMANIPULATION = "HortonMachine/Dem Manipulation";
    public static final String GEOMORPHOLOGY = "HortonMachine/Geomorphology";
    public static final String HYDROGEOMORPHOLOGY = "HortonMachine/Hydro-Geomorphology";
    public static final String HILLSLOPE = "HortonMachine/Hillslope";
    public static final String NETWORK = "HortonMachine/Network";
    public static final String STATISTICS = "HortonMachine/Statistics";

    /*
     * vars ui hints
     */
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

}
