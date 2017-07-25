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
package org.locationtech.udig.tool.select;

import java.awt.Point;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.AbstractModalTool;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.tool.select.internal.Messages;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Selects and drags single features providing an appropriate selection popup 
 * in case there are multiple features located at the click point.
 * 
 * @author nprigour
 * @since 2.0.0
 */
public class ArrowSelectionWithPopup extends AbstractModalTool implements ModalTool {

    private int x;
    private int y;

    public ArrowSelectionWithPopup(){
        super(DRAG_DROP|MOUSE);
    }


    @Override
    public void mousePressed( MapMouseEvent e ) {
        if (e.button == MapMouseEvent.BUTTON3)
            ((ViewportPane) e.source).getMapEditor().openContextMenu();
        else {
            x=e.x; 
            y=e.y;
        }
    }

    @Override
    public void mouseReleased( final MapMouseEvent e ) {

        if( e.x==x && e.y==y ){
            //creates a pop-up menu to hold the values if multiple items are found in a position
            //final Menu menu = new Menu(ApplicationGISInternal.getActiveEditor().getComposite().getShell(), SWT.POP_UP);
            final Menu menu = new Menu(((ViewportPane) e.source).getControl().getShell(), SWT.POP_UP);

            PlatformGIS.run(new IRunnableWithProgress(){

                @SuppressWarnings("unchecked")
                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(Messages.ArrowSelection_0, 5);
                    ReferencedEnvelope bbox = getContext().getBoundingBox(
                            new Point(x,y),
                            SELECTION_SEARCH_SIZE);

                    FeatureCollection<SimpleFeatureType, SimpleFeature> collection=null;
                    FeatureIterator<SimpleFeature> iter=null;
                    try {
                        final ILayer selectedLayer = getContext().getSelectedLayer();
                        FeatureSource<SimpleFeatureType, SimpleFeature> source =selectedLayer.getResource(FeatureSource.class, new SubProgressMonitor(monitor, 1));
                        if( source==null )
                            return;
                        collection=source.getFeatures(selectedLayer.createBBoxFilter(bbox, new SubProgressMonitor(monitor, 1)));
                        iter=collection.features();
                        if( !iter.hasNext() ){
                            if( !e.buttonsDown() ){
                                getContext().sendASyncCommand(getContext().getEditFactory().createNullEditFeatureCommand());
                            }
                            getContext().sendASyncCommand(getContext().getSelectionFactory().createNoSelectCommand());                            
                            return;
                        } else {
                            //if only one item then do normal UDIG action
                            if (collection.size() == 1) {
                                SimpleFeature feature=iter.next();
                                getContext().sendASyncCommand(getContext().getEditFactory().createSetEditFeatureCommand(feature, selectedLayer));
                                getContext().sendASyncCommand(getContext().getSelectionFactory().createFIDSelectCommand(selectedLayer, feature));
                            } else {
                                final SimpleFeature[] features = collection.toArray(new SimpleFeature[]{});
                                //MapPart mapPart = MapEditor();
                                Display.getDefault().syncExec(new Runnable() {

                                    @Override
                                    public void run() {
                                    	final String attribName = FeatureUtils.getActualPropertyName(
                                    			features[0].getFeatureType(), ATTRIBUTE_NAME);
                                       
                                        for (final SimpleFeature feat : features) {
                                            MenuItem item = new MenuItem(menu, SWT.PUSH);
                                            item.setText(attribName != null ? 
                                                    feat.getAttribute(attribName).toString() : feat.getID());
                                            
                                            item.addSelectionListener(new SelectionListener() {

                                                @Override
                                                public void widgetSelected(SelectionEvent e) {
                                                    getContext().sendASyncCommand(getContext().getEditFactory().createSetEditFeatureCommand(feat, selectedLayer));
                                                    getContext().sendASyncCommand(getContext().getSelectionFactory().createFIDSelectCommand(selectedLayer, feat));
                                                }

                                                @Override
                                                public void widgetDefaultSelected(SelectionEvent e) {
                                                    // TODO Auto-generated method stub

                                                }
                                            });
                                        }
                                        //ApplicationGISInternal.getActiveEditor().getComposite().setMenu(menu);
                                        //((ViewportPane) e.source).getControl().setMenu(menu);
                                        //just make the menu visible (do not add it to a control since it will affect already existing menus)
                                        menu.setVisible(true);
                                    }

                                });

                            }
                        }
                    } catch (IOException e) {
                        // do nothing
                    }finally{
                        monitor.done();
                        if( collection !=null && iter!=null )
                            iter.close();

                    }
                }

            });
        }
    }
}
