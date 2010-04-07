/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
 */
package net.refractions.udig.tool.select;

import java.awt.Point;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.tool.select.internal.Messages;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Selects and drags single features.
 * 
 * @author jones
 * @since 1.0.0
 */
public class ArrowSelection extends AbstractModalTool implements ModalTool {

    private int x;
    private int y;

    public ArrowSelection(){
        super(DRAG_DROP|MOUSE);
    }

    @Override
    public void mousePressed( MapMouseEvent e ) {
        x=e.x;
        y=e.y;
    }
    
    @Override
    public void mouseReleased( final MapMouseEvent e ) {
        if( e.x==x && e.y==y ){
            PlatformGIS.run(new IRunnableWithProgress(){

                @SuppressWarnings("unchecked")
                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(Messages.ArrowSelection_0, 5);
                    ReferencedEnvelope bbox = getContext().getBoundingBox(new Point(x,y),5);
                    FeatureCollection<SimpleFeatureType, SimpleFeature> collection=null;
                    Iterator<SimpleFeature> iter=null;
                    try {
                        ILayer selectedLayer = getContext().getSelectedLayer();
                        FeatureSource<SimpleFeatureType, SimpleFeature> source =selectedLayer.getResource(FeatureSource.class, new SubProgressMonitor(monitor, 1));
                        if( source==null )
                            return;
                        collection=source.getFeatures(selectedLayer.createBBoxFilter(bbox, new SubProgressMonitor(monitor, 1)));
                        iter=collection.iterator();
                        if( !iter.hasNext() ){
                            if( !e.buttonsDown() ){
                                getContext().sendASyncCommand(getContext().getEditFactory().createNullEditFeatureCommand());
                            }
                            getContext().sendASyncCommand(getContext().getSelectionFactory().createNoSelectCommand());                            
                            return;
                        }
                        SimpleFeature feature=iter.next();
                        getContext().sendASyncCommand(getContext().getEditFactory().createSetEditFeatureCommand(feature, selectedLayer));
                        getContext().sendASyncCommand(getContext().getSelectionFactory().createFIDSelectCommand(selectedLayer, feature));
                    } catch (IOException e) {
                        
                     // return;
                    }finally{
                        monitor.done();
                        if( collection !=null && iter!=null )
                            collection.close(iter);
                    }
                }
                
            });
        }
    }
}
