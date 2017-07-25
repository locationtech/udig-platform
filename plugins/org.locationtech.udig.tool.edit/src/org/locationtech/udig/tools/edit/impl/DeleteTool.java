/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.impl;

import java.awt.Point;
import java.text.MessageFormat;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.AbstractModalTool;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.preferences.PreferenceConstants;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Deletes a feature from the currently selected layer or the top layer.
 * 
 * @author Jesse
 * @since 0.8.1
 */
public class DeleteTool extends AbstractModalTool implements ModalTool {

    // The size of the box that is searched during deletion operations.  
    protected int DELETE_SEARCH_SIZE;

    // boolean flag indicating whether a confirm message should be issued during deletion.  
    protected boolean DELETE_CONFIRM;
    
    /**
     * Construct <code>DeleteTool</code>.
     *
     */
    public DeleteTool() {
        super(MOUSE|MOTION);
    }


    @Override
    public void setActive(final boolean active) {
        super.setActive(active);
        
        DELETE_SEARCH_SIZE = Platform.getPreferencesService().getInt(
                EditPlugin.ID, PreferenceConstants.P_DELETE_TOOL_RADIUS, 6, null);
        DELETE_CONFIRM = Platform.getPreferencesService().getBoolean(
                EditPlugin.ID, PreferenceConstants.P_DELETE_TOOL_CONFIRM, true, null);
        
        setStatusBarMessage(active);
    }

    private void setStatusBarMessage(final boolean active) {
        getContext().updateUI(new Runnable() {
            public void run() {
                if( getContext().getActionBars()==null )
                    return;
                IStatusLineManager bar = getContext().getActionBars().getStatusLineManager();
                if( bar!=null ){
                    if( active ){
                        if( getContext().getMapLayers().size()>0 )
                            bar.setMessage(Messages.DeleteTool_status);
                    }else
                        bar.setMessage(""); //$NON-NLS-1$
                    bar.setErrorMessage(null);
                }
            }
        });
    }


    @Override
    public void mousePressed( MapMouseEvent e ) {
        draw.setValid( true ); // make sure context.getViewportPane().repaint() knows about us        
        context.sendASyncCommand( draw ); // should of isValided us       
        feedback( e );

    }
    @Override
    public void mouseDragged( MapMouseEvent e ) {
        feedback( e );

    }

    DrawShapeCommand draw = new DrawShapeCommand();

    /**
     * Provides user feedback
     * @param e 
     */
    public void feedback( MapMouseEvent e ) {        
        ReferencedEnvelope box = context.getBoundingBox( new Point(e.x-3, e.y-3), 7 );
        draw.setShape( context.toShape( box ) );        
        context.getViewportPane().repaint();

        super.mouseDragged(e);
    }


    public void mouseReleased( MapMouseEvent e ) {
        if( getContext().getMapLayers().size()==0 )
            return;
        FeatureIterator<SimpleFeature> reader = null;
        try {

            ILayer layer = getContext().getEditManager().getSelectedLayer();
            if (layer == null)
                layer = getContext().getMapLayers().get(0);

            if (layer == null)
                throw new Exception("No layers in map"); //$NON-NLS-1$

            Envelope env = getContext().getBoundingBox(e.getPoint(), DELETE_SEARCH_SIZE);
            FeatureCollection<SimpleFeatureType, SimpleFeature>  results = getContext().getFeaturesInBbox(
                    layer,
                    env);

            reader = results.features();

            final boolean found=!reader.hasNext() ;
            getContext().updateUI(new Runnable() {
                public void run() {
                    if( getContext().getActionBars()==null )
                        return;
                    IStatusLineManager bar = getContext().getActionBars().getStatusLineManager();
                    if (bar!=null){
                        if( found )
                            bar.setErrorMessage( Messages.DeleteTool_warning);
                        else
                            bar.setErrorMessage(null);
                    }
                }
            });

            if (found)
                return;

            final SimpleFeature[] features = results.toArray(new SimpleFeature[]{});
            if (features.length == 1) {
                SimpleFeature feature=features[0];
                if (DELETE_CONFIRM && !MessageDialog.openConfirm(null, "", MessageFormat.format(
                        Messages.DeleteTool_confirmation_text2, feature.getIdentifier()))) {
                    return;
                }
                MapCommand deleteFeatureCommand = getContext().getEditFactory().createDeleteFeature(feature, layer);
                getContext().sendASyncCommand(deleteFeatureCommand);
            } else {
                final Menu menu = new Menu(((ViewportPane) e.source).getControl().getShell(), SWT.POP_UP);
                final ILayer selectedLayer = layer;
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                    	final String attribName = FeatureUtils.getActualPropertyName(
                    			features[0].getFeatureType(), ATTRIBUTE_NAME);
                        for (final SimpleFeature feat : features) {
                            MenuItem item = new MenuItem(menu, SWT.PUSH);
                            //SimpleFeature feature=iter.next();
                            item.setText(attribName != null ? 
                                    feat.getAttribute(attribName).toString() : feat.getID());
                            item.addSelectionListener(new SelectionAdapter() {

                                @Override
                                public void widgetSelected(SelectionEvent e) {
                                    if (DELETE_CONFIRM && !MessageDialog.openConfirm(null, "", MessageFormat.format(
                                            Messages.DeleteTool_confirmation_text2, feat.getIdentifier()))) {
                                        return;
                                    }
                                    MapCommand deleteFeatureCommand = getContext().getEditFactory().createDeleteFeature(feat, selectedLayer);
                                    getContext().sendASyncCommand(deleteFeatureCommand);

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

            getContext().getViewportPane().repaint();
        } catch (Exception e1) {
            EditPlugin.log( null, e1);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (Exception e2) {
                EditPlugin.log( null, e2);
            }
            draw.setValid( false ); // get us off the draw stack for context.getViewportPane().repaint();
            getContext().getViewportPane().repaint();
        }
    }

}
