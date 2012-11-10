/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.style.sld.simple;

/**
 * Captures the current *mode* of the style configurator making use
 * of a StyleViewer.
 * <p>
 * This is used to let the viewers get modey, and disable fill content
 * when working with linestrings for example.
 * </p>
 * 
 * @author Jody Garnett
 * @since 1.0.0
 */
public enum Mode { 
    /** <code>POINT</code> Mode - editing a Point or MultiPoint. */
    POINT, 
    /** <code>LINE</code> Mode - editing a Linestring or MultiLineString. */
    LINE, 
    /** <code>POLYGON</code> Mode - editing a Polygone or MultiPolygon. */
    POLYGON, 
    /** <code>ALL</code> Mode - editing a Geometry. */
    ALL, 
    /** <code>NONE</code> Mode - content cannot be styled by SLD (like scalebar) */
    NONE
}