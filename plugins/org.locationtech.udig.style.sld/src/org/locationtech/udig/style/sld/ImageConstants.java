/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld;

public interface ImageConstants {

    /** view icons */
  	public static final String PATH_EVIEW = "eview16/"; //$NON-NLS-1$

  	/** colorblind-friendly icon */
  	public static final String COLORBLIND_ICON = PATH_EVIEW + "colorblind_friendly_mode.gif"; //$NON-NLS-1$
    /** crt-friendly icon */
    public static final String CRT_ICON = PATH_EVIEW + "crt_friendly_mode.gif"; //$NON-NLS-1$
    /** laptop-friendly icon */
    public static final String LAPTOP_ICON = PATH_EVIEW + "laptop_friendly_mode.gif"; //$NON-NLS-1$
    /** photocopier-friendly icon */
    public static final String PHOTOCOPY_ICON = PATH_EVIEW + "photocopy_friendly_mode.gif"; //$NON-NLS-1$
    /** printer-friendly icon */
    public static final String PRINTER_ICON = PATH_EVIEW + "printer_friendly_mode.gif"; //$NON-NLS-1$
    /** projector-friendly icon */
    public static final String PROJECTOR_ICON = PATH_EVIEW + "projector_friendly_mode.gif"; //$NON-NLS-1$

    /** overlays */
    public static final String PATH_OVR = "ovr16/"; //$NON-NLS-1$

    /** suitability=good overlay */
    public static final String GOOD_OVERLAY = PATH_OVR + "good_ovr.gif"; //$NON-NLS-1$
    /** suitability=good overlay */
    public static final String DOUBTFUL_OVERLAY = PATH_OVR + "doubtful_ovr.gif"; //$NON-NLS-1$
    /** suitability=good overlay */
    public static final String BAD_OVERLAY = PATH_OVR + "bad_ovr.gif"; //$NON-NLS-1$
    /** suitability=good overlay */
    public static final String UNKNOWN_OVERLAY = PATH_OVR + "unknown_ovr.gif"; //$NON-NLS-1$

}
