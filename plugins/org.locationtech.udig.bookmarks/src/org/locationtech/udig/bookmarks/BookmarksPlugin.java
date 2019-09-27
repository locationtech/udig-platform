/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2006, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.bookmarks;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.locationtech.udig.bookmarks.internal.MapReference;

import org.locationtech.jts.geom.Envelope;

/**
 * Plugin callback object for the bookmark plugin.
 */
public class BookmarksPlugin extends AbstractUIPlugin {
    public static String ID = "org.locationtech.udig.bookmarks";

    private static final String KEY_NAME = "name"; //$NON-NLS-1$
    private static final String KEY_MINX = "minx"; //$NON-NLS-1$
    private static final String KEY_MINY = "miny"; //$NON-NLS-1$
    private static final String KEY_MAXX = "maxx"; //$NON-NLS-1$
    private static final String KEY_MAXY = "maxy"; //$NON-NLS-1$
    private static final String KEY_CRS = "crs"; //$NON-NLS-1$

    // The shared instance.
    private static BookmarksPlugin plugin;

    /**
     * The constructor.
     */
    public BookmarksPlugin() {
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        restoreFromPreferences();
    }

    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop( BundleContext context ) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     * 
     * @return The instance of this plugin
     */
    public static BookmarksPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public ImageDescriptor getImageDescriptor( String path ) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(plugin.getBundle().getSymbolicName(),
                path);
    }

    /**
     * Restore the bookmarks from the plugin's preference store
     * 
     * @throws BackingStoreException
     */
    public void restoreFromPreferences() throws BackingStoreException {
        IPreferencesService prefs = Platform.getPreferencesService();
        IEclipsePreferences root = prefs.getRootNode();
        Preferences node = root.node(InstanceScope.SCOPE).node(
                getBundle().getSymbolicName() + ".bookmarks"); //$NON-NLS-1$

        for( String projectId : node.childrenNames() ) {
            URI projectURI = URI.createURI(URI.decode(projectId));
            Preferences projectNode = node.node(projectId);
            for( String mapId : projectNode.childrenNames() ) {
                URI mapURI = URI.createURI(URI.decode(mapId));
                Preferences mapNode = projectNode.node(mapId);
                String mapName = mapNode.get(KEY_NAME, null);
                for( String bmarkName : mapNode.childrenNames() ) {
                    Preferences bmarkNode = mapNode.node(bmarkName);
                    double minx = bmarkNode.getDouble(KEY_MINX, 0.0);
                    double miny = bmarkNode.getDouble(KEY_MINY, 0.0);
                    double maxx = bmarkNode.getDouble(KEY_MAXX, 0.0);
                    double maxy = bmarkNode.getDouble(KEY_MAXY, 0.0);
                    Envelope env = new Envelope(minx, maxx, miny, maxy);
                    CoordinateReferenceSystem crs;
                    String crsString = bmarkNode.get(KEY_CRS, ""); //$NON-NLS-1$
                    try {
                        crs = CRS.parseWKT(crsString);
                    } catch (NoSuchAuthorityCodeException e) {
                        crs = DefaultGeographicCRS.WGS84;
                    } catch (FactoryException e) {
                        crs = DefaultGeographicCRS.WGS84;
                    }
                    ReferencedEnvelope bounds = new ReferencedEnvelope(env, crs);
                    Bookmark bmark = new Bookmark(bounds, new MapReference(mapURI, projectURI,
                            mapName), URI.decode(bmarkName));
                    getBookmarkService().load(bmark);
                }
            }
        }

    }

    /**
     * Stores the bookmarks to the plugin's preference store
     * 
     * @throws BackingStoreException
     */
    public void storeToPreferences() throws BackingStoreException {
        IBookmarkService mgr = getBookmarkService();
        if (mgr == null) {
            return; // nothing to save!
        }
        IPreferencesService prefs = Platform.getPreferencesService();
        IEclipsePreferences root = prefs.getRootNode();
        Preferences node = root.node(InstanceScope.SCOPE).node(
                getBundle().getSymbolicName() + ".bookmarks"); //$NON-NLS-1$
        clearPreferences(node);
        for( URI project : mgr.getProjects() ) {
            String projectString = project.toString();
            String encPStr = URI.encodeSegment(projectString, true);
            Preferences projectNode = node.node(encPStr);
            for( MapReference map : mgr.getMaps(project) ) {
                Preferences mapNode = projectNode.node(URI.encodeSegment(map.getMapID().toString(),
                        true));
                mapNode.put(KEY_NAME, map.getName());
                for( IBookmark bookmark : mgr.getBookmarks(map) ) {
                    Preferences bmarkNode = mapNode
                            .node(URI.encodeSegment(bookmark.getName(), true));
                    ReferencedEnvelope bounds = bookmark.getEnvelope();
                    bmarkNode.putDouble(KEY_MINX, bounds.getMinX());
                    bmarkNode.putDouble(KEY_MINY, bounds.getMinY());
                    bmarkNode.putDouble(KEY_MAXX, bounds.getMaxX());
                    bmarkNode.putDouble(KEY_MAXY, bounds.getMaxY());
                    bmarkNode.put(KEY_CRS, bounds.getCoordinateReferenceSystem().toWKT());
                }
                mapNode.flush();
            }
            projectNode.flush();
        }
        node.flush();
    }

    private void clearPreferences( Preferences node ) throws BackingStoreException {
        for( String name : node.childrenNames() ) {
            Preferences child = node.node(name);
            child.removeNode();
        }
    }

    /** Access the IBookmarkService for the current workbench */
    public static IBookmarkService getBookmarkService() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IBookmarkService bookmarkService = (IBookmarkService) workbench
                .getService(IBookmarkService.class);
        return bookmarkService;
    }
}
