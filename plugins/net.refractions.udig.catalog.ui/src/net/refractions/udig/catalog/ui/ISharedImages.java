/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.catalog.ui;

import static net.refractions.udig.core.internal.Icons.OBJECT;
import static net.refractions.udig.core.internal.Icons.OVERLAY;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
/**
 * A registry for common images which may be useful to other plug-ins.
 * <p>
 * This lists the <code>ImageDescriptor</code>s that are available via
 * RegistryUI.getImageResource(). The fact that a constant is mentioned here, by convention, makes
 * the associated Image part of the Plug-In api.
 * </p>
 * To use one of these images:
 * 
 * <pre><code>
 * ImageRegistry images = CatalogUIPlugin.getDefault().getImageRegistry();
 * ImageDescriptor image = images.getDescriptor(ISharedImages.IMG_DATASTORE_OBJ);
 * </code></pre>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ISharedImages {
    /**
     * Graphic representing a all generated map graphics
     */
    public final static String MAP_GRAPHICS_OBJ = OBJECT + "map_graphics_obj.gif"; //$NON-NLS-1$

    /**
     * Graphic representing a generated graphic
     */
    public final static String GRAPHIC_OBJ = OBJECT + "graphic_obj.gif"; //$NON-NLS-1$

    /**
     * Graphic representing a generated folder
     */
    public final static String FOLDER_OBJ = OBJECT + "mapfolder_obj.gif"; //$NON-NLS-1$

    /**
     * Generic catalog glyph
     */
    public final static String CATALOG_OBJ = OBJECT + "repository_obj.gif"; //$NON-NLS-1$
    /**
     * Shared Image representing a database.
     */
    public final static String DATABASE_OBJ = OBJECT + "database_obj.gif"; //$NON-NLS-1$
    /**
     * Shared Image representing a Datastore (generic feature information).
     */
    public final static String DATASTORE_OBJ = OBJECT + "datastore_obj.gif"; //$NON-NLS-1$
    /**
     * Represent a data source with an erro condition.
     */
    public final static String ERROR_OVR = OVERLAY + "error_ovr.gif"; //$NON-NLS-1$
    /**
     * Graphic representing a SimpleFeature File
     */
    public final static String FEATURE_FILE_OBJ = OBJECT + "feature_file_obj.gif"; //$NON-NLS-1$
    /**
     * Graphic representing a SimpleFeature (default for an indivudal IGeoResource)
     */
    public final static String FEATURE_OBJ = OBJECT + "feature_obj.gif"; //$NON-NLS-1$
    /**
     * Generic storage glyph.
     */
    public final static String FILE_OBJ = OBJECT + "file_obj.gif"; //$NON-NLS-1$
    /**
     * Shared Image representing a Grid Coverage Exchange (generic raster information).
     */
    public final static String GCE_OBJ = OBJECT + "gce_obj.gif"; //$NON-NLS-1$

    /**
     * Graphic representing a Grid based file format
     */
    public final static String GRID_FILE_OBJ = OBJECT + "grid_file_obj.gif"; //$NON-NLS-1$

    /**
     * Graphic representing a Grid (default for an indivudal IGeoResource)
     */
    public final static String GRID_OBJ = OBJECT + "grid_obj.gif"; //$NON-NLS-1$
    public final static String GRID_MISSING = OBJECT + "grid_missing_obj.gif"; //$NON-NLS-1$    
    /**
     * Graphic representing a Grid (default for an indivudal IGeoResource)
     */
    public final static String PIXEL_OBJ = OBJECT + "pixel_obj.gif"; //$NON-NLS-1$

    /**
     * Generic storage glyph.
     */
    public final static String MEMORY_OBJ = OBJECT + "memory_obj.gif"; //$NON-NLS-1$
    /**
     * Palette of map graphics (synthetic content)
     */
    public final static String PALETTE_OBJ = OBJECT + "palette_obj.gif"; //$NON-NLS-1$
    /**
     * Graphic representing a Grid (default for an indivudal IGeoResource)
     */
    public final static String RESOURCE_OBJ = OBJECT + "resource_obj.gif"; //$NON-NLS-1$
    /**
     * Generic external provider of information.
     */
    public final static String SERVER_OBJ = OBJECT + "server_obj.gif"; //$NON-NLS-1$
    /**
     * Represent a data source waiting for status information.
     */
    public final static String WAIT_OVR = OVERLAY + "wait_ovr.gif"; //$NON-NLS-1$
    /**
     * Represents a data source with a warning codition.
     * <p>
     * A warning means that the system is making an assumption. The user should be able to edit the
     * data source and fix the problem.
     * </p>
     * <p>
     * The context menu could provide a list of suggested fixes, or if there is only one it should
     * probably just do it. An example of this would be getting back a redirect for WFS url. The fix
     * would be to remember the redirect (rather than the origional). This is the kind of thing that
     * should just happen.
     * </p>
     */
    public final static String WARNING_OVR = OVERLAY + "warning_ovr.gif"; //$NON-NLS-1$
    /**
     * Web SimpleFeature Server is a standards based external source of feature information.
     */
    public final static String WFS_OBJ = OBJECT + "wfs_obj.gif"; //$NON-NLS-1$
    /**
     * Web Map Server is a standards based external source of raster information.
     */
    public final static String WMS_OBJ = OBJECT + "wms_obj.gif"; //$NON-NLS-1$

    /**
     * Wen Registry Service registry (a OWS Catalog)
     */
    public final static String WRS_OBJ = OBJECT + "wrs_obj.gif"; //$NON-NLS-1$

    /**
     * Returns shared image for the given image ID. Returns null if there is no such image.
     * 
     * @param id Constant from ISharedImages
     * @return Shared image for image ID, do plugin will manage disposal.
     */
    public Image get( String id );

    /**
     * Returns the image descriptor for the given image ID. Returns null if there is no such image.
     * 
     * @param id Constant from ISharedImages
     * @return ImageDescriptor locating resource associated with id
     */
    public ImageDescriptor getImageDescriptor( String id );
}