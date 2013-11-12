/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 * 
 */
package org.locationtech.udig.project.internal;

import org.locationtech.udig.project.ILayerLegendItem;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Layer Legend Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.locationtech.udig.project.internal.LayerLegendItem#getLayer <em>Layer</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.locationtech.udig.project.internal.ProjectPackage#getLayerLegendItem()
 * @model superTypes="org.locationtech.udig.project.internal.LegendItem org.locationtech.udig.project.internal.ILayerLegendItem"
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
     * @see org.locationtech.udig.project.internal.ProjectPackage#getLayerLegendItem_Layer()
     * @model required="true"
     * @generated
     */
    Layer getLayer();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.LayerLegendItem#getLayer <em>Layer</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Layer</em>' reference.
     * @see #getLayer()
     * @generated
     */
    void setLayer(Layer value);

} // LayerLegendItem
