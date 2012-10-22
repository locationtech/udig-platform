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
package net.refractions.udig.mapgraphic.graticule;

import java.util.List;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicPlugin;
import net.refractions.udig.mapgraphic.internal.Messages;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.internal.commands.SetLayerCRSCommand;
import net.refractions.udig.style.IStyleConfigurator;
import net.refractions.udig.ui.CRSChooserDialog;

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
        crsButton.setText("..."); //$NON-NLS-1$
        crsButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                CoordinateReferenceSystem crs = getCRS(getStyle());
                CRSChooserDialog dialog = new CRSChooserDialog(crsButton.getShell(), crs);
                int code = dialog.open();
                if (Window.OK == code) {
                    String property;
                    try {
                        property = "EPSG:"+CRS.lookupEpsgCode(dialog.getResult(),false);  //$NON-NLS-1$
                        crsText.setText(property); 
                        GraticuleStyle style = getStyle();
                        style.setCRS(property); 
                        getStyleBlackboard().put(GraticuleStyle.ID, style);
                        GraticuleCRSConfigurator.this.makeActionDoStuff();
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
        crsText.setText(getCRS(getStyle()).getName().getCode()); 
    }
    
    @Override
    public void preApply() {
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