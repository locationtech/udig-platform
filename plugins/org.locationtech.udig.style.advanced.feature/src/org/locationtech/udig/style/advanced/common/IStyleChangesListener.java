/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.common;

/**
 * Style changes listener interface for widgets.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public interface IStyleChangesListener {
    
    /**
     * The differnt types of events that might occurr.
     */
    public enum STYLEEVENTTYPE {
        // general
        NAME,
        PATH,
        MARKNAME,
        SIZE, 
        ROTATION,
        OFFSET,
        MINSCALE,
        MAXSCALE,
        // border
        BORDERENABLE, 
        BORDERWIDTH, 
        BORDERCOLOR, 
        BORDEROPACITY,
        // fill
        FILLENABLE, 
        FILLCOLOR, 
        FILLOPACITY, 
        // graphics path
        GRAPHICSPATHBORDER, 
        GRAPHICSPATHFILL, 
        // wellknown marks for borders and fill, which also need to have width and color
        WKMGRAPHICSBORDER, 
        WKMGRAPHICSFILL, 
        // dashes and line properties
        DASH,
        DASHOFFSET,
        LINECAP,
        LINEJOIN,
        LINEEND,
        LINESTART,
        // text
        LABELENABLE,
        LABEL,
        LABELFONT,
        LABELCOLOR,
        LABELOPACITY,
        LABELHALOCOLOR,
        LABELHALORADIUS,
        LABELANCHOR,
        LABELDISPLACEMENT,
        LABELROTATION,
        LABELINITIALGAP,
        LABELPERPENDICULAROFFSET,
        LABELMAXDISPLACEMENT_VO,
        LABELREPEAT_VO,
        LABELAUTOWRAP_VO,
        LABELSPACEAROUND_VO,
        LABELFOLLOWLINE_VO,
        LABELMAXANGLEDELTA_VO,
        //Filters
        FILTER,
    }
    
    /**
     * Method triggered on changes of Style.
     * 
     * @param source the source widget.
     * @param values the string representation of the changed values.
     * @param fromField defines if the value is an attribute field name as opposed of the real property value.
     * @param styleEventType the type of the style parameter that was changed.
     */
    public void onStyleChanged(Object source, String[] values, boolean fromField, STYLEEVENTTYPE styleEventType);

}
