/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.style.filter;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.filter.FilterViewer;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Style page responsible for allowing user to configure filter information
 * used to preprocess data prior to display.
 */
public class FilterConfigurator extends IStyleConfigurator {
    public static String STYLE_ID = ProjectBlackboardConstants.LAYER__STYLE_FILTER;
    
    FilterStyle filterStyle = null;
    
    /** FilterViewer used to allow easy input of CQL expressions keeping the current feature type in mind */
    protected FilterViewer text;
    
    /** Toggle to indicate interest in the current area of interest */
    protected Button aoiButton;
    
    /**
     * AOI we are watching you
     */
    private SelectionListener aoiListener = new SelectionListener(){
        @Override
        public void widgetSelected( SelectionEvent e ) {
            if( aoiButton == null || aoiButton.isDisposed() ){
                return; // ignore me!
            }
            externalUpdate();
        }
        @Override
        public void widgetDefaultSelected( SelectionEvent e ) {
        }
    };

    /** Will write filter to blackboard on focus lost */
    private ISelectionChangedListener listener = new ISelectionChangedListener(){
        public void selectionChanged( SelectionChangedEvent event ) {
            if( text == null || text.getControl().isDisposed() ){
                return; // ignore me!
            }
            externalUpdate();
        }
    };

    public FilterConfigurator() {
    }

    /**
     * Update the internal filter; will set tooltip text as required in the event the filter does
     * not parse.
     */
    public boolean checkValid(){
        if( text == null || text.getControl() == null ||  text.getControl().isDisposed() ){
            return false; // nothing to see
        }
        if( text.validate() ){
            return true;
        }
        else {
            return false;
        }
    }
    public void externalUpdate() {
        if( text == null || text.getControl() == null ||  text.getControl().isDisposed() ){
            return; // nothing to see
        }
        Filter filter = filterStyle.getFilter();
        boolean changed = false;
        if( !text.getInput().equals(filter)){
            filterStyle.setFilter( text.getInput() );
            changed = true;
        }
        boolean aoi = filterStyle.isAoiFilter();
        if( aoiButton.getSelection() != aoi ){
            filterStyle.setAoiFilter( aoiButton.getSelection());
            changed = true;
        }
        
        if( changed ){
            getStyleBlackboard().put(STYLE_ID, filterStyle ); // this will cause FilterContent to rewrite our memento
        }
    }

    @Override
    public boolean canStyle( Layer aLayer ) {
        if (aLayer.hasResource(FeatureSource.class)) {
            return true;
        }
        return false;
    }

    protected FilterStyle getFilterStyle() {
        Layer layer = getLayer();
        assert (canStyle(layer));
        
        FilterStyle current = (FilterStyle) getStyleBlackboard().get(STYLE_ID);
        if (current == null) {
            return new FilterStyle(); // not available
        }
        return current;
    }

    @Override
    public void createControl( Composite parent ) {
        parent.setLayout(new MigLayout("", "[right]rel[left, grow]", "[c,grow 75,fill]"));

        Label label = new Label(parent, SWT.SINGLE );
        label.setText("Filter");

        text = new FilterViewer(parent,  SWT.MULTI | SWT.V_SCROLL | SWT.BORDER );
        text.getControl().setLayoutData("growx, growy, span, wrap");
        
        // Area of Interest filter button
        aoiButton = new Button(parent, SWT.CHECK);
        aoiButton.setText("Area of Interest");
        aoiButton.addSelectionListener(aoiListener);
        
        listenText(true);
        listenAOI(true);
    }

    private void listenText( boolean listen ) {
        if (listen) {
            text.addSelectionChangedListener(listener);
        } else {
            text.removeSelectionChangedListener(listener);
        }
    }
    
    private void listenAOI( boolean listen ) {
        if (aoiButton != null && !aoiButton.isDisposed()) {
            if (listen) {
                aoiButton.addSelectionListener(aoiListener);
            } else {
                aoiButton.removeSelectionListener(aoiListener);
            }
        }
    }
    
    @Override
    protected void refresh() {
        if( this.aoiButton == null || this.aoiButton.isDisposed()){
            return; // we are shut down and thus ignoring this request to update the ui
        }
        SimpleFeatureType type = getLayer().getSchema();
        text.setSchema( type );
        
        final FilterStyle style = getFilterStyle();
        text.getControl().getDisplay().asyncExec(new Runnable(){
            public void run() {
                if( aoiButton == null || aoiButton.isDisposed()){
                    return; // we are shut down and thus ignoring this request to update the ui
                }
                try {
                    listenText(false);
                    
                    Filter filter = style.getFilter();
                    if( filter == null ){
                        // we are going to default to INCLUDE
                        text.setInput( Filter.INCLUDE );
                    }
                    else {
                        text.setInput( filter );
                    }
                } finally {
                    listenText(true);
                }
                try {
                    listenAOI(false);
                    aoiButton.setSelection(style.isAoiFilter());
                } finally {
                    listenAOI(true);
                }
                FilterConfigurator.this.filterStyle = style; // remember this so we can check if changed
            }
        });
    }

    @Override
    public void dispose() {
        if (text != null) {
            listenText(false);
            text = null;
        }
        super.dispose();
    }
}
