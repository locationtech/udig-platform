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
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;
import net.refractions.udig.ui.filter.FilterInput;
import net.refractions.udig.ui.filter.FilterViewer;
import net.refractions.udig.ui.filter.IFilterViewer;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Style page responsible for allowing user to configure filter information
 * used to preprocess data prior to display.
 */
public class FilterConfigurator extends IStyleConfigurator {

    /** Viewer used to store the current filter; it will only be changed by the user */
    protected IFilterViewer filterViewer;
    
    /** Constant used to store FilterStyle on the layer blackboard */
    public static String STYLE_ID = ProjectBlackboardConstants.LAYER__STYLE_FILTER;
    
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
            valueChanged();
        }
        @Override
        public void widgetDefaultSelected( SelectionEvent e ) {
        }
    };

    /** Will write filter to blackboard on focus lost */
    private ISelectionChangedListener listener = new ISelectionChangedListener(){
        public void selectionChanged( SelectionChangedEvent event ) {
            if( filterViewer == null || filterViewer.getControl() == null ||  filterViewer.getControl().isDisposed() ){
                return; // nothing to see
            }
            Filter oldValue = getFilterStyle().getFilter();
            Filter filter = filterViewer.getFilter();
            if( filter == null ){
                return; // invalid
            }
            if( !IFilterViewer.same( oldValue, filter ) ){
                valueChanged();
            }
            else {
                // ignore 
            }
        }
    };


    public FilterConfigurator() {
    }
    
    public void valueChanged() {
        if( aoiButton == null || aoiButton.isDisposed() ){
            return; // nothing to see
        }
        FilterStyle style = getFilterStyle();
        Filter filter = style.getFilter();
        boolean isAoiFilter = style.isAoiFilter();
        
        if( (filterViewer.getFilter() == null || !filterViewer.getFilter().equals(filter)) ||
                aoiButton.getSelection() != isAoiFilter ){
            FilterStyle newFilterStyle = new FilterStyle( style );
            newFilterStyle.setFilter( filterViewer.getFilter() );
            newFilterStyle.setAoiFilter( aoiButton.getSelection());

            // this will cause FilterContent to rewrite our memento
            // the actual change won't go out until "apply" or "okay" is pressed
            getStyleBlackboard().put(STYLE_ID, newFilterStyle ); 
        }
    }

    @Override
    public boolean canStyle( Layer aLayer ) {
        if (aLayer.hasResource(FeatureSource.class)) {
            return true;
        }
        return false;
    }
    /**
     * Grab the FilterStyle from the style blackboard.
     * <p>
     * An empty FilterStyle will be returned if required.
     * 
     * @return FilterStyle from style blackboard, an empty FilterStyle will be returned if required.
     */
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
        MigLayout layout = new MigLayout("insets panel", "[][fill]", "[fill][]");
        parent.setLayout(layout);

        Label label = new Label(parent, SWT.SINGLE );
        label.setText("Filter");
        label.setLayoutData("cell 0 0,aligny top, gapx 0 unrelated"); // unrelated spacing after to leave room for label decoration
        
        ControlDecoration decoration = new ControlDecoration(label, SWT.RIGHT | SWT.TOP );
        filterViewer = new FilterViewer(parent, SWT.MULTI );
        filterViewer.getControl().setLayoutData("cell 1 0,grow,width 200:100%:100%,height 60:100%:100%");
        
        FilterInput input = new FilterInput();
        input.setFeedback( decoration );
        filterViewer.setInput(input);
        filterViewer.refresh();
//        label = new Label(parent, SWT.SINGLE );
//        label.setText("Tip: Use the apply button below to preview the selected content");
//        label.setLayoutData("cell 0 1 2 1,left,grow x");

        // Area of Interest filter button
        aoiButton = new Button(parent, SWT.CHECK);
        aoiButton.setText("Area of Interest");
        aoiButton.setLayoutData("cell 0 1 2 1, left, grow x" );
        aoiButton.addSelectionListener(aoiListener);

        listen(true);
    }

    public void listen( boolean listen ) {
        if (listen) {
            filterViewer.addSelectionChangedListener(listener);
            aoiButton.addSelectionListener(aoiListener);
        } else {
            filterViewer.removeSelectionChangedListener(listener);
            aoiButton.removeSelectionListener(aoiListener);
        }
    }
        
    @Override
    protected void refresh() {
        if (filterViewer == null || filterViewer.getControl() == null || filterViewer.getControl().isDisposed()) {
            return;
        }
        
        if( this.aoiButton == null || this.aoiButton.isDisposed()){
            return; // we are shut down and thus ignoring this request to update the ui
        }
        SimpleFeatureType type = getLayer().getSchema();
        FilterInput filterInput = filterViewer.getInput();
        filterInput.setSchema( type );
        
        final FilterStyle style = getFilterStyle();

        filterViewer.getControl().getDisplay().asyncExec(new Runnable(){
            public void run() {
                if (filterViewer == null || filterViewer.getControl() == null || filterViewer.getControl().isDisposed()) {
                    return; // we are shut down and thus ignoring this request to update the ui
                }
                try {
                    listen(false);
                    
                    filterViewer.setFilter( style.getFilter() );
                    filterViewer.refresh();
                    
                    aoiButton.setSelection( style.isAoiFilter() );
                } finally {
                    listen(true);
                }

            }
        });
    }

    @Override
    public void dispose() {
        if (filterViewer != null) {
            listen(false);
            filterViewer = null;
            aoiButton = null;
        }
        super.dispose();
    }
}
