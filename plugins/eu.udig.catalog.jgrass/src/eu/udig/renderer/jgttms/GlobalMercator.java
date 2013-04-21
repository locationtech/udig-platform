/**
 *    Copyright (C) 2013, Jody Garnett
 *    Copyright (C) 2009, 2010 State of California, Department of Water Resources.
 *    
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details. [http://www.gnu.org/licenses]
 *    
 *    @author Nicky Sandhu
 */
package eu.udig.renderer.jgttms;

/**
 * TMS Global Mercator Profile ---------------------------
 * 
 * Functions necessary for generation of tiles in Spherical Mercator projection,
 * EPSG:900913 (EPSG:gOOglE, Google Maps Global Mercator), EPSG:3785,
 * OSGEO:41001.
 * 
 * Such tiles are compatible with Google Maps, Microsoft Virtual Earth, Yahoo
 * Maps, UK Ordnance Survey OpenSpace API, ... and you can overlay them on top
 * of base maps of those web mapping applications.
 * 
 * Pixel and tile coordinates are in TMS notation (origin [0,0] in bottom-left).
 * 
 * What coordinate conversions do we need for TMS Global Mercator tiles::
 * 
 * LatLon <-> Meters <-> Pixels <-> Tile
 * 
 * WGS84 coordinates Spherical Mercator Pixels in pyramid Tiles in pyramid
 * lat/lon XY in metres XY pixels Z zoom XYZ from TMS EPSG:4326 EPSG:900913
 * .----. --------- -- TMS / \ <-> | | <-> /----/ <-> Google \ / | | /--------/
 * QuadTree ----- --------- /------------/ KML, public WebMapService Web Clients
 * TileMapService
 * 
 * What is the coordinate extent of Earth in EPSG:900913?
 * 
 * [-20037508.342789244, -20037508.342789244, 20037508.342789244,
 * 20037508.342789244] Constant 20037508.342789244 comes from the circumference
 * of the Earth in meters, which is 40 thousand kilometers, the coordinate
 * origin is in the middle of extent. In fact you can calculate the constant as:
 * 2 * Math.PI * 6378137 / 2.0 $ echo 180 85 | gdaltransform -s_srs EPSG:4326
 * -t_srs EPSG:900913 Polar areas with abs(latitude) bigger then 85.05112878 are
 * clipped off.
 * 
 * What are zoom level constants (pixels/meter) for pyramid with EPSG:900913?
 * 
 * whole region is on top of pyramid (zoom=0) covered by 256x256 pixels tile,
 * every lower zoom level resolution is always divided by two initialResolution
 * = 20037508.342789244 * 2 / 256 = 156543.03392804062
 * 
 * What is the difference between TMS and Google Maps/QuadTree tile name
 * convention?
 * 
 * The tile raster itself is the same (equal extent, projection, pixel size),
 * there is just different identification of the same raster tile. Tiles in TMS
 * are counted from [0,0] in the bottom-left corner, id is XYZ. Google placed
 * the origin [0,0] to the top-left corner, reference is XYZ. Microsoft is
 * referencing tiles by a QuadTree name, defined on the website:
 * http://msdn2.microsoft.com/en-us/library/bb259689.aspx
 * 
 * The lat/lon coordinates are using WGS84 datum, yeh?
 * 
 * Yes, all lat/lon we are mentioning should use WGS84 Geodetic Datum. Well, the
 * web clients like Google Maps are projecting those coordinates by Spherical
 * Mercator, so in fact lat/lon coordinates on sphere are treated as if the were
 * on the WGS84 ellipsoid.
 * 
 * From MSDN documentation: To simplify the calculations, we use the spherical
 * form of projection, not the ellipsoidal form. Since the projection is used
 * only for map display, and not for displaying numeric coordinates, we don't
 * need the extra precision of an ellipsoidal projection. The spherical
 * projection causes approximately 0.33 percent scale distortion in the Y
 * direction, which is not visually noticable.
 * 
 * How do I create a raster in EPSG:900913 and convert coordinates with PROJ.4?
 * 
 * You can use standard GIS tools like gdalwarp, cs2cs or gdaltransform. All of
 * the tools supports -t_srs 'epsg:900913'.
 * 
 * For other GIS programs check the exact definition of the projection: More
 * info at http://spatialreference.org/ref/user/google-projection/ The same
 * projection is degined as EPSG:3785. WKT definition is in the official EPSG
 * database.
 * 
 * Proj4 Text: +proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0
 * +y_0=0 +k=1.0 +units=m +nadgrids=@null +no_defs
 * 
 * Human readable WKT format of EPGS:900913:
 * PROJCS["Google Maps Global Mercator", GEOGCS["WGS 84", DATUM["WGS_1984",
 * SPHEROID["WGS 84",6378137,298.2572235630016, AUTHORITY["EPSG","7030"]],
 * AUTHORITY["EPSG","6326"]], PRIMEM["Greenwich",0],
 * UNIT["degree",0.0174532925199433], AUTHORITY["EPSG","4326"]],
 * PROJECTION["Mercator_1SP"], PARAMETER["central_meridian",0],
 * PARAMETER["scale_factor",1], PARAMETER["false_easting",0],
 * PARAMETER["false_northing",0], UNIT["metre",1, AUTHORITY["EPSG","9001"]]]
 */
public class GlobalMercator {
    
    /**
     * Look up the tile index for the provided lat / lon.
     * <p>
     * Tile index is based the common web mapping convention of starting
     * at -180,-90 and dividing the world up into two squares for the first
     * zoom level. This technique is used by WMS-C, Google Maps and other services.
     * 
     * @param lat
     * @param lon
     * @param zoom
     * @return tile index
     */
    public static int[] tile( double lat, double lon, int zoom ){
        // From: http://en.wikipedia.org/wiki/Figure_of_the_Earth
        double WGS84_EQUATORAL_RADIUS = 6378137.0;
        
        final double SHIFT = 2 * Math.PI * WGS84_EQUATORAL_RADIUS / 2.0;
        final double RESOLUTION = 2 * Math.PI * WGS84_EQUATORAL_RADIUS / 256;
        
        double x = lon * SHIFT / 180.0;
        double y = Math.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0);
        y = y * SHIFT / 180.0;
        double[] meters = new double[]{x, y};
        
        final double factor = Math.pow(2, zoom);
        double res = RESOLUTION / factor;
        int px = (int) Math.round((meters[0] + SHIFT) / res);
        int py = (int) Math.round((meters[1] + SHIFT) / res);
        int[] p = new int[]{px, py};
        int tx = (int) Math.ceil(p[0] / 256.0 - 1);
        int ty = (int) Math.ceil(p[1] / 256.0 - 1);
        int[] tile = new int[]{tx, ty};
        return new int[]{tile[0],(int) ((factor - 1) - tile[1])};
    }

    /**
     * Swap to TMS tile.
     * <p>
     * TMS standard uses the topLeft.
     * 
     * @param i
     * @param j
     * @param z
     * @return tile index for TMS
     */
    public static int[] toTMS( int i, int j, int z ){
        int topLeft = (int) ((Math.pow(2, z) - 1) - j);
        return new int[]{i, topLeft };
    }
    
    private static void check( boolean check, String msg ){
        if( !check ) {
            System.err.println( msg );
        }
    }
    private static void check( int expected[], int actual[], String msg ){
        if( expected.length != actual.length ){
            System.err.println("Expected "+expected.length+" items, but "+actual.length+" items sipplied:"+msg);
        }
        StringBuffer buffer = new StringBuffer("[");
        int fail = -1;
        for(int i=0; i< expected.length && i<actual.length; i++){
            boolean match = expected[i] == actual[i];
            if(!match){
                buffer.append("*");
                if( fail == -1 ) fail = i;
            }
            else {
                buffer.append(" ");
            }
            buffer.append(expected[i]);
            buffer.append( match ? " " : "*");
            if( i< expected.length){
                buffer.append(",");
            }
            
        }
        buffer.append("]");

        if( fail != -1 ){
            System.out.println("Expected "+buffer+" did not match "+ actual[fail]+" at index"+fail+": "+msg);
        }
        
    }
    public static void main( String args[] ){
        // zoom level 1
        check( new int[]{0,1}, tile( 0.0, 0.0, 1), "centre");
        check( new int[]{1,2}, tile( -180.0, 90.0, 1), "north west");
        check( new int[]{1,2}, tile( 180.0, 90.0, 1), "north east");
        check( new int[]{0,2}, tile( -180.0, -90.0, 1), "south west");
        check( new int[]{0,2}, tile( 180.0, -90.0, 1), "south east");
        
        check( new int[]{0,0}, toTMS( 0, 1, 1 ), "Swap [0,1] level 1 to TMS");
        check( new int[]{1,0}, toTMS( 1, 2, 1 ), "Swap [1,2] level 1 to TMS");
        
        // zoom level 2
        check( new int[]{1,2}, tile( 0.0, 0.0, 2), "level 2 centre");
        check( new int[]{2,4}, tile( -180.0, 90.0, 2), "level 2 north west");
        check( new int[]{2,4}, tile( 180.0, 90.0, 2), "level 2 north east");
        check( new int[]{0,4}, tile( -180.0, -90.0, 2), "level 2 south west");
        check( new int[]{0,4}, tile( 180.0, -90.0, 2), "level 2 south east");
        
    }
}