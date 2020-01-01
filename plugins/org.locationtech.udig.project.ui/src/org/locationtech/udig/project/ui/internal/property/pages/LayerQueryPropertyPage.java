/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.property.pages;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.geotools.data.Query;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.ui.filter.FilterInput;
import org.locationtech.udig.ui.filter.FilterViewer;
import org.locationtech.udig.ui.filter.IFilterViewer;
import org.locationtech.udig.ui.properties.UDIGPropertyPage;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import net.miginfocom.swt.MigLayout;

/**
 * Preference page for defining the query the layer uses to access data.
 * <p>
 * This page is responsible for editing the {@link ProjectBlackboardConstants#LAYER__DATA_QUERY} value
 * as interpreted by ShowViewInterceptor. This facility is similar to defining a "view" in database
 * in that it can be used to provide a cut-down data set.
 * <p>
 * In order for this property page to work we require that FeatureType be made available.
 * 
 * @author Jody Garnett (LISAsoft)
 */
public class LayerQueryPropertyPage extends UDIGPropertyPage implements IWorkbenchPropertyPage {

    private FilterViewer filter;
    
    /*
     * Used to enable the apply button when content changes
     */
    private ISelectionChangedListener selectionListener = new ISelectionChangedListener(){
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            setApplyButton();
        }
    };
    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents( Composite parent ) {
        Layer layer = getElement( Layer.class );
        
        SimpleFeatureType schema = layer.getSchema();
        // check if layer is polygon
        if (schema == null) {
            // disable!
        }
        Composite page = new Composite(parent, SWT.NONE);
        page.setLayout( new MigLayout("insets panel", "[][fill]", "[fill][]") );

        Label label = new Label(page, SWT.SINGLE );
        label.setText("Data Filter");
        label.setLayoutData("cell 0 0,aligny top, gapx 0 unrelated"); // unrelated spacing after to leave room for label decoration

        ControlDecoration decoration = new ControlDecoration(label, SWT.RIGHT | SWT.TOP );
        filter = new FilterViewer(page, SWT.MULTI );
        filter.getControl().setLayoutData("cell 1 0,grow,width 200:100%:100%,height 60:100%:100%");
        
        FilterInput input = new FilterInput();
        input.setFeedback(decoration);
        input.setRequired(false);
        filter.setInput(input);
        
        loadLayer();
        listen(true);
        
        return page;
    }
    
    public void listen( boolean listen ) {
        if (listen) {
            filter.addSelectionChangedListener(selectionListener);
        } else {
            filter.removeSelectionChangedListener(selectionListener);
        }
    }
    
    
    @Override
    public boolean performOk() {
        saveLayer();
        return super.performOk();
    }
    
    @Override
    protected void performApply() {
        saveLayer();
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        loadLayer();
        super.performDefaults();
    }
    
    /**
     * Look up the {@link ProjectBlackboardConstants#LAYER__DATA_QUERY} on the style blackboard.
     * <p>
     * The provided filter is returned, or {@link Filter#INCLUDE} if not available
     * (as the default is to include all content).
     * 
     * @return Filter, or {@link Filter#INCLUDE} if not available
     */
    Filter getDataQueryFilter(){
        Layer layer = getElement(Layer.class);
        if( layer != null ){
            StyleBlackboard blackboard = layer.getStyleBlackboard();
            Object dataQuery = blackboard.get( ProjectBlackboardConstants.LAYER__DATA_QUERY );
            if( dataQuery instanceof Filter ){
                return (Filter) dataQuery;
            }
            if( dataQuery instanceof Query){
                Query query = (Query) dataQuery;
                return query.getFilter();
            }
        }
        return Filter.INCLUDE;
    }
    /**
     * Update the apply and revert buttons if anything has been modified ...
     */
    protected void setApplyButton(){
        Filter stored = getDataQueryFilter();
        Filter current = filter.getFilter();
        
        boolean changed = !IFilterViewer.same(stored,  current);
        
        this.getApplyButton().setEnabled(changed);
        this.getDefaultsButton().setEnabled(changed);
    }
    
    /*
     * Saves any changes to the style blackboard
     */
    private void saveLayer() {
        Filter stored = getDataQueryFilter();
        Filter current = filter.getFilter();
        if(!IFilterViewer.same(stored,  current)){
            Layer layer = getElement(Layer.class);
            if( layer != null ){
                StyleBlackboard blackboard = layer.getStyleBlackboard();
                blackboard.put(ProjectBlackboardConstants.LAYER__DATA_QUERY, current );
                layer.refresh(null); // force redraw?
            }
        }
    }
    
    /* Grabs the layer and fills in the current page. */
    private void loadLayer() {
        Layer layer = getElement(Layer.class);
        SimpleFeatureType schema = layer != null ? layer.getSchema() : null;
        filter.getInput().setSchema(schema);
        
        boolean enabled = schema != null;
        filter.getControl().setEnabled(enabled);
        
        Filter stored = getDataQueryFilter();
        filter.setFilter(stored);
    }

}
