package net.refractions.udig.style.filter;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.filter.FilterViewer;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQL;
import org.opengis.filter.Filter;

public class FilterConfigurator extends IStyleConfigurator {
    public static String STYLE_ID = ProjectBlackboardConstants.LAYER__DATA_QUERY;

    protected Filter filter;
    protected FilterViewer text;

    /** Will write filter to blackboard on focus lost */
    private ISelectionChangedListener listener = new ISelectionChangedListener(){
        public void selectionChanged( SelectionChangedEvent event ) {
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
        Filter oldValue = filter;
        filter = text.getInput();
        valueChanged(oldValue, filter);
    }

    @Override
    public boolean canStyle( Layer aLayer ) {
        if (aLayer.hasResource(FeatureSource.class)) {
            return true;
        }
        return false;
    }

    protected Filter getStyle() {
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
        parent.setLayout(new MigLayout("", "[right]rel[left, grow]", "[c,grow 75,fill]"));

        Label label = new Label(parent, SWT.MULTI | SWT.WRAP );
        label.setText("Filter");

        text = new FilterViewer(parent );
        text.getControl().setLayoutData("growx, growy, span, wrap");
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
        final Filter style = getStyle();

        text.getControl().getDisplay().asyncExec(new Runnable(){
            public void run() {
                if (text == null || text.getControl() == null || text.getControl().isDisposed()) {
                    return;
                }
                try {
                    listen(false);
                    filter = style;
                    text.setInput( filter );
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
