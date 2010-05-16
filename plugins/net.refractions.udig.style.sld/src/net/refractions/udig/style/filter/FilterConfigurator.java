package net.refractions.udig.style.filter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.wms.WebMapServer;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.styling.Style;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.filter.Filter;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;
import net.refractions.udig.style.sld.SLDContent;
import net.refractions.udig.style.sld.SLDPlugin;

public class FilterConfigurator extends IStyleConfigurator {
    public static String STYLE_ID = ProjectBlackboardConstants.LAYER__DATA_QUERY;
    
    protected Filter filter;
    protected Text text;
    
    /** Will continue to update filter on key release */
    private KeyListener keyListener = new KeyAdapter(){
        public void keyReleased( KeyEvent e ) {
            if( checkValid() ){
                externalUpdate();
            }
        }
    };
    
    /** Will write filter to blackboard on focus lost */
    private FocusListener focusListener = new FocusAdapter(){
        public void focusLost( FocusEvent e ) {
            if( checkValid() ){
                externalUpdate();
            }
            else {
                // unable to save out filter :-(
                SLDPlugin.getDefault().log("Filter not valid:"+text.getToolTipText(), null );
            }
        }
    };

    public FilterConfigurator() {
    }

    
    /**
     * Update the internal filter; will set tooltip text as required in
     * the event the filter does not parse.
     */
    public boolean checkValid(){
        if( text == null || text.isDisposed() ){
            return false; // nothing to see
        }
        Filter test;
        String cql = text.getText();
        if( cql == null || cql.length() == 0){
            test = Filter.INCLUDE; // no filtering happening
            return true;
        }
        try {
            test = CQL.toFilter( cql );
            text.setToolTipText( CQL.toCQL(test));
            return true;
        }
        catch (Throwable t ){
            test = Filter.EXCLUDE;
            text.setToolTipText( t.getLocalizedMessage() );
            return false;
        }
    }
    
    public void externalUpdate(){
        Filter oldValue = filter;
        String cql = text.getText();
        if( cql == null || cql.length() == 0){
            filter = Filter.INCLUDE; // no filtering happening
        }
        try {
            filter = CQL.toFilter( cql );
            text.setToolTipText( CQL.toCQL(filter));
            
            text.setToolTipText(null);
        }
        catch (Throwable t ){
            filter = Filter.EXCLUDE;
            text.setToolTipText( t.getLocalizedMessage() );
        }
        valueChanged(oldValue, filter);
    }
    
    @Override
    public boolean canStyle( Layer aLayer ) {
        if (aLayer.hasResource(FeatureSource.class)){
            return true;
        }
        return false;
    }

    protected Filter getStyle(){
        Layer layer = getLayer();
        assert( canStyle( layer ));
        
        Object current = getStyleBlackboard().get( STYLE_ID );
        if( current == null ){
            return Filter.INCLUDE;
        }
        else if( current instanceof Filter ){
            return (Filter) current;
        }
        else if( current instanceof Query ){
            Query query = (Query) current;
            return query.getFilter();
        }
        return null; // not available
    }
    
    @Override
    public void createControl( Composite parent ) {
        parent.setLayout(new MigLayout("", "[right]rel[left, grow]", "[c,grow 75,fill]"));
        
        Label label = new Label( parent, SWT.NONE );
        label.setText("Filter");
        
        text = new Text( parent, SWT.V_SCROLL | SWT.WRAP | SWT.BORDER );
        text.setLayoutData("growx, growy, span, wrap");
        
        listen( true );
    }

    protected void valueChanged( Filter oldValue, Filter newValue ) {
        if( oldValue == newValue ||
                (oldValue!=null&&oldValue.equals(newValue))){
            // nothing to change here
        }
        else {
            getStyleBlackboard().put(STYLE_ID, newValue );
        }        
    }

    public void listen( boolean listen ){
        if( listen ){
            text.addKeyListener( keyListener );
            text.addFocusListener( focusListener );
        }
        else {
            text.removeKeyListener( keyListener );
            text.removeFocusListener( focusListener );
        }
    }
    @Override
    protected void refresh() {
        if( text == null || text.isDisposed() ){
            return;
        }
        final Filter style = getStyle();
        
        text.getDisplay().asyncExec(new Runnable(){
            public void run() {
                if( text == null || text.isDisposed() ){
                    return; // nothing to do widget is gone
                }
                try {
                    listen(false);
                    filter = style;
                    if( style == Filter.INCLUDE ){
                        text.setText("include");
                    }
                    else {
                        String cql = CQL.toCQL( style );
                    
                        if( cql == null || cql.isEmpty() ){
                            text.setText("");
                        }
                        else {
                            text.setText(cql);
                        }
                    }
                }
                finally {
                    listen(true);
                }
            }
        });
    }
    
    @Override
    public void dispose() {
        if( text != null ){
            listen( false );
            text = null;
        }
        super.dispose();
    }
}
