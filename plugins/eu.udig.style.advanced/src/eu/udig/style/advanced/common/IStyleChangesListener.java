/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
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
package eu.udig.style.advanced.common;

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
