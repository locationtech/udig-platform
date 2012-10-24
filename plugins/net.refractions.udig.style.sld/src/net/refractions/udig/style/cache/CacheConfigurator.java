/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.style.cache;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.wms.WebMapServer;
import org.geotools.styling.Style;
import org.opengis.coverage.grid.GridCoverage;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;
import net.refractions.udig.style.sld.SLDContent;

public class CacheConfigurator extends IStyleConfigurator {

    protected Button cacheCheckbox;
    protected boolean wasSelected;
    
    private SelectionListener listener = new SelectionAdapter(){
        public void widgetSelected( SelectionEvent e ) {
            boolean isSelected = cacheCheckbox.getSelection();
            valueChanged( wasSelected, isSelected );
        }
    };

    public CacheConfigurator() {
    }

    @Override
    public boolean canStyle( Layer aLayer ) {
        if (aLayer.hasResource(GridCoverage.class) || aLayer.hasResource(FeatureSource.class)
                || aLayer.hasResource(AbstractGridCoverage2DReader.class)){
            return true;
        }
        return false;
    }

    protected boolean getStyle(){
        Layer layer = getLayer();
        assert( canStyle( layer ));
        
        Boolean isCached = (Boolean) getStyleBlackboard().get( CacheContent.ID );
        
        if( isCached == null ){
            isCached = false;
            getStyleBlackboard().put(CacheContent.ID, isCached );
        }
        return isCached;
    }
    
    @Override
    public void createControl( Composite parent ) {
        parent.setLayout(new MigLayout("", "[right]rel[left, grow]", "30"));
        
        Label label = new Label( parent, SWT.NONE );
        label.setText("Cache");
        
        cacheCheckbox = new Button( parent, SWT.CHECK);
        cacheCheckbox.setLayoutData("span,wrap");
        
        cacheCheckbox.addSelectionListener(listener);
    }

    protected void valueChanged( boolean oldValue, boolean newValue ) {
        if( oldValue != newValue ){
            getStyleBlackboard().put(CacheContent.ID, newValue );
        }        
    }

    @Override
    protected void refresh() {
        if( cacheCheckbox == null || cacheCheckbox.isDisposed() ){
            return;
        }
        final boolean style = getStyle();
        cacheCheckbox.getDisplay().asyncExec(new Runnable(){
            public void run() {
                if( cacheCheckbox == null || cacheCheckbox.isDisposed() ){
                    return; // nothing to do widget is gone
                }
                if( style != cacheCheckbox.getSelection() ){
                    cacheCheckbox.removeSelectionListener(listener);
                    cacheCheckbox.setSelection(style);
                    wasSelected = style;
                    cacheCheckbox.addSelectionListener(listener);
                }
            }
        });
    }
    
    @Override
    public void dispose() {
        if( cacheCheckbox != null ){
            cacheCheckbox.removeSelectionListener(listener);
            cacheCheckbox.dispose();
            cacheCheckbox = null;
        }
        super.dispose();
    }

}
