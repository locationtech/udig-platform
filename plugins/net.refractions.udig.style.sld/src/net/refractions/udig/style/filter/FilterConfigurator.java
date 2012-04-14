package net.refractions.udig.style.filter;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.filter.FilterViewer;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;
import net.refractions.udig.ui.filter.CQLFilterViewer;
import net.refractions.udig.ui.filter.FilterInput;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.util.Utilities;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

public class FilterConfigurator extends IStyleConfigurator {
    public static String STYLE_ID = ProjectBlackboardConstants.LAYER__DATA_QUERY;

    //protected Filter filter;
    
    protected CQLFilterViewer text;

    /** Will write filter to blackboard on focus lost */
    private ISelectionChangedListener listener = new ISelectionChangedListener(){
        public void selectionChanged( SelectionChangedEvent event ) {
            if( text == null || text.getControl() == null ||  text.getControl().isDisposed() ){
                return; // nothing to see
            }
            
            Filter oldValue = getStyleFilter();
            Filter filter = text.getFilter();
            if( filter == null ){
                return; // invalid
            }
            String before = filter != null ? CQL.toCQL(oldValue) : "(empty)";
            String after = filter != null ? CQL.toCQL(filter) : "(empty)";
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
        parent.setLayout(new MigLayout("", "[right]10[left, grow]", "[c,grow 75,fill]"));

        Label label = new Label(parent, SWT.SINGLE );
        label.setText("Filter");

        ControlDecoration decoration = new ControlDecoration(label, SWT.RIGHT | SWT.TOP );
        text = new CQLFilterViewer(parent, SWT.MULTI );
        text.getControl().setLayoutData("growx, growy, span, wrap");
        
        FilterInput input = new FilterInput();
        input.setFeedback( decoration );
        text.setInput(input);
        text.refresh();
        
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
            text.addSelectionChangedListener(listener);
        } else {
            text.removeSelectionChangedListener(listener);
        }
    }
    
    @Override
    protected void refresh() {
        if (text == null || text.getControl() == null || text.getControl().isDisposed()) {
            return;
        }
        SimpleFeatureType type = getLayer().getSchema();
        text.getInput().setSchema( type );
        
        final Filter style = getStyleFilter();

        text.getControl().getDisplay().asyncExec(new Runnable(){
            public void run() {
                if (text == null || text.getControl() == null || text.getControl().isDisposed()) {
                    return;
                }
                try {
                    listen(false);
                    text.setFilter( style );
                    text.refresh();
                } finally {
                    listen(true);
                }
            }
        });
    }

    @Override
    public void dispose() {
        if (text != null) {
            listen(false);
            text = null;
        }
        super.dispose();
    }
}
