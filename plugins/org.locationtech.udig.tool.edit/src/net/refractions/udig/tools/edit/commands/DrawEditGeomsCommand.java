/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.commands;

import java.awt.Rectangle;
import java.util.List;

import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.preferences.PreferenceConstants;
import net.refractions.udig.tools.edit.support.CurrentEditGeomPathIterator;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;

/**
 * Draws all the {@link net.refractions.udig.tools.edit.support.EditGeom}s on the provided 
 * {@link net.refractions.udig.tools.edit.support.EditBlackboard}
 * 
 * @author jones
 * @since 1.1.0
 */
public class DrawEditGeomsCommand extends AbstractDrawCommand {

    private StyleStrategy colorStrategy = new StyleStrategy();
    private Point location;
    private PrimitiveShape currentShape;
    private EditToolHandler handler;
    IPreferenceStore store = EditPlugin.getDefault().getPreferenceStore();

    public DrawEditGeomsCommand( EditToolHandler handler){
        this.handler=handler;
    }
    
    public void run( IProgressMonitor monitor ) throws Exception {
        List<EditGeom> geoms = handler.getEditBlackboard(handler.getEditLayer()).getGeoms();
        for( EditGeom geom : geoms ) {
            if( geom.getShell().getNumPoints()==0 ){
                continue;
            }
            
            if( geom.getShell().getNumPoints()>0){
                CurrentEditGeomPathIterator pathIterator = CurrentEditGeomPathIterator.getPathIterator(geom);
                if( currentShape!=null && currentShape.getEditGeom()==geom )
                    pathIterator.setLocation(location, currentShape);
                else{
                    pathIterator.setLocation(null, null);
                }

                fillPath(geom,pathIterator);
            }
        }
    }

    private void fillPath( EditGeom geom, CurrentEditGeomPathIterator pathIterator ) {
        Path shape = pathIterator.toPath(Display.getCurrent());
        try{
            if( store.getBoolean(PreferenceConstants.P_FILL_POLYGONS) && geom.getShapeType()==ShapeType.POLYGON ){
                colorStrategy.setFillColor(graphics, geom, handler);
                graphics.fillPath(shape);
            }

            colorStrategy.setLineColor2(graphics, geom, handler);
            graphics.drawPath(shape);

            colorStrategy.setLineColor(graphics, geom, handler);
            graphics.drawPath(shape);
            
        }finally{
            shape.dispose();
        }
    }

    @Override
    public void setValid( boolean valid ) {
        super.setValid(valid);
    }
    
    /**
     * @return Returns the currentShape.
     */
    public PrimitiveShape getCurrentShape() {
        return currentShape;
    }

    /**
     * @param l the current location of the mouse
     * @param currentShape The sub-shape of the geometry that is being editted.
     * @return true if the new setting requires a repaint();
     */
    public boolean setCurrentLocation( Point l, PrimitiveShape currentShape ) {
        boolean result=false;
        if( currentShape!=this.currentShape || (l!=null &&!l.equals(location)) )
            result=true;
        location=l;
        this.currentShape = currentShape;
        return result;
    }

    public Rectangle getValidArea() {
        return null;
    }
    
    /**
     * Returns the strategy object responsible for choosing the line and fill colors.
     */
    public StyleStrategy getColorizationStrategy() {
        return colorStrategy;
    }
}
