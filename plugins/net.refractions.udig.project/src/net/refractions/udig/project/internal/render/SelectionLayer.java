/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.render;

import java.io.IOException;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerDecorator;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.internal.impl.LayerImpl;
import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.geotools.data.FeatureSource;
import org.geotools.filter.Filter;
import org.geotools.styling.Style;

/**
 * TODO Purpose of net.refractions.udig.project.internal.render
 * <p>
 * </p>
 *
 * @author Jesse
 * @since 1.0.0
 */
public class SelectionLayer extends LayerDecorator {

    private StyleBlackboard styleBlackboard = ProjectFactory.eINSTANCE.createStyleBlackboard();

    private String message;

    /**
     * Construct <code>SelectionLayer</code>.
     *
     * @param layer
     */
    public SelectionLayer( Layer layer ) {
        super(layer);
    }

    /**
     * @see net.refractions.udig.project.internal.Layer#isVisible()
     */
    public boolean isVisible() {
        return layer.isVisible() && !Filter.ALL.equals(getFilter());
    }

    /**
     * @see net.refractions.udig.project.internal.Layer#getStyleBlackboard()
     * @uml.property name="styleBlackboard"
     */
    public StyleBlackboard getStyleBlackboard() {
        if (styleBlackboard == null)
            styleBlackboard = ProjectFactory.eINSTANCE.createStyleBlackboard();

        styleBlackboard.clear();
        Style style = SelectionStyleContent.createDefaultStyle(layer);
        if (style == null)
            return styleBlackboard;
        style.getFeatureTypeStyles()[0].setFeatureTypeName(SLDs.GENERIC_FEATURE_TYPENAME);
        styleBlackboard.put(SelectionStyleContent.ID, style);
        return styleBlackboard;
    }

    /**
     * @see net.refractions.udig.project.internal.Layer#setStyleBlackboard(net.refractions.udig.project.StyleBlackboard)
     * @uml.property name="styleBlackboard"
     */
    public void setStyleBlackboard( StyleBlackboard value ) {
        styleBlackboard = value;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__STYLE_BLACKBOARD, value, value));
    }

    /**
     * @see net.refractions.udig.project.internal.LayerDecorator#getZorder()
     */
    @Override
    public int getZorder() {
        Map mapInternal = getMapInternal();
        if( mapInternal==null )
            return Integer.MAX_VALUE;
        return super.getZorder() + mapInternal.getLayersInternal().size();
    }

    /**
     * Required because delegating to layer won't get this objects z-order
     */
    public int compareTo( ILayer layer2 ) {
        return LayerImpl.doComparison(this, layer2);
    }


    /**
     * @see net.refractions.udig.project.internal.Layer#setStatus(int)
     */
    public void setStatus( int status ) {
        // FIXME: Selection layer is always setting wait on us
        if (status == WAIT)
            return; // not
        layer.setStatus(status);
    }

    /**
     * @see net.refractions.udig.project.internal.LayerDecorator#setStatusMessage(java.lang.String)
     */
    public void setStatusMessage( String message ) {
        this.message = message;
    }

    /**
     * @see net.refractions.udig.project.internal.LayerDecorator#getStatusMessage()
     */
    public String getStatusMessage() {
        return message;
    }
}
