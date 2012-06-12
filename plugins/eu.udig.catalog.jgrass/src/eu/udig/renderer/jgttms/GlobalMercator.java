package eu.udig.renderer.jgttms;
import java.util.Arrays;

/**
 *    Copyright (C) 2009, 2010 
 *    State of California,
 *    Department of Water Resources.
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
 *    
 */

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
 * 
 * 
 */
public class GlobalMercator {
    public static final int TILE_SIZE = 256;
    private final int tileSize;
    private final double initialResolution;
    private final double originShift;

    public GlobalMercator() {
        tileSize = TILE_SIZE;
        initialResolution = 2 * Math.PI * 6378137 / tileSize;
        // 156543.03392804062 for tileSize 256 pixels
        originShift = 2 * Math.PI * 6378137 / 2.0;
        // 20037508.342789244
    }

    /**
     * Converts given lat/lon in WGS84 Datum to XY in Spherical Mercator
     * EPSG:900913
     * 
     * @param lat
     * @param lon
     * @return
     */
    public double[] LatLonToMeters( double lat, double lon ) {

        double mx = lon * originShift / 180.0;
        double my = Math.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0);
        my = my * originShift / 180.0;
        return new double[]{mx, my};
    }

    /**
     * Converts XY point from Spherical Mercator EPSG:900913 to lat/lon in WGS84
     * Datum
     * 
     * @return
     */
    public double[] MetersToLatLon( double mx, double my ) {

        double lon = (mx / originShift) * 180.0;
        double lat = (my / originShift) * 180.0;

        lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
        return new double[]{lat, lon};
    }

    /**
     * Converts pixel coordinates in given zoom level of pyramid to EPSG:900913
     * 
     * @return
     */
    public double[] PixelsToMeters( double px, double py, int zoom ) {
        double res = Resolution(zoom);
        double mx = px * res - originShift;
        double my = py * res - originShift;
        return new double[]{mx, my};
    }

    /**
     * Converts EPSG:900913 to pyramid pixel coordinates in given zoom level
     * 
     * @param mx
     * @param my
     * @param zoom
     * @return
     */
    public int[] MetersToPixels( double mx, double my, int zoom ) {
        double res = Resolution(zoom);
        int px = (int) Math.round((mx + originShift) / res);
        int py = (int) Math.round((my + originShift) / res);
        return new int[]{px, py};
    }

    /**
     * Returns a tile covering region in given pixel coordinates
     * 
     * @param px
     * @param py
     * @return
     */
    public int[] PixelsToTile( int px, int py ) {
        int tx = (int) Math.ceil(px / ((double) tileSize) - 1);
        int ty = (int) Math.ceil(py / ((double) tileSize) - 1);
        return new int[]{tx, ty};
    }

    /**
     * Move the origin of pixel coordinates to top-left corner
     * 
     * @param px
     * @param py
     * @param zoom
     * @return
     */
    public int[] PixelsToRaster( int px, int py, int zoom ) {
        int mapSize = tileSize << zoom;
        return new int[]{px, mapSize - py};
    }

    /**
     * Returns tile for given mercator coordinates
     * 
     * @return
     */
    public int[] MetersToTile( double mx, double my, int zoom ) {
        int[] p = MetersToPixels(mx, my, zoom);
        return PixelsToTile(p[0], p[1]);
    }

    public int[] metersToTileUp( double mx, double my, int zoom ) {
        int[] p = metersToPixelsUp(mx, my, zoom);
        return pixelsToTileUp(p[0], p[1]);
    }
    
    public int[] metersToPixelsUp( double mx, double my, int zoom ) {
        double res = Resolution(zoom);
        int px = (int) Math.ceil((mx + originShift) / res);
        int py = (int) Math.ceil((my + originShift) / res);
        return new int[]{px, py};
    }
    
    public int[] pixelsToTileUp( int px, int py ) {
        int tx = (int) Math.ceil(px / ((double) tileSize) - 1);
        int ty = (int) Math.ceil(py / ((double) tileSize) - 1);
        return new int[]{tx, ty};
    }

    public int[] metersToTileDown( double mx, double my, int zoom ) {
        int[] p = metersToPixelsDown(mx, my, zoom);
        return pixelsToTileDown(p[0], p[1]);
    }
    
    public int[] metersToPixelsDown( double mx, double my, int zoom ) {
        double res = Resolution(zoom);
        int px = (int) Math.floor((mx + originShift) / res);
        int py = (int) Math.floor((my + originShift) / res);
        return new int[]{px, py};
    }
    
    public int[] pixelsToTileDown( int px, int py ) {
        int tx = (int) Math.floor(px / ((double) tileSize) - 1);
        int ty = (int) Math.floor(py / ((double) tileSize) - 1);
        return new int[]{tx, ty};
    }

    /**
     * Returns bounds of the given tile in EPSG:900913 coordinates
     * 
     * @param tx
     * @param ty
     * @param zoom
     * @return
     */
    public double[] TileBounds( int tx, int ty, int zoom ) {
        double[] min = PixelsToMeters(tx * tileSize, ty * tileSize, zoom);
        double minx = min[0], miny = min[1];
        double[] max = PixelsToMeters((tx + 1) * tileSize, (ty + 1) * tileSize, zoom);
        double maxx = max[0], maxy = max[1];
        return new double[]{minx, miny, maxx, maxy};
    }

    /**
     * Returns bounds of the given tile in latitude/longitude using WGS84 datum
     * 
     */
    public double[] TileLatLonBounds( int tx, int ty, int zoom ) {
        double[] bounds = TileBounds(tx, ty, zoom);
        double[] mins = MetersToLatLon(bounds[0], bounds[1]);
        double[] maxs = MetersToLatLon(bounds[2], bounds[3]);
        return new double[]{mins[0], mins[1], maxs[0], maxs[1]};
    }

    /**
     * Resolution (meters/pixel) for given zoom level (measured at Equator)
     * 
     * @return
     */
    public double Resolution( int zoom ) {
        // return (2 * Math.PI * 6378137) / (this.tileSize * 2**zoom)
        return initialResolution / Math.pow(2, zoom);
    }

    /**
     * Maximal scaledown zoom of the pyramid closest to the pixelSize
     * 
     * @param pixelSize
     * @return
     */
    public int ZoomForPixelSize( int pixelSize ) {
        for( int i = 0; i < 30; i++ ) {
            if (pixelSize > Resolution(i)) {
                if (i != 0) {
                    return i - 1;
                } else {
                    return 0; // We don't want to scale up
                }
            }
        }
        return 0;
    }

    /**
     * Converts TMS tile coordinates to Google Tile coordinates
     * 
     * @param tx
     * @param ty
     * @param zoom
     * @return
     */
    public int[] GoogleTile( int tx, int ty, int zoom ) {
        // coordinate origin is moved from bottom-left to top-left corner of the
        // extent
        return new int[]{tx, (int) ((Math.pow(2, zoom) - 1) - ty)};
    }

    public int[] TMSTileFromGoogleTile( int tx, int ty, int zoom ) {
        // coordinate origin is moved from bottom-left to top-left corner of the
        // extent
        return new int[]{tx, (int) ((Math.pow(2, zoom) - 1) - ty)};
    }

    /**
     * Converts a lat long coordinates to Google Tile Coordinates
     * 
     * @param lat
     * @param lon
     * @param zoom
     * @return
     */
    public int[] GoogleTile( double lat, double lon, int zoom ) {
        double[] meters = LatLonToMeters(lat, lon);
        int[] tile = MetersToTile(meters[0], meters[1], zoom);
        return this.GoogleTile(tile[0], tile[1], zoom);
    }

    /**
     * Converts TMS tile coordinates to Microsoft QuadTree
     * 
     * @return
     */
    public String QuadTree( int tx, int ty, int zoom ) {
        String quadKey = "";
        ty = (int) ((Math.pow(2, zoom) - 1) - ty);
        for( int i = zoom; i < 0; i-- ) {
            int digit = 0;
            int mask = 1 << (i - 1);
            if ((tx & mask) != 0) {
                digit += 1;
            }
            if ((ty & mask) != 0) {
                digit += 2;
            }
            quadKey += (digit + "");
        }
        return quadKey;
    }

    public static void main( String[] args ) {
        int[][] TMS = {//
        {0, 1, 1},//
                {16, 20, 5},//
                {8930, 10684, 14},//
        };
        int[][] GOOGLE = {//
        {0, 0, 1}, //
                {16, 11, 5}, //
                {8930, 5699, 14},//
        };

        GlobalMercator gm = new GlobalMercator();
        for( int i = 0; i < GOOGLE.length; i++ ) {
            int[] googleTile = gm.TMSTileFromGoogleTile(GOOGLE[i][0], GOOGLE[i][1], GOOGLE[i][2]);
            System.out.println(Arrays.toString(googleTile));
        }
    }

}