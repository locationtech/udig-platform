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
package org.locationtech.udig.core.internal;

/**
 * Image Directory constants used for making Images, ISharedImages and so on.
 * <p>
 * Example use when defining Constants (ISharedImages or ImageConstants):
 *
 * <pre><code>
 * interface ISharedImages extends ImagePathConstants {
 *     public static final String ADD_CO = PATH_ELOCALTOOL + &quot;add_co.gif&quot;;
 * }
 * </code></pre>
 *
 * </p>
 *
 * @author Jody Garnett, Refractions Research
 */
public interface Icons {

    /**
     * Enabled toolbar icons
     */
    public static final String ETOOL = "etool16/"; //$NON-NLS-1$

    /**
     * Disabled toolbar icons.
     */
    public static final String DTOOL = "dtool16/"; //$NON-NLS-1$

    /**
     * Enabled local toolbar icons.
     */
    public static final String ELOCALTOOL = "elcl16/"; //$NON-NLS-1$

    /**
     * Disabled local toolbar icons
     */
    public static final String DLOCALTOOL = "dlcl16/"; //$NON-NLS-1$

    /**
     * View icons
     */
    public static final String EVIEW = "eview16/"; //$NON-NLS-1$

    /**
     * Product images
     */
    // public static final String PATH_PROD = "prod/"; //$NON-NLS-1$
    /**
     * Model object icons
     */
    public static final String OBJECT = "obj16/"; //$NON-NLS-1$

    /**
     * Pointer icons
     */
    public static final String POINTER = "pointer/"; //$NON-NLS-1$

    /**
     * Wizard banners
     */
    public static final String WIZBAN = "wizban/"; //$NON-NLS-1$

    /**
     * Misc icons
     */
    public static final String MISC = "misc/"; //$NON-NLS-1$

    /**
     * icons Overlays
     */
    public static final String OVERLAY = "ovr16/"; //$NON-NLS-1$

}
