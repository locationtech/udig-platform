/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.IStyleBlackboard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

/**
 * Provides persistence, storage for Style information and shared collaboration between renderers
 * and the user.
 * 
 * @author Richard Gould
 * @since 0.6.0
 * @model
 */
public interface StyleBlackboard extends EObject, IStyleBlackboard, Cloneable {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * List of Style information.
     * <p>
     * Note: This list should not be accessed by client code - it is for use by the EMF model.
     * Please use the lookup methods provided.
     * </p>
     * 
     * @model containment="true" type="StyleEntry"
     */
    List<StyleEntry> getContent();

    /**
     * Retreives the style value by the styleId.
     * 
     * @param styleId A well known String constant identifying the style agreed upon by the Renderer
     *        and the StyleConfigurator.
     * @return The requested style object or null if it does not exist
     * @model
     */
    Object get( String styleId );

    /**
     * Retreives the style value by the class of the style object. Returns the first object that
     * meets the criteria.
     * 
     * @param theClass A superclass of the class of the style object.
     * @return The requested style object or null if it does not exist
     * @model
     */
    Object lookup( Class<?> theClass );

    /**
     * Convenience method for testing for the existance of a style.
     * 
     * @param styleId A well known String constant identifying the style agreed upon by the Renderer
     *        and the StyleConfigurator.
     * @return true if the style extists, otherwise false.
     * @model
     */
    boolean contains( String styleId );

    /**
     * Places a style onto the blackboard.
     * 
     * @param styleId A well known String constant identifying the style agreed upon by the Renderer
     *        and the StyleConfigurator. Since the id is assumed to be unique, if a style entry
     *        already exists with the specified id, it should be overwritten.
     * @param style An object containing all the styling information.
     * @model
     */
    void put( String styleId, Object style );

    /**
     * Loads a style from a URL pointing to a resource. This method blocks.
     * 
     * @param url the URL pointing to the style.
     * @param monitor A progress monitor. Allowed to be null.
     * @throws IOException if there is an error getting the style.
     * @model
     */
    void put( URL url, IProgressMonitor monitor ) throws IOException;

    /**
     * Sets the styles indicated by the ids to be <em>selected</em>.  
     * <p>Selected styles are those that were selected/created by the user rather than the framework</p>
     * <p>Example:
     *      <p>A layer has WMS and WFS {@link IGeoResource}s so either a FeatureRenderer or a WMSRenderer could render the layer.</p>
     *      <p>In the case where the wms allows the SLD to be retrieved the WMS StyleConfigurator could add both the named style to the blackboard
     *      as well as the SLD style to the blackboard.  It could set both as selected because both the WMSRenderer and the FeatureRenderer
     *      would render the same image.</p>
     *      <p>However if the user enters a SLD style (assuming the WMS does not accept SLD POST) only the SLD style would be set to selected</p>
     *      <p>By using selection the choice of renderer used is affected towards the selected style</p>
     * </p>
     *
     * @param ids
     */
    void setSelected(String[] ids);
    
    /**
     * Removes the style value identified by styleId from the blackboard. FIXME: Can we reduce this
     * to put( styleId, null ) ?
     * 
     * @param styleId A well known String constant identifying the style agreed upon by the Renderer
     *        and the StyleConfigurator.
     * @return The style object removed from the blackboard, or null if no such entry exists.
     * @model
     */
    Object remove( String styleId );

    /**
     * Creates a clone of the blackboard.
     * 
     * @return A clone of the blackboard.
     * @model
     */
    Object clone();

}
