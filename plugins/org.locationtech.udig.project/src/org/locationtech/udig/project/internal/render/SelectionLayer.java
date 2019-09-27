/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.geotools.feature.NameImpl;
import org.geotools.styling.Style;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerDecorator;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.internal.impl.LayerImpl;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.filter.Filter;

/**
 * TODO Purpose of org.locationtech.udig.project.internal.render
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
     * @see org.locationtech.udig.project.internal.Layer#isVisible()
     */
    public boolean isVisible() {
        return layer.isVisible() && !Filter.EXCLUDE.equals(getFilter());
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#getStyleBlackboard()
     * @uml.property name="styleBlackboard"
     */
    public StyleBlackboard getStyleBlackboard() {
        if (styleBlackboard == null)
            styleBlackboard = ProjectFactory.eINSTANCE.createStyleBlackboard();

        styleBlackboard.clear();
        Style style = null;
        
        //if the original layer has a selection style on its blackboard, copy
        //it to this blackboard
        if (layer.getStyleBlackboard() != null) {
            style = (Style)layer.getStyleBlackboard().get(SelectionStyleContent.ID);
        }
        
        //no selection style defined on original layer, so create a default one
        if (style == null) {
            style = SelectionStyleContent.createDefaultStyle(layer);
            style.featureTypeStyles().get(0).featureTypeNames().add(new NameImpl(SLDs.GENERIC_FEATURE_TYPENAME));
        }
        
        styleBlackboard.put(SelectionStyleContent.ID, style);
        return styleBlackboard;
    }

    /**
     * @see org.locationtech.udig.project.internal.Layer#setStyleBlackboard(org.locationtech.udig.project.StyleBlackboard)
     * @uml.property name="styleBlackboard"
     */
    public void setStyleBlackboard( StyleBlackboard value ) {
        styleBlackboard = value;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER__STYLE_BLACKBOARD, value, value));
    }

    /**
     * @see org.locationtech.udig.project.internal.LayerDecorator#getZorder()
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
     * @see org.locationtech.udig.project.internal.Layer#setStatus(int)
     */
    public void setStatus( int status ) {
        // FIXME: Selection layer is always setting wait on us
        if (status == WAIT)
            return; // not
        layer.setStatus(status);
    }

    /**
     * @see org.locationtech.udig.project.internal.LayerDecorator#setStatusMessage(java.lang.String)
     */
    public void setStatusMessage( String message ) {
        this.message = message;
    }

    /**
     * @see org.locationtech.udig.project.internal.LayerDecorator#getStatusMessage()
     */
    public String getStatusMessage() {
        return message;
    }
}
