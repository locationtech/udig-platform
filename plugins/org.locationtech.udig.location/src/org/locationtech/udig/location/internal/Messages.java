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
package org.locationtech.udig.location.internal;

import java.lang.reflect.Field;

import org.locationtech.udig.location.LocationUIPlugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.locationtech.udig.location.internal.messages"; //$NON-NLS-1$
	public static String action_show_image;
	public static String action_show_tooltip;
	public static String action_show_label;
	public static String LocationView_no_results;
	public static String LocationView_searching;
	public static String LocationView_keywords;
	public static String LocationView_notFound;
	public static String LocationView_name;
	public static String LocationView_title;
	public static String LocationView_wait;
	public static String LocationView_searching_for;
	public static String LocationView_instructions;
	public static String LocationView_description;
	public static String LocationView_bboxTooltip;
	public static String LocationView_bbox;
	public static String LocationView_server;
	public static String LocationView_prompt;
	public static String LocationView_default;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
	
	/**
     * Initialize the given Action from a ResourceBundle.
     * <p>
     * Makes use of the following keys:
     * <ul>
     * <li>prefix.label
     * <li>prefix.tooltip
     * <li>prefix.image
     * <li>prefix.description
     * </p>
     * <p>
     * Note: The use of a single image value is mapped to images for both the enabled and disabled
     * state of the IAction. the Local toolbar (elcl16/ and dlcl16/) is assumed if a path has not
     * been provided.
     * 
     * <pre><code>
     *  add_co.gif              (prefix.image)
     *     enabled: elcl16/add_co.gif
     *    disabled: dlcl/remove_co.gif
     *  tool16/discovery_wiz.16 (prefix.image)
     *     enabled: etool16/discovery_wiz.16
     *    disabled: etool16/discovery_wiz.16
     * </code></pre>
     * 
     * </p>
     * 
     * @param a action
     * @param id used for binding (id.label, id.tooltip, ...)
     */
    public static void initAction( IAction a, String id ) {
        String labelKey = "_label"; //$NON-NLS-1$
        String tooltipKey = "_tooltip"; //$NON-NLS-1$
        String imageKey = "_image"; //$NON-NLS-1$
        String descriptionKey = "_description"; //$NON-NLS-1$
        if (id != null && id.length() > 0) {
            labelKey = id + labelKey;
            tooltipKey = id + tooltipKey;
            imageKey = id + imageKey;
            descriptionKey = id + descriptionKey;
        }
        String s = bind(labelKey);
        if (s != null)
            a.setText(s);
        s = bind(tooltipKey);
        if (s != null)
            a.setToolTipText(s);
        s = bind(descriptionKey);
        if (s != null)
            a.setDescription(s);
        String relPath = bind(imageKey);
        if (relPath != null && !relPath.equals(imageKey) && relPath.trim().length() > 0) {
            String dPath;
            String ePath;
            if (relPath.indexOf("/") >= 0) { //$NON-NLS-1$
                String path = relPath.substring(1);
                dPath = 'd' + path;
                ePath = 'e' + path;
            } else {
                dPath = "dlcl16/" + relPath; //$NON-NLS-1$
                ePath = "elcl16/" + relPath; //$NON-NLS-1$
            }
            ImageDescriptor image;

            image = LocationUIPlugin.getDefault().getImageDescriptor(ePath);
            if (id != null) {
            	LocationUIPlugin.trace(id + ": '" + ePath + "' found " + id); //$NON-NLS-1$ //$NON-NLS-2$
                a.setImageDescriptor(image);
            }
            image = LocationUIPlugin.getDefault().getImageDescriptor(dPath);
            if (id != null) {
                LocationUIPlugin.trace(id + ": '" + dPath + "' found " + id); //$NON-NLS-1$ //$NON-NLS-2$
                a.setDisabledImageDescriptor(image);
            }
        }
    }

	private static String bind(String fieldName) {
		Field field;
		try {
			field = Messages.class.getDeclaredField(fieldName);
			return (String) field.get(null);
		} catch (Exception e) {
			LocationUIPlugin.log("Error loading key " + fieldName, e); //$NON-NLS-1$
		}
		return null;
	}
}
