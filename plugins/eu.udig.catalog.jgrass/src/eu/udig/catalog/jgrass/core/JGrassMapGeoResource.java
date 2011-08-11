/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.catalog.jgrass.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveFolder;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.gce.grassraster.JGrassConstants;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import eu.udig.catalog.jgrass.JGrassPlugin;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

public class JGrassMapGeoResource extends IGeoResource {

    public static final String READERID = "eu.hydrologis.udig.catalog.internal.jgrass.JGrassMapGeoResource.readerid"; //$NON-NLS-1$

    /** the parent georesource field */
    private IResolveFolder parent = null;

    /** name field */
    private String name = null;

    /** map type field */
    private String type = null;

    /** error message field */
    private Throwable msg = null;

    /** the map file region window field */
    private JGrassRegion fileWindow = null;

    /** metadata info field */
    private IGeoResourceInfo info = null;

    private final IService parentService;

    private ID id;

    private JGrassMapEnvironment jGrassMapEnvironment;

    public JGrassMapGeoResource( IService parentService, JGrassMapsetGeoResource parentMapset, String mapName,
            String mapTypeAndPath ) {
        this.parentService = parentService;
        this.parent = parentMapset;
        this.name = mapName;

        String[] typeAndPath = mapTypeAndPath.split("\\|"); //$NON-NLS-1$
        type = typeAndPath[0].trim();

        URL parentIdentifier = parent.getIdentifier();
        ID parentID = new ID(parentIdentifier);

        jGrassMapEnvironment = new JGrassMapEnvironment(parentMapset.getFile(), name);

        id = new ID(parentID, name);

    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        // garbage in, garbage out
        if (adaptee == null)
            return false;

        /*
         * in this case our resource is a folder, therefore of type File
         */
        return adaptee.isAssignableFrom(IService.class) || adaptee.isAssignableFrom(IGeoResource.class)
                || adaptee.isAssignableFrom(JGrassMapGeoResource.class) || adaptee.isAssignableFrom(GridCoverage.class)
                || adaptee.isAssignableFrom(GridGeometry2D.class) || super.canResolve(adaptee);
        // || adaptee.isAssignableFrom(File.class);
    }

    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;

        if (adaptee.isAssignableFrom(IService.class)) {
            return adaptee.cast(parent.parent(monitor));
        }
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class)) {
            return adaptee.cast(getInfo(monitor));
        }
        if (adaptee.isAssignableFrom(File.class)) {
            return adaptee.cast(jGrassMapEnvironment.getMapFile());
        }
        if (adaptee.isAssignableFrom(JGrassMapGeoResource.class)) {
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(IGeoResource.class)) {
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(GridGeometry2D.class)) {
            try {
                CoordinateReferenceSystem crs = jGrassMapEnvironment.getCoordinateReferenceSystem();
                JGrassRegion r = jGrassMapEnvironment.getFileRegion();
                Envelope2D envelope = new Envelope2D(crs, r.getWest(), r.getSouth(), r.getEast() - r.getWest(), r.getNorth()
                        - r.getSouth());
                GridEnvelope2D gridRange = new GridEnvelope2D(0, 0, r.getCols(), r.getRows());
                GridGeometry2D gridGeometry2D = new GridGeometry2D(gridRange, (org.opengis.geometry.Envelope) envelope);
                return adaptee.cast(gridGeometry2D);
            } catch (Exception e) {
                msg = e;
            }
        }
        if (adaptee.isAssignableFrom(GridCoverage.class)) {
            try {
                JGrassRegion jGrassRegion = jGrassMapEnvironment.getFileRegion();
                GridCoverage2D mapCoverage = JGrassCatalogUtilities.getGridcoverageFromGrassraster(jGrassMapEnvironment,
                        jGrassRegion);
                return adaptee.cast(mapCoverage);
            } catch (Exception e) {
                msg = e;
            }
        }
        // bad call to resolve
        return super.resolve(adaptee, monitor);
    }
    // public ID getID() {
    // return id;
    // }

    public URL getIdentifier() {

        File mapFile = jGrassMapEnvironment.getMapFile();
        int mapsetPathLength = mapFile.getParentFile().getParent().length();
        String relativePath = mapFile.getAbsolutePath().substring(mapsetPathLength);
        try {
            String parenturlString = URLUtils.urlToString(parent.getIdentifier(), false);
            relativePath = relativePath.replace("\\", "/");
            return new URL(parenturlString + relativePath);
        } catch (MalformedURLException e) {
            JGrassPlugin.log(
                    "JGrassPlugin problem: eu.hydrologis.udig.catalog.internal.jgrass#JGrassMapGeoResource#getIdentifier", e); //$NON-NLS-1$

            e.printStackTrace();
            return null;
        }
    }

    public IService service( IProgressMonitor monitor ) throws IOException {
        return parentService;
    }

    public IResolve parent( IProgressMonitor monitor ) throws IOException {
        return parent;
    }

    public Throwable getMessage() {
        return msg;
    }

    public Status getStatus() {
        // error occured
        if (msg != null) {
            return Status.BROKEN;
        }

        return Status.CONNECTED;
    }

    public String getType() {
        return type;
    }

    public File getMapFile() {
        return jGrassMapEnvironment.getMapFile();
    }

    public File getMapsetFile() {
        return jGrassMapEnvironment.getMAPSET();
    }

    public File getLocationFile() {
        return jGrassMapEnvironment.getLOCATION();
    }

    /**
     * <p>
     * get some informations about the map resource
     * </p>
     * 
     * @author Andrea Antonello - www.hydrologis.com
     * @since 1.1.0
     */
    class JGrassMapGeoResourceInfo extends IGeoResourceInfo {
        public JGrassMapGeoResourceInfo( IProgressMonitor monitor ) {
            this.name = JGrassMapGeoResource.this.name;
            this.title = this.name;
            this.description = JGrassMapGeoResource.this.type;

            /*
             * what if it is a grass binary raster map?
             */
            String mapsetPath = jGrassMapEnvironment.getMAPSET().getAbsolutePath();
            try {
                if (JGrassMapGeoResource.this.type.equals(JGrassConstants.GRASSBINARYRASTERMAP)) {

                    File cellhd = jGrassMapEnvironment.getCELLHD();
                    fileWindow = new JGrassRegion(cellhd.getAbsolutePath());
                    CoordinateReferenceSystem grassCrs = ((JGrassMapsetGeoResource) JGrassMapGeoResource.this.parent)
                            .getJGrassCrs();
                    bounds = new ReferencedEnvelope(fileWindow.getEnvelope(), grassCrs);

                    super.icon = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.GRID_OBJ);
                }
                /*
                 * what if it is a grass ascii raster map?
                 */
                else if (JGrassMapGeoResource.this.type.equals(JGrassConstants.GRASSASCIIRASTERMAP)) {
                    String grassasciiFilePath = mapsetPath + File.separator + JGrassConstants.GRASSASCIIRASTER + File.separator
                            + this.name;

                    fileWindow = getGrassAsciiFileWindow(grassasciiFilePath);
                    bounds = new ReferencedEnvelope(fileWindow.getEnvelope(),
                            ((JGrassMapsetGeoResource) JGrassMapGeoResource.this.parent).getJGrassCrs());

                    super.icon = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/grassascii.gif"); //$NON-NLS-1$
                }
                /*
                 * what if it is an esri ascii grid map?
                 */
                else if (JGrassMapGeoResource.this.type.equals(JGrassConstants.ESRIRASTERMAP)) {
                    String esriiFilePath = mapsetPath + File.separator + JGrassConstants.ESRIASCIIRASTER + File.separator
                            + this.name;

                    fileWindow = getEsriGridFileWindow(esriiFilePath);
                    bounds = new ReferencedEnvelope(fileWindow.getEnvelope(),
                            ((JGrassMapsetGeoResource) JGrassMapGeoResource.this.parent).getJGrassCrs());

                    super.icon = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/esrigrid.gif"); //$NON-NLS-1$
                }
                /*
                 * what if it is a fluidturtle ascii raster map?
                 */
                else if (JGrassMapGeoResource.this.type.equals(JGrassConstants.FTRASTERMAP)) {
                    String fluidturtleFilePath = mapsetPath + File.separator + JGrassConstants.FLUIDTURTLEASCIIRASTER
                            + File.separator + this.name;
                    fileWindow = getFluidturtleFileWindow(fluidturtleFilePath);
                    bounds = new ReferencedEnvelope(fileWindow.getEnvelope(),
                            ((JGrassMapsetGeoResource) JGrassMapGeoResource.this.parent).getJGrassCrs());

                    super.icon = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/ftraster.gif"); //$NON-NLS-1$
                }

                /*
                 * what if it is a vector map
                 */
                else if (JGrassMapGeoResource.this.type.equals(JGrassConstants.GRASS6VECTORMAP)) {
                    super.icon = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.FEATURE_OBJ);
                }
                /*
                 * what if it is a point map
                 */
                else if (JGrassMapGeoResource.this.type.equals(JGrassConstants.SITESMAP)) {
                    // calculate bounds
                    Envelope tmpBounds = new Envelope();

                    File sitesFile = new File(jGrassMapEnvironment.getMAPSET(), JGrassConstants.SITE_LISTS + "/"
                            + jGrassMapEnvironment.getMapName());
                    BufferedReader sitesReader = null;
                    try {
                        sitesReader = new BufferedReader(new FileReader(sitesFile));

                        // jump over the first two lines, which are descriptive
                        sitesReader.readLine();
                        sitesReader.readLine();
                        String line = null;
                        while( (line = sitesReader.readLine()) != null ) {
                            /*
                             * read the entries
                             */
                            String[] tokens = line.split("\\|"); //$NON-NLS-1$
                            double x = Double.parseDouble(tokens[0].trim());
                            double y = Double.parseDouble(tokens[1].trim());

                            if (tmpBounds.isNull()) {
                                tmpBounds.init(new Coordinate(x, y));
                            } else {
                                tmpBounds.expandToInclude(new Coordinate(x, y));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        sitesReader.close();
                    }

                    bounds = new ReferencedEnvelope(tmpBounds,
                            ((JGrassMapsetGeoResource) JGrassMapGeoResource.this.parent).getJGrassCrs());
                    super.icon = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.PIXEL_OBJ);
                } else {
                    super.icon = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.GRID_MISSING);
                }
            } catch (Exception e) {
                super.icon = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/problem.gif"); //$NON-NLS-1$
                e.printStackTrace();
            }

        }

        public ReferencedEnvelope getBounds() {
            return bounds;
        }

        public void setBounds( ReferencedEnvelope newBounds ) {
            bounds = newBounds;
        }
    }

    /**
     * the real extention and resolution of the map
     * @throws IOException 
     */
    public JGrassRegion getFileWindow() {
        return fileWindow;
    }

    public void resetBoundInfo() throws IOException {
        jGrassMapEnvironment = new JGrassMapEnvironment(jGrassMapEnvironment.getCELL());
        fileWindow = jGrassMapEnvironment.getFileRegion();
        CoordinateReferenceSystem grassCrs = ((JGrassMapsetGeoResource) JGrassMapGeoResource.this.parent).getJGrassCrs();
        ReferencedEnvelope newBounds = new ReferencedEnvelope(fileWindow.getEnvelope(), grassCrs);
        if (info != null) {
            ((JGrassMapGeoResourceInfo) info).setBounds(newBounds);
        }
        createInfo(new NullProgressMonitor());
    }

    @Override
    public String getTitle() {
        try {
            createInfo(new NullProgressMonitor());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info.getTitle();
    }

    /**
     * the region how it is seen when doing elaborations. This is the active working region of the
     * mapset.
     * @throws IOException 
     */
    public JGrassRegion getActiveWindow() throws IOException {
        if (JGrassMapGeoResource.this.type.equals(JGrassConstants.GRASSBINARYRASTERMAP)) {

            JGrassRegion activeRegion = jGrassMapEnvironment.getActiveRegion();
            return activeRegion;
        }

        return null;
    }

    public JGrassMapEnvironment getjGrassMapEnvironment() {
        return jGrassMapEnvironment;
    }

    // /**
    // * Quick way to read a {@link GridCoverage2D coverage} from this resource active region.
    // *
    // * <p>The active region is read at the moment of the call.</p>
    // *
    // * @param monitor a progress monitor.
    // * @return the read {@link GridCoverage2D}.
    // * @throws IOException
    // */
    // public GridCoverage2D getCoverageActiveRegion( IProgressMonitorJGrass monitor ) throws
    // IOException {
    // return getCoverageInRegion(getActiveWindow(), monitor);
    // }
    //
    // /**
    // * Quick way to read a {@link GridCoverage2D coverage} from this resource.
    // *
    // * @param region the requested region.
    // * @param monitor a progress monitor.
    // * @return the read {@link GridCoverage2D}.
    // * @throws IOException
    // */
    // public GridCoverage2D getCoverageInRegion( JGrassRegion region, IProgressMonitorJGrass
    // monitor ) throws IOException {
    // GrassCoverageReader coverageReader = new GrassCoverageReader(PixelInCell.CELL_CENTER, null,
    // true, false, monitor);
    // GrassCoverageReadParam params = new GrassCoverageReadParam(region);
    // coverageReader.setInput(jGrassMapEnvironment.getCELL());
    // GridCoverage2D mapCoverage = coverageReader.read(params);
    // return mapCoverage;
    // }

    // /**
    // * Quick way to read data from this resource active region.
    // *
    // * <p>The active region is read at the moment of the call.</p>
    // *
    // * @param monitor a prgress monitor.
    // * @return the read {@link RasterData}.
    // * @throws IOException
    // */
    // public RasterData getDataInActiveRegion( IProgressMonitorJGrass monitor ) throws IOException
    // {
    // return getDataInRegion(getActiveWindow(), monitor);
    // }
    //
    // /**
    // * Quick way to read some data from this resource.
    // *
    // * @param region the requested region.
    // * @param monitor a progress monitor.
    // * @return the read {@link RasterData}.
    // * @throws IOException
    // */
    // public RasterData getDataInRegion( JGrassRegion region, IProgressMonitorJGrass monitor )
    // throws IOException {
    //
    // JGrassRasterMapReader jgrassMapReader = new JGrassRasterMapReader.BuilderFromMapPath(region,
    // jGrassMapEnvironment
    // .getMapFile().getAbsolutePath()).maptype(JGrassConstants.GRASSBINARYRASTERMAP).monitor(monitor).build();
    // if (!jgrassMapReader.open()) {
    // throw new IOException("An error occurred while reading the map: " +
    // jGrassMapEnvironment.getMapName());
    // }
    // RasterData rasterData = null;
    // if (jgrassMapReader.hasMoreData()) {
    // rasterData = jgrassMapReader.getNextData();
    // }
    // jgrassMapReader.close();
    //
    // return rasterData;
    // }

    // public double getValueInCoordinate( Coordinate coordinate ) throws IOException {
    // JGrassRegion newRegion = JGrassUtilities.getRectangleAroundPoint(getActiveWindow(),
    // coordinate.x, coordinate.y);
    // RasterData rasterData = getDataInRegion(newRegion, null);
    // return rasterData.getValueAt(0, 0);
    // }

    private JGrassRegion getGrassAsciiFileWindow( String filepath ) {
        BufferedReader grassasciireader;
        try {
            grassasciireader = new BufferedReader(new FileReader(filepath));

            // read the header
            String line = null;
            HashMap<String, String> fileMapHeader = new HashMap<String, String>();
            for( int i = 0; i < 8; i++ ) {
                if ((line = grassasciireader.readLine()) != null) {
                    String lowerline = line.toLowerCase().trim();
                    StringTokenizer tok = new StringTokenizer(lowerline, ":"); //$NON-NLS-1$

                    if (tok.countTokens() == 2 && lowerline.indexOf(':') != -1) {
                        String key = tok.nextToken().trim();
                        String value = tok.nextToken().trim();
                        /*
                         * exceptions that sometimes occur can be added here below
                         */
                        if (key.startsWith(JGrassConstants.HEADER_EW_RES)) // could be "e-w resol"
                        {
                            key = JGrassConstants.HEADER_EW_RES;
                        }
                        if (key.startsWith(JGrassConstants.HEADER_NS_RES)) {
                            key = JGrassConstants.HEADER_NS_RES;
                        }
                        fileMapHeader.put(key, value);
                    } else {
                        break;
                    }
                }
            }

            double north = 0.0;
            double south = 0.0;
            double east = 0.0;
            double west = 0.0;
            double xres = 0.0;
            double yres = 0.0;
            int thecols = 0;
            int therows = 0;

            north = Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_NORTH));
            south = Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_SOUTH));
            east = Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_EAST));
            west = Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_WEST));
            if (!fileMapHeader.containsKey(JGrassConstants.HEADER_EW_RES)
                    && !fileMapHeader.containsKey(JGrassConstants.HEADER_NS_RES)) {
                thecols = Integer.parseInt(fileMapHeader.get(JGrassConstants.HEADER_COLS));
                therows = Integer.parseInt(fileMapHeader.get(JGrassConstants.HEADER_ROWS));

                xres = (east - west) / thecols;
                yres = (north - south) / therows;
            } else {
                xres = Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_EW_RES));
                yres = Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_NS_RES));

                therows = (int) ((north - south) / yres);
                thecols = (int) ((east - west) / xres);
            }

            /*
             * Setup file window object that holds the geographic limits of the file data.
             */
            fileWindow = null;
            if (fileMapHeader.containsKey(JGrassConstants.HEADER_NS_RES)) {
                fileWindow = new JGrassRegion(Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_WEST)),
                        Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_EAST)), Double.parseDouble(fileMapHeader
                                .get(JGrassConstants.HEADER_SOUTH)), Double.parseDouble(fileMapHeader
                                .get(JGrassConstants.HEADER_NORTH)), Double.parseDouble(fileMapHeader
                                .get(JGrassConstants.HEADER_EW_RES)), Double.parseDouble(fileMapHeader
                                .get(JGrassConstants.HEADER_NS_RES)));
            } else if (fileMapHeader.containsKey(JGrassConstants.HEADER_COLS)) {
                fileWindow = new JGrassRegion(Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_WEST)),
                        Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_EAST)), Double.parseDouble(fileMapHeader
                                .get(JGrassConstants.HEADER_SOUTH)), Double.parseDouble(fileMapHeader
                                .get(JGrassConstants.HEADER_NORTH)), Integer.parseInt(fileMapHeader
                                .get(JGrassConstants.HEADER_ROWS)), Integer.parseInt(fileMapHeader
                                .get(JGrassConstants.HEADER_COLS)));
            } else {
                fileWindow = null;
            }
            grassasciireader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileWindow;
    }

    private JGrassRegion getEsriGridFileWindow( String filepath ) {
        BufferedReader esriasciireader;
        try {
            esriasciireader = new BufferedReader(new FileReader(filepath));

            // read the header
            String line = null;
            HashMap<String, String> fileMapHeader = new HashMap<String, String>();
            for( int i = 0; i < 8; i++ ) {
                if ((line = esriasciireader.readLine()) != null) {
                    String lowerline = line.toLowerCase().trim();
                    StringTokenizer tok = new StringTokenizer(lowerline);

                    if (tok.countTokens() == 2) {
                        String key = tok.nextToken().trim();
                        String value = tok.nextToken().trim();
                        if (key.startsWith(JGrassConstants.ESRI_HEADER_XLL_PIECE)) {
                            key = JGrassConstants.ESRI_HEADER_XLL;
                        }
                        if (key.startsWith(JGrassConstants.ESRI_HEADER_YLL_PIECE)) {
                            key = JGrassConstants.ESRI_HEADER_YLL;
                        }
                        if (key.startsWith(JGrassConstants.ESRI_HEADER_NROWS_PIECE)) {
                            key = JGrassConstants.ESRI_HEADER_NROWS;
                        }
                        if (key.startsWith(JGrassConstants.ESRI_HEADER_NCOLS_PIECE)) {
                            key = JGrassConstants.ESRI_HEADER_NCOLS;
                        }
                        if (key.startsWith(JGrassConstants.ESRI_HEADER_DIMENSION)
                                || key.startsWith(JGrassConstants.ESRI_HEADER_CELLSIZE)) {
                            key = JGrassConstants.ESRI_HEADER_CELLSIZE;
                        }
                        if (key.startsWith(JGrassConstants.ESRI_HEADER_NOVALUE_PIECE)) {
                            key = JGrassConstants.ESRI_HEADER_NOVALUE;
                        }
                        fileMapHeader.put(key, value);
                    }
                }
            }
            double north = 0.0;
            double south = 0.0;
            double east = 0.0;
            double west = 0.0;
            double xres = 0.0;
            double yres = 0.0;
            int thecols = 0;
            int therows = 0;

            south = Double.parseDouble(fileMapHeader.get(JGrassConstants.ESRI_HEADER_YLL));
            west = Double.parseDouble(fileMapHeader.get(JGrassConstants.ESRI_HEADER_XLL));
            thecols = Integer.parseInt(fileMapHeader.get(JGrassConstants.ESRI_HEADER_NCOLS));
            therows = Integer.parseInt(fileMapHeader.get(JGrassConstants.ESRI_HEADER_NROWS));
            xres = yres = Double.parseDouble(fileMapHeader.get(JGrassConstants.ESRI_HEADER_CELLSIZE));
            east = xres * thecols + west;
            north = yres * therows + south;

            /*
             * Setup file window object that holds the geographic limits of the file data.
             */
            fileWindow = new JGrassRegion(west, east, south, north, xres, yres);
            esriasciireader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileWindow;
    }

    @SuppressWarnings("nls")
    private JGrassRegion getFluidturtleFileWindow( String filepath ) {
        HashMap<String, String> fileMapHeader = new HashMap<String, String>();
        /* Read contents of 'cellhd/name' file from the current mapset */
        String line = null;
        try {
            //
            BufferedReader fluidasciireader = new BufferedReader(new FileReader(filepath));
            // read the header
            boolean readheader = false;
            while( !readheader ) {
                if ((line = fluidasciireader.readLine()) != null) {
                    if (line.toLowerCase().trim().startsWith("index")) {
                        String[] index = line.split("\\{");
                        index[1] = index[1].split("\\}")[0].trim();

                        int numberofBlocks = Integer.parseInt(index[1].split("\\,")[0].trim());

                        for( int i = 0; i < numberofBlocks; i++ ) {
                            while( !readheader ) {
                                if ((line = fluidasciireader.readLine()) != null) {
                                    if (line.toLowerCase().trim().startsWith((i + 1) + ":")) {

                                        if (line.indexOf("novalue") != -1)
                                        // defines the novalue
                                        {
                                            // String[] noVal = line.split("\\{");
                                            // String[] tmp = noVal[1].trim().split("\\}");
                                            // String[] noValues = tmp[0].trim().split(",");
                                            // Double novalue = new Double(Double
                                            // .parseDouble(noValues[1].trim()));
                                            break;
                                        } else if (line.indexOf("matrix") != -1)
                                        // defines the data matrix rows and cols
                                        {
                                            String[] matrix = line.split("\\{");
                                            String[] tmp = matrix[1].trim().split("\\}");
                                            String[] rowsandcols = tmp[0].trim().split(",");

                                            fileMapHeader.put(JGrassConstants.HEADER_ROWS, rowsandcols[0].trim());
                                            fileMapHeader.put(JGrassConstants.HEADER_COLS, rowsandcols[1].trim());
                                            break;
                                        } else {

                                            String[] demHeader = line.split("\\{");
                                            String[] tmp = demHeader[1].trim().split("\\}");
                                            String[] values = tmp[0].trim().split(",");

                                            fileMapHeader.put(JGrassConstants.HEADER_EW_RES, values[0].trim());
                                            fileMapHeader.put(JGrassConstants.HEADER_NS_RES, values[1].trim());
                                            fileMapHeader.put(JGrassConstants.HEADER_SOUTH, values[2].trim());
                                            fileMapHeader.put(JGrassConstants.HEADER_WEST, values[3].trim());
                                            break;
                                        }

                                    }
                                }
                            }
                        }
                        readheader = true;
                    }

                }
            }

            double north = 0.0;
            double south = 0.0;
            double east = 0.0;
            double west = 0.0;
            double xres = 0.0;
            double yres = 0.0;
            int thecols = 0;
            int therows = 0;

            south = Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_SOUTH));
            west = Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_WEST));
            therows = Integer.parseInt(fileMapHeader.get(JGrassConstants.HEADER_ROWS));
            thecols = Integer.parseInt(fileMapHeader.get(JGrassConstants.HEADER_COLS));
            xres = Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_EW_RES));
            yres = Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_NS_RES));
            north = south + therows * yres;
            east = west + thecols * xres;

            therows = (int) ((north - south) / yres);
            thecols = (int) ((east - west) / xres);

            /*
             * Setup file window object that holds the geographic limits of the file data.
             */
            fileWindow = null;
            if (fileMapHeader.containsKey(JGrassConstants.HEADER_NS_RES)) {
                fileWindow = new JGrassRegion(Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_WEST)), east,
                        Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_SOUTH)), north,
                        Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_EW_RES)), Double.parseDouble(fileMapHeader
                                .get(JGrassConstants.HEADER_NS_RES)));
            } else if (fileMapHeader.containsKey(JGrassConstants.HEADER_COLS)) {
                fileWindow = new JGrassRegion(Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_WEST)), east,
                        Double.parseDouble(fileMapHeader.get(JGrassConstants.HEADER_SOUTH)), north,
                        Integer.parseInt(fileMapHeader.get(JGrassConstants.HEADER_ROWS)), Integer.parseInt(fileMapHeader
                                .get(JGrassConstants.HEADER_COLS)));
            } else {
                fileWindow = null;
            }
            fluidasciireader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileWindow;
    }

    protected IGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        // support concurrent access
        synchronized (this) {
            if (info == null) {
                info = new JGrassMapGeoResourceInfo(monitor);
            }
        }

        return info;
    }

}
