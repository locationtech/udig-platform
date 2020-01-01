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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.PageBook;
import org.geotools.styling.StyleBuilder;
import org.locationtech.udig.project.internal.Layer;

/**
 * Provides a user interface to edit a component of an Style Layer Descriptor (SLD) style object.
 * <p>
 * An SLD style component can be one of the following classes of object:
 * <ul>
 * <li>
 * 
 * @see org.geotools.renderer.style.Style
 *      <li>
 * @see org.geotools.styling.FeatureTypeStyle
 *      <li>
 * @see org.geotools.styling.Rule
 *      <li>
 * @see org.geotools.styling.Symbolizer
 *      </ul>
 *      </p>
 *      <p>
 *      This object does not store state. Any state information of ui widgets must be immediatley
 *      reflected in the style component.
 *      </p>
 * @author Justin Deoliveira, Refractions Research Inc.
 */
public abstract class SLDEditorPart {

    public static final String XPID = "org.locationtech.udig.style.sld.sldEditorPart"; //$NON-NLS-1$

    /** the ui control for the SLDEditor part * */
    private Composite page;

    /** the style component * */
    private Object content;

    /** the label for the SLDEditor part * */
    private String label;

    /** the path to the icon for the extension * */
    private ImageDescriptor image;

    /** the layer being styled * */
    private Layer layer;

    /** the id of the plugin providing the extension * */
    private String pluginId;

    /** style builder used to create styling * */
    private StyleBuilder styleBuilder;

    public SLDEditorPart() {

    }

    /**
     * Returns the ui control. This method should not be overridden.
     * 
     * @return The ui control.
     */
    public Composite getPage() {
        return page;
    }

    /**
     * Signals the ui control to be created. This method should not be overidden.
     * 
     * @param parent The parent control.
     */
    public void createControl( PageBook book ) {
        page = new Composite(book, SWT.NONE);
        page.setLayout(new FillLayout());
        page.setData(getLabel());
        createPartControl(page);
    }

    /**
     * @return Returns the content.
     */
    public Object getContent() {
        return content;
    }
    /**
     * @param content The content to set.
     */
    public void setContent( Object content ) {
        this.content = content;
    }

    /**
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }
    /**
     * @param label The label to set.
     */
    public void setLabel( String label ) {
        this.label = label;
    }
    /**
     * @return Returns the layer.
     */
    public Layer getLayer() {
        return layer;
    }
    /**
     * @param layer The layer to set.
     */
    public void setLayer( Layer layer ) {
        this.layer = layer;
    }
    /**
     * @param styleBuilder The styleBuilder to set.
     */
    public void setStyleBuilder( StyleBuilder styleBuilder ) {
        this.styleBuilder = styleBuilder;
    }
    /**
     * @return Returns the styleBuilder.
     */
    public StyleBuilder getStyleBuilder() {
        return styleBuilder;
    }
    /**
     * @param image The image descriptor.
     */
    public void setImageDescriptor( ImageDescriptor image ) {
        this.image = image;
    }
    /**
     * @return The image descriptor.
     */
    public ImageDescriptor getImageDescriptor() {
        return image;
    }
    /**
     * @param pluginId The pluginId to set.
     */
    public void setPluginId( String pluginId ) {
        this.pluginId = pluginId;
    }
    /**
     * @return Returns the pluginId.
     */
    public String getPluginId() {
        return pluginId;
    }

    /**
     * Returns an image descriptor for the editor part. Sublcasses have the option of overiding to
     * provide a custom image.
     */
    public ImageDescriptor createImageDescriptor() {
        return SLD.createImageDescriptor(getContentType());
    }

    /**
     * Initializes the editor. This method is called before the ui is created so this method should
     * not attempt to access any of its (yet to be created) widgets.
     */
    public abstract void init();

    /**
     * Style class, like TextSymbolizer, used for editing.
     * 
     * @return the class of style component the ui is used for editing.
     */
    public abstract Class getContentType();

    /**
     * The internal method for creating the ui component. The parent control passed to the method
     * must not be modified in any way.
     * 
     * @param parent The parent control.
     * @return The newly created control.
     */
    protected abstract Control createPartControl( Composite parent );

    /**
     * Resets the editor. This method resets the ui to reflect the new state of the content being
     * edited, and the layer being styled.. This method is not called unless content, and a layer
     * are available.
     */
    public abstract void reset();
}
