/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package net.refractions.udig.project.internal;

import net.refractions.udig.project.ILayerLegendItem;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Layer Legend Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.refractions.udig.project.internal.LayerLegendItem#getLayer <em>Layer</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.refractions.udig.project.internal.ProjectPackage#getLayerLegendItem()
 * @model superTypes="net.refractions.udig.project.internal.LegendItem net.refractions.udig.project.internal.ILayerLegendItem"
 * @generated
 */
public interface LayerLegendItem extends LegendItem, ILayerLegendItem {
    /**
     * Returns the value of the '<em><b>Layer</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Layer</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Layer</em>' reference.
     * @see #setLayer(Layer)
     * @see net.refractions.udig.project.internal.ProjectPackage#getLayerLegendItem_Layer()
     * @model required="true"
     * @generated
     */
    Layer getLayer();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.LayerLegendItem#getLayer <em>Layer</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Layer</em>' reference.
     * @see #getLayer()
     * @generated
     */
    void setLayer( Layer value );

} // LayerLegendItem
