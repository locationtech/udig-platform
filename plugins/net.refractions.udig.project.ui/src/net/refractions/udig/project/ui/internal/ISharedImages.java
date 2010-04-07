/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal;

import static net.refractions.udig.core.internal.Icons.DLOCALTOOL;
import static net.refractions.udig.core.internal.Icons.ELOCALTOOL;
import static net.refractions.udig.core.internal.Icons.ETOOL;
import static net.refractions.udig.core.internal.Icons.OBJECT;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * A registry for common images which may be useful to other plug-ins.
 * <p>
 * This lists the <code>ImageDescriptor</code>s that are available via
 * RegistryPlugin.getImageResource(). The fact that a constant is mentioned here, by convention,
 * makes the associated Image part of the Plug-In API.
 * </p>
 * To use one of these images:
 * 
 * <pre><code>
 * ImageRegistry images = RegistryPlugin.getDefault().getImageRegistry();
 * ImageDescriptor image = images.getDescriptor(ISharedImages.IMG_DATASTORE_OBJ);
 * </code></pre>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ISharedImages {
    /**
     * Represent a Map object.
     */
    public final static String MAP_OBJ = OBJECT + "map_obj.gif"; //$NON-NLS-1$

    /**
     * Represent a Page object.
     */
    public final static String PAGE_OBJ = OBJECT + "page_obj.gif"; //$NON-NLS-1$
    /**
     * Represents a Layer object
     */
    public static final String LAYER_OBJ = OBJECT + "layer_obj.gif"; //$NON-NLS-1$;

    /**
     * Represents a Map folder object
     */
    public static final String MAP_FOLDER_OBJ = OBJECT + "mapfolder_obj.gif"; //$NON-NLS-1$;

    /**
     * Represent a Project.
     */
    public final static String PROJECT_OBJ = OBJECT + "project_obj.gif"; //$NON-NLS-1$

    /** Represents the "Collapse all branches of tree" action */
    public static final String COLLAPSE_ALL = ELOCALTOOL + "collapseall_co.gif"; //$NON-NLS-1$;

    /** Represents the "Link editor focus to Project Explorer" action */
    public static final String LINK = ELOCALTOOL + "link_co.gif"; //$NON-NLS-1$;;

    /** Represents the "Link editor focus to Project Explorer" action */
    public static final String ACTIVE_LINK = ELOCALTOOL + "link_on_co.gif"; //$NON-NLS-1$;;

    /** Represents the "Link editor focus to Project Explorer" action */
    public static final String ADD_CO = ELOCALTOOL + "add_co.gif"; //$NON-NLS-1$;;

    /** Represents the "Collapse all branches of tree" action */
    public static final String D_COLLAPSE_ALL = DLOCALTOOL + "collapseall_co.gif"; //$NON-NLS-1$;

    /** Represents the "Collapse all branches of tree" action */
    public static final String D_ADD_CO = DLOCALTOOL + "add_co.gif"; //$NON-NLS-1$;

    /** Represents the "Link editor focus to Project Explorer" action */
    public static final String D_LINK = DLOCALTOOL + "link_co.gif"; //$NON-NLS-1$;;

    /** Represents the "Start new Project wizard" action */
    public static final String NEW_PROJECT = ETOOL + "newprj_wiz.gif"; //$NON-NLS-1$
    /**
     * Returns the image descriptor for the given image ID. Returns null if there is no such image.
     * 
     * @param id Constant from ISharedImages
     * @return ImageDescriptor locating resource associated with id
     */
    public ImageDescriptor getImageDescriptor( String id );

}