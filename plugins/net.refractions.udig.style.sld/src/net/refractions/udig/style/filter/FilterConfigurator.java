package net.refractions.udig.style.filter;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;
import net.refractions.udig.ui.filter.DefaultFilterViewer;
import net.refractions.udig.ui.filter.FilterInput;
import net.refractions.udig.ui.filter.FilterViewer;
import net.refractions.udig.ui.filter.IFilterViewer;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.Utilities;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

public class FilterConfigurator extends IStyleConfigurator {
    public static String STYLE_ID = ProjectBlackboardConstants.LAYER__DATA_QUERY;

    /** Viewer used to store the current filter; it will only be changed by the user */
    protected IFilterViewer filterViewer;

    /** Will write filter to blackboard on focus lost */
    private ISelectionChangedListener listener = new ISelectionChangedListener(){
        public void selectionChanged( SelectionChangedEvent event ) {
            if( filterViewer == null || filterViewer.getControl() == null ||  filterViewer.getControl().isDisposed() ){
                return; // nothing to see
            }
            
            Filter oldValue = getStyleFilter();
            Filter filter = filterViewer.getFilter();
            if( filter == null ){
                return; // invalid
            }
            String before = filter != null ? ECQL.toCQL(oldValue) : "(empty)";
            String after = filter != null ? ECQL.toCQL(filter) : "(empty)";
            if (!Utilities.equals(before, after)){
                valueChanged(oldValue, filter);
            }
        }
    };

    public FilterConfigurator() {
    }

    @Override
    public boolean canStyle( Layer aLayer ) {
        if (aLayer.hasResource(FeatureSource.class)) {
            return true;
        }
        return false;
    }

    protected Filter getStyleFilter() {
        Layer layer = getLayer();
        assert (canStyle(layer));

        Object current = getStyleBlackboard().get(STYLE_ID);
        if (current == null) {
            return Filter.INCLUDE;
        } else if (current instanceof Filter) {
            return (Filter) current;
        } else if (current instanceof Query) {
            Query query = (Query) current;
            return query.getFilter();
        }
        return null; // not available
    }

    @Override
    public void createControl( Composite parent ) {
        MigLayout layout = new MigLayout("insets panel", "[][fill]", "[fill][]");
        parent.setLayout(layout);

        Label label = new Label(parent, SWT.SINGLE );
        label.setText("Filter");
        label.setLayoutData("cell 0 0,aligny top");
        
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
        listen(true);
    }

    protected void valueChanged( Filter oldValue, Filter newValue ) {
        if (oldValue == newValue || (oldValue != null && oldValue.equals(newValue))) {
            // nothing to change here
        } else {
            getStyleBlackboard().put(STYLE_ID, newValue);
        }
    }

    public void listen( boolean listen ) {
        if (listen) {
            filterViewer.addSelectionChangedListener(listener);
        } else {
            filterViewer.removeSelectionChangedListener(listener);
        }
    }
    
    @Override
    protected void refresh() {
        if (filterViewer == null || filterViewer.getControl() == null || filterViewer.getControl().isDisposed()) {
            return;
        }
        SimpleFeatureType type = getLayer().getSchema();
        FilterInput filterInput = filterViewer.getInput();
        filterInput.setSchema( type );
        
        final Filter style = getStyleFilter();

        filterViewer.getControl().getDisplay().asyncExec(new Runnable(){
            public void run() {
                if (filterViewer == null || filterViewer.getControl() == null || filterViewer.getControl().isDisposed()) {
                    return;
                }
                try {
                    listen(false);
                    filterViewer.setFilter( style );
                    filterViewer.refresh();
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
        }
        super.dispose();
    }
}
