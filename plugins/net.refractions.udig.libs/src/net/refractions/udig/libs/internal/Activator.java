package net.refractions.udig.libs.internal;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.imageio.spi.ImageReaderSpi;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.factory.Hints.Key;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.GeneralDirectPosition;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.factory.PropertyAuthorityFactory;
import org.geotools.referencing.factory.ReferencingFactoryContainer;
import org.geotools.resources.image.ImageUtilities;
import org.geotools.util.logging.Logging;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The Activator for net.refractions.udig.libs provides global settings to help all the open source
 * projects get along.
 * <p>
 * Currently this activator supplied:
 * <ul>
 * <li>hints about axis order for GeoTools;
 * <li>instructs java not to use native PNG support; see UDIG-1391 for details
 * </ul>
 * <p>
 * The contents of this Activator will change over time according to the needs of the libraries and
 * tool kits we are using.
 * </p>
 * 
 * @author Jody Garnett
 * @since 1.1.0
 */
public class Activator implements BundleActivator {

    public static String ID = "net.refractions.udig.libs"; //$NON-NLS-1$
    public static String JDBC_DATA_TRACE_FINE = "net.refractions.udig.libs/debug/data/jdbc/fine";
    public static String JDBC_TRACE_FINE = "net.refractions.udig.libs/debug/jdbc/fine";

    public void start( final BundleContext context ) throws Exception {
        if (Platform.getOS().equals(Platform.OS_WIN32)) {
            try {
                // PNG native support is not very good .. this turns it off
                ImageUtilities.allowNativeCodec("png", ImageReaderSpi.class, false); //$NON-NLS-1$
            } catch (Throwable t) {
                // we should not die if JAI is missing; we have a warning for that...
                System.out
                        .println("Difficulty turnning windows native PNG support (which will result in scrambled images from WMS servers)"); //$NON-NLS-1$
                t.printStackTrace();
            }
        }

        // System properites work for controlling referencing behavior
        // not so sure about the geotools global hints
        //
        System.setProperty("org.geotools.referencing.forceXY", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        Map<Key, Boolean> map = new HashMap<Key, Boolean>();
        // these commented out hints are covered by the forceXY system property
        //
        // map.put( Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, true );
        // map.put( Hints.FORCE_STANDARD_AXIS_DIRECTIONS, true );
        // map.put( Hints.FORCE_STANDARD_AXIS_UNITS, true );
        map.put(Hints.LENIENT_DATUM_SHIFT, true);
        Hints global = new Hints(map);
        GeoTools.init(global);
        
//        ClassLoader cl = Thread.currentThread().getContextClassLoader();
//        Thread.currentThread().setContextClassLoader(GeoTools.class.getClassLoader());
//        try {
        Logger jdbcLogger = Logging.getLogger("org.geotools.jdbc");
        Logger jdbcDataLogger = Logging.getLogger("org.geotools.data.jdbc");
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        
        Logging.getLogger("org.geotools").addHandler(handler);

        if (isDebugging(JDBC_TRACE_FINE)) {

        	jdbcLogger.setLevel(Level.FINEST);
        } else {
        	jdbcLogger.setLevel(Level.INFO);
        }
        if (isDebugging(JDBC_DATA_TRACE_FINE)) {
        	jdbcDataLogger.setLevel(Level.FINEST);
        } else {
        	jdbcDataLogger.setLevel(Level.INFO);
        }
//        } finally {
//        	Thread.currentThread().setContextClassLoader(cl);
//        }
        
        // We cannot do this here - it takes too long!
        // Early startup is too late
        // functionality moved to the UDIGApplication init method
        //
        // initializeReferencingModule( context.getBundle(), null );
    }
    public static boolean isDebugging(final String trace) {
        return isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$    
    }

    public static boolean isDebugging() {
        Bundle bundle = Platform.getBundle(ID);
        String key = bundle.getSymbolicName() + "/debug"; //$NON-NLS-1$
        // first check if platform debugging is enabled
        BundleContext context = bundle.getBundleContext();
        if (context == null) {
            return false;
        }
        ServiceTracker debugTracker = new ServiceTracker(context, DebugOptions.class.getName(),
                null);
        debugTracker.open();

        DebugOptions debugOptions = (DebugOptions) debugTracker.getService();
        if (debugOptions == null) {
            return false;
        }
        // if platform debugging is enabled, check to see if this plugin is enabled for debugging
        return debugOptions.isDebugEnabled() ? InternalPlatform.getDefault().getBooleanOption(key,
                false) : false;
    }

    public static void initializeReferencingModule( IProgressMonitor monitor ) {
        Bundle bundle = Platform.getBundle(ID);
        if (monitor == null)
            monitor = new NullProgressMonitor();

        monitor.beginTask(Messages.Activator_EPSG_DATABASE, 100);

        searchEPSGProperties(bundle, new SubProgressMonitor(monitor, 20));

        loadEPSG(bundle, new SubProgressMonitor(monitor, 60));

        monitor.subTask(Messages.OPERATIONS_DEFINITIONS);
        load(ReferencingFactoryFinder.getCoordinateOperationAuthorityFactories(null));
        monitor.worked(2);

        monitor.subTask(Messages.COORDINATE_REFERENCE_SYSTSMS);
        load(ReferencingFactoryFinder.getCRSFactories(null));
        monitor.worked(8);

        monitor.subTask(Messages.COORDINATE_SYSTEMS);
        load(ReferencingFactoryFinder.getCSFactories(null));
        monitor.worked(2);

        monitor.subTask(Messages.DATUM_DEFINITIONS);
        load(ReferencingFactoryFinder.getDatumAuthorityFactories(null));
        monitor.worked(2);

        monitor.subTask(Messages.DATUMS);
        load(ReferencingFactoryFinder.getDatumFactories(null));
        monitor.worked(2);

        monitor.subTask(Messages.MATH_TRANSFORMS);
        load(ReferencingFactoryFinder.getMathTransformFactories(null));
        monitor.worked(4);
    }
    @SuppressWarnings("unchecked")
    static private void load( Set coordinateOperationAuthorityFactories ) {
        for( Iterator iter = coordinateOperationAuthorityFactories.iterator(); iter.hasNext(); ) {
            iter.next();
        }
    }

    /**
     * Will load the EPSG database; this will trigger the unpacking of the EPSG database (which may
     * take several minutes); and check in a few locations for an epsg.properties file to load: the
     * locations are the data directory; the configuration directory; and finally the libs plugin
     * bundle itself (which includes a default epsg.properties file that has a few common unofficial
     * codes for things like the google projection).
     * <p>
     * This method will trigger the geotools referencing module to "scanForPlugins" and MUST be
     * called prior to using the geotools library for anything real. I am sorry we could not arrange
     * for this method to be called in an Activator as it simple takes too long and the Platform
     * get's mad at us.
     * 
     * @param bundle
     * @param monitor
     */
    public static void searchEPSGProperties( Bundle bundle, IProgressMonitor monitor ) {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        monitor.beginTask(Messages.EPSG_SETUP, IProgressMonitor.UNKNOWN);
        try {
            // go through and check a couple of locations
            // for an "epsg.properties" file full of
            // suplementary codes
            //
            URL epsg = null;
            Location configLocaiton = Platform.getInstallLocation();
            Location dataLocation = Platform.getInstanceLocation();
            if (dataLocation != null) {
                try {
                    URL url = dataLocation.getURL();
                    URL proposed = new URL(url, "epsg.properties"); //$NON-NLS-1$
                    monitor.subTask(Messages.CHECK + proposed);
                    String externalForm = proposed.toExternalForm();
                    if ("file".equals(proposed.getProtocol())) { //$NON-NLS-1$
                        String path = externalForm.replaceFirst("file:", "");  //$NON-NLS-1$//$NON-NLS-2$
                        File file = new File(path);
                        if (file.exists()) {
                            epsg = file.toURI().toURL();
                        }
                    }
                    monitor.worked(1);
                } catch (Throwable t) {
                    if (isDebugging()) {
                        System.out.println("Could not find data directory epsg.properties"); //$NON-NLS-1$
                        t.printStackTrace();
                    }
                }
            }
            if (epsg == null && configLocaiton != null) {
                try {
                    URL url = configLocaiton.getURL();
                    URL proposed = new URL(url, "epsg.properties"); //$NON-NLS-1$
                    String externalForm = proposed.toExternalForm();
                    monitor.subTask(Messages.Activator_1 + proposed);
                    if ("file".equals(proposed.getProtocol())) { //$NON-NLS-1$
                        String path = externalForm.replaceFirst("file:", "");  //$NON-NLS-1$//$NON-NLS-2$
                        File file = new File(path);
                        if (file.exists()) {
                            epsg = file.toURI().toURL();
                        }
                    }
                    monitor.worked(1);
                } catch (Throwable t) {
                    if (isDebugging()) {
                        System.out.println("Could not find configuration epsg.properties"); //$NON-NLS-1$
                        t.printStackTrace();
                    }
                }
            }
            if (epsg == null) {
                try {
                    URL internal = bundle.getEntry("epsg.properties"); //$NON-NLS-1$
                    URL fileUrl = FileLocator.toFileURL(internal);
                    String externalForm = fileUrl.toExternalForm();
                    String path = externalForm.replaceFirst("file:", "");  //$NON-NLS-1$//$NON-NLS-2$
                    epsg = new File(path).toURI().toURL();
                } catch (Throwable t) {
                    if (Platform.inDebugMode()) {
                        System.out
                                .println("Could not find net.refractions.udig.libs/epsg.properties"); //$NON-NLS-1$
                        t.printStackTrace();
                    }
                }
            }

            if (epsg != null) {
                monitor.subTask(Messages.LOADING + epsg);
                Hints hints = new Hints(Hints.CRS_AUTHORITY_FACTORY, PropertyAuthorityFactory.class);
                ReferencingFactoryContainer referencingFactoryContainer = ReferencingFactoryContainer
                        .instance(hints);

                PropertyAuthorityFactory factory = new PropertyAuthorityFactory(
                        referencingFactoryContainer, Citations.fromName("EPSG"), epsg); //$NON-NLS-1$

                ReferencingFactoryFinder.addAuthorityFactory(factory);
                monitor.worked(1);

                monitor.subTask(Messages.REGISTER + epsg);
                ReferencingFactoryFinder.scanForPlugins(); // hook everything up
                monitor.worked(10);
            }

            monitor.subTask(Messages.PLEASE_WAIT);
            CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326"); //$NON-NLS-1$
            if (wgs84 == null) {
                String msg = "Unable to locate EPSG authority for EPSG:4326; consider removing temporary 'GeoTools' directory and trying again."; //$NON-NLS-1$
                System.out.println(msg);
                // throw new FactoryException(msg);
            }
            monitor.worked(1);

            // Show EPSG authority chain if in debug mode
            // 
            if (isDebugging()) {
                //CRS.main(new String[]{"-dependencies"}); //$NON-NLS-1$
            }
            // Verify EPSG authority configured correctly
            // if we are in development mode
            if (isDebugging()) {
                monitor.subTask("verify epsg definitions"); //$NON-NLS-1$
                verifyReferencingEpsg();
                monitor.subTask("verify epsg operations"); //$NON-NLS-1$
                verifyReferencingOperation();
            }
        } catch (Throwable t) {
            Platform.getLog(bundle).log(
                    new Status(Status.ERROR, Activator.ID, t.getLocalizedMessage(), t));
        } finally {
            monitor.done();
        }
    }

    public static void loadEPSG( Bundle bundle, IProgressMonitor monitor ) {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        monitor.beginTask(Messages.EPSG_SETUP, IProgressMonitor.UNKNOWN);
        try {
            monitor.subTask(Messages.PLEASE_WAIT);
            CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326"); //$NON-NLS-1$
            if (wgs84 == null) {
                String msg = "Unable to locate EPSG authority for EPSG:4326; consider removing temporary 'GeoTools' directory and trying again."; //$NON-NLS-1$
                System.out.println(msg);
                // throw new FactoryException(msg);
            }
            monitor.worked(1);

            // Show EPSG authority chain if in debug mode
            //
            if (Platform.inDebugMode()) {
                CRS.main(new String[]{"-dependencies"}); //$NON-NLS-1$
            }
            // Verify EPSG authority configured correctly
            // if we are in development mode
            if (Platform.inDevelopmentMode()) {
                monitor.subTask("verify epsg definitions"); //$NON-NLS-1$
                verifyReferencingEpsg();
                monitor.subTask("verify epsg operations"); //$NON-NLS-1$
                verifyReferencingOperation();
            }
        } catch (Throwable t) {
            Platform.getLog(bundle).log(
                    new Status(Status.ERROR, Activator.ID, t.getLocalizedMessage(), t));
        } finally {
            monitor.done();
        }
    }

    /**
     * If this method fails it's because, the epsg jar is either not available, or not set up to
     * handle math transforms in the manner udig expects.
     * 
     * @return true if referencing is working and we get the expected result
     * @throws Exception if we cannot even get that far
     */
    private static void verifyReferencingEpsg() throws Exception {
        CoordinateReferenceSystem WGS84 = CRS.decode("EPSG:4326"); // latlong //$NON-NLS-1$
        CoordinateReferenceSystem BC_ALBERS = CRS.decode("EPSG:3005"); //$NON-NLS-1$

        MathTransform transform = CRS.findMathTransform(BC_ALBERS, WGS84);
        DirectPosition here = new DirectPosition2D(BC_ALBERS, 1187128, 395268);
        DirectPosition there = new DirectPosition2D(WGS84, -123.47009173007372, 48.54326498732153);

        DirectPosition check = transform.transform(here, new GeneralDirectPosition(WGS84));
        // DirectPosition doubleCheck = transform.inverse().transform( check, new
        // GeneralDirectPosition(BC_ALBERS) );
        // if( !check.equals(there)){
        // String msg =
        // "Referencing failed to produce expected transformation; check that axis order settings are correct.";
        // System.out.println( msg );
        // //throw new FactoryException(msg);
        // }
        double delta = Math.abs(check.getOrdinate(0) - there.getOrdinate(0))
                + Math.abs(check.getOrdinate(1) - there.getOrdinate(1));
        if (delta > 0.0001) {
            String msg = "Referencing failed to transformation with expected accuracy: Off by " + delta + "\n" + check + "\n" + there; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            System.out.println(msg);
            // throw new FactoryException(msg);
        }
    }
    /**
     * If this method fails it's because, the epsg jar is either not available, or not set up to
     * handle math transforms in the manner udig expects.
     * 
     * @return true if referencing is working and we get the expected result
     * @throws Exception if we cannot even get that far
     */
    private static void verifyReferencingOperation() throws Exception {
        // ReferencedEnvelope[-0.24291497975705742 : 0.24291497975711265, -0.5056179775280899 :
        // -0.0]
        // ReferencedEnvelope[-0.24291497975705742 : 0.24291497975711265, -0.5056179775280899 :
        // -0.0]
        CoordinateReferenceSystem EPSG4326 = CRS.decode("EPSG:4326"); //$NON-NLS-1$
        ReferencedEnvelope pixelBounds = new ReferencedEnvelope(-0.24291497975705742,
                0.24291497975711265, -0.5056179775280899, 0.0, EPSG4326);
        CoordinateReferenceSystem WGS84 = DefaultGeographicCRS.WGS84;

        ReferencedEnvelope latLong = pixelBounds.transform(WGS84, false);
        if (latLong == null) {
            String msg = "Unable to transform EPSG:4326 to DefaultGeographicCRS.WGS84"; //$NON-NLS-1$
            System.out.println(msg);
            // throw new FactoryException(msg);
        }
    }

    public void stop( BundleContext context ) throws Exception {
    }

}
