/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.graticule;

import java.util.List;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicPlugin;
import org.locationtech.udig.mapgraphic.internal.Messages;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.internal.commands.SetLayerCRSCommand;
import org.locationtech.udig.style.IStyleConfigurator;
import org.locationtech.udig.ui.CRSChooserDialog;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * CRS configurator for {@link GraticuleStyle}
 * 
 * @author kengu
 * @since 1.3.3
 */
public class GraticuleCRSConfigurator extends IStyleConfigurator {
    
    private static final String EPSG = "EPSG:"; //$NON-NLS-1$
    private Text crsText;
    private Label crsLabel;

    @Override
    public boolean canStyle( Layer layer ) {
        return layer.hasResource(MapGraphic.class)
                && layer.getStyleBlackboard().get(GraticuleStyle.ID) != null;
    }

    @Override
    public void createControl( Composite parent ) {

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false);
        composite.setLayout(gridLayout);
        
        crsLabel = new Label(composite, SWT.NONE);
        crsLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        crsLabel.setText(Messages.GraticuleCRSConfigurator_CRS);

        crsText = new Text(composite, SWT.BORDER);
        crsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        crsText.setEditable(false);

        final Button crsButton = new Button(composite, SWT.BORDER);
        crsButton.setText(Messages.GraticuleCRSConfigurator_Select); 
        crsButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                CoordinateReferenceSystem crs = getCRS(getStyle());
                CRSChooserDialog dialog = new CRSChooserDialog(crsButton.getShell(), crs);
                int code = dialog.open();
                if (Window.OK == code) {
                    try {
                        crs = dialog.getResult();
                        crsText.setText(EPSG+CRS.lookupEpsgCode(crs,false));
                    } catch (FactoryException ex) {
                        MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
                        getLayer().setStatus(Layer.ERROR);
                        getLayer().setStatusMessage(ex.getMessage());
                    }
                }
            }
        });        
    }
    
    private GraticuleStyle getStyle() {
        return GraticuleStyle.getStyle(getLayer());
    }
    
    @Override
    protected void refresh() {
        try {
            CoordinateReferenceSystem crs = getCRS(getStyle());
            crsText.setText(EPSG+CRS.lookupEpsgCode(crs,false));
        } catch (FactoryException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
            getLayer().setStatus(Layer.ERROR);
            getLayer().setStatusMessage(ex.getMessage());
        } 
    }
    
    @Override
    public void preApply() {
        GraticuleStyle style = getStyle();
        style.setCRS(crsText.getText()); 
        getStyleBlackboard().put(GraticuleStyle.ID, style);        
        apply(getLayer(),getStyle().getCRS());
    }
    
    public static void apply(Layer layer, String code ) {
        apply(layer, getCRS(layer,code));
    }    

    public static void apply(Layer layer, CoordinateReferenceSystem crs ) {
        if(!layer.getCRS().equals(crs)) {
            UndoableComposite commands=new UndoableComposite();
            List<MapCommand> commandList = commands.getCommands();
            commandList.add(new SetLayerCRSCommand(layer, crs));
            layer.getMap().sendCommandASync(commands);
        }
    }
    
    public CoordinateReferenceSystem getCRS(GraticuleStyle style) {
        return getCRS(getLayer(),style.getCRS());
    }    

    public static CoordinateReferenceSystem getCRS(ILayer graticule, String code) {
        try {
            return CRS.decode(code);
        } catch (NoSuchAuthorityCodeException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
        } catch (FactoryException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
        }
        return graticule.getCRS();        
    }
    
    
}
