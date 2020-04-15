/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.wizard.export.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.locationtech.udig.project.IMap;

/**
 * An interface for the different ways that Images can be exported from {@link ExportMapToImageWizard} 
 * 
 * @author jesse
 * @since 1.1.0
 */
public abstract class ImageExportFormat {

    private Control control;

    /**
     * Returns the name of the format.
     *
     * @return the name of the format.
     */
    public abstract String getName();

    /**
     * Returns the extension of the format.  The "." should not be included.
     *
     * @return the extension of the format.
     */
    public abstract String getExtension();

    /**
     * Method adds creates a control for custom configuration. 
     * This method must call {@link #setControl(Control)}
     * 
     * @param parent The parent widget.  But only temporarily since the widgets may be reparented.
     */
    public abstract void createControl( Composite parent );
    
    /**
     * Must return the control that is created by {@link #createControl(Composite)}
     *
     * @return
     */
    public Control getControl(){
        return control;
        
    }
    
    protected void setControl( Control control ){
        this.control = control;
    }

    /**
     * Returns true if the format will use the standard height/width widgets for choosing the dimensions of the 
     * output.  If false is returned the standard controls will be disabled and {@link #getHeight(double, double)} and
     * {@link #getWidth(double, double)} will both be called and should be implemented.
     *
     * @return  true if the format will use the standard height/width widgets for choosing the dimensions of the 
     * output
     */
    public boolean useStandardDimensionControls() {
        return true;
    }

    /**
     * Writes the image that was created from the map to the destination file.
     *
     * @param map the map that was used to render the image.  It has viewportmodel and rendermanager set on it.
     * @param image the image that was created
     * @param destination the file to write to.
     * @throws IOException 
     */
    public abstract void write( IMap map, BufferedImage image, File destination ) throws IOException;

    /**
     * Returns the height of the image to be created.  
     * Called if {@link #useStandardDimensionControls()} returns false.  
     *
     * @param mapwidth the width of the map being rendered
     * @param mapheight the height of the map being rendered
     * 
     * @return the width of the image that will be created
     */
    public int getHeight( double mapwidth, double mapheight ) {
        return 0;
    }

    /**
     * Returns the width of the image to be created.  
     * Called if {@link #useStandardDimensionControls()} returns false.  
     *
     * @param mapwidth the width of the map being rendered
     * @param mapheight the height of the map being rendered
     * 
     * @return the width of the image that will be created
     */
    public int getWidth( double mapwidth, double mapheight ) {
        return 0;
    }

    /**
     * Returns the DPI at which to render.  The default is the Graphics2D 72
     *
     * @return
     */
    public int getDPI() {
        return 72;
    }

}
