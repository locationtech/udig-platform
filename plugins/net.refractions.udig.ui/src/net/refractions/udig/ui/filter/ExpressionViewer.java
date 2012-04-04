package net.refractions.udig.ui.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.PageBook;
import org.eclipse.update.internal.ui.parts.DefaultContentProvider;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.expression.Expression;

public class ExpressionViewer extends IExpressionViewer {
    /** Extension point ID */
    public static final String FILTER_VIEWER_EXTENSION = "net.refractions.udig.ui.filterViewer";

    private static ArrayList<ExpressionViewerFactory> list;

    /**
     * ExpressionViewer we are currently delegating to.
     */
    IExpressionViewer delegate;

    ComboViewer choice;

    private SimpleFeatureType schema;

    private Composite control;

    private PageBook flip;

    private Map<ExpressionViewerFactory, IExpressionViewer> pages;

    private Label label;

    public ExpressionViewer( Composite parent ) {
        this(parent, SWT.DEFAULT);
    }

    public ExpressionViewer( Composite parent, int style ) {
        super(parent, style);
        control = new Composite(parent, SWT.DEFAULT);

        choice = new ComboViewer(control);
        choice.setContentProvider(ArrayContentProvider.getInstance());
        choice.setLabelProvider(new LabelProvider(){
            @Override
            public String getText( Object element ) {
                if (element instanceof ExpressionViewerFactory) {
                    ExpressionViewerFactory factory = (ExpressionViewerFactory) element;
                    return factory.getDisplayName();
                }
                return super.getText(element);
            }
        });
        List<ExpressionViewerFactory> factories = factories();
        choice.setInput(factories);
        ExpressionViewerFactory currentFactory = factories.get(0);
        choice.setSelection(new StructuredSelection(currentFactory));

        flip = new PageBook(control,  SWT.DEFAULT );
        
        label = new Label(flip, SWT.DEFAULT);
        label.setText("--");
        
        // use SWT.DEFALT as we are expecting to be "inline" ...
        delegate = currentFactory.createViewer(flip, SWT.DEFAULT);
        
        pages = new HashMap<ExpressionViewerFactory,IExpressionViewer>();
        pages.put( currentFactory, delegate );
    }

    private synchronized static List<ExpressionViewerFactory> factories() {
        if (list == null) {
//            list = new ArrayList<ExpressionViewerFactory>();
//            list.add(new DefaultExpressionViewer.Factory());
            IExtensionRegistry registery = Platform.getExtensionRegistry();
            IConfigurationElement[] stuff = registery.getConfigurationElementsFor(FILTER_VIEWER_EXTENSION);
            for( IConfigurationElement config : stuff ){
                if( "expressionViewer".equals( config.getName() )){
                    try {
                        ExpressionViewerFactory factory;
                        factory = (ExpressionViewerFactory) config.createExecutableExtension("class");
                        factory.init( config );
                    
                        list.add( factory );
                    } catch (CoreException e) {
                        String pluginId = config.getContributor().getName();
                        IStatus status = new Status(IStatus.WARNING, pluginId, e.getMessage(), e );
                        UiPlugin.log( status );
                    }
                }
                else {
                    // skip as it is probably a filterViewer element
                }
            }
        }
        return list;
    }
    /** Called by the combo box when the user has changed what factory they want to use */
    protected void changeDelegate( ExpressionViewerFactory factory ){
        IExpressionViewer page;
        if( pages.containsKey(factory)){
            page = pages.get(factory);
        }
        else {
            page = factory.createViewer( control, SWT.DEFAULT );
        }
        if( delegate == page ){
            // no change what are you doing!
            return;
        }
        if( delegate != null ){
            // stop listening!
        }
        if( page != null ){
            delegate = page;
            // hook up new delegate!
            delegate.setSchema( schema );
            flip.showPage( delegate.getControl() );
        }
        else {
            flip.showPage( label );
        }
    }
    
    public void feedback() {
        delegate.feedback();
    }

    public String getValidationMessage() {
        return delegate.getValidationMessage();
    }

    public Expression getInput() {
        return delegate.getInput();
    }

    public ISelection getSelection() {
        return delegate.getSelection();
    }

    public void addHelpListener( HelpListener listener ) {
        delegate.addHelpListener(listener);
    }

    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        delegate.addSelectionChangedListener(listener);
    }

    public boolean equals( Object obj ) {
        return delegate.equals(obj);
    }

    public Control getControl() {
        return control;
    }

    public void feedback( String warning ) {
        delegate.feedback(warning);
    }

    public void feedback( String exception, Exception eek ) {
        delegate.feedback(exception, eek);
    }

    public SimpleFeatureType getSchema() {
        return delegate.getSchema();
    }

    public Class< ? > getExpected() {
        return delegate.getExpected();
    }

    public Object getData( String key ) {
        return delegate.getData(key);
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public void setRequired( boolean required ) {
        delegate.setRequired(required);
    }

    public boolean isRequired() {
        return delegate.isRequired();
    }

    public void refresh() {
        delegate.refresh();
    }

    public void setInput( Object input ) {
        delegate.setInput(input);
    }

    public void setSelection( ISelection selection, boolean reveal ) {
        delegate.setSelection(selection, reveal);
    }

    public void setSchema( SimpleFeatureType schema ) {
        this.schema = schema;
        delegate.setSchema(schema);
    }

    public void setExpected( Class< ? > binding ) {
        delegate.setExpected(binding);
    }

    public void removeHelpListener( HelpListener listener ) {
        delegate.removeHelpListener(listener);
    }

    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        delegate.removeSelectionChangedListener(listener);
    }

    public Item scrollDown( int x, int y ) {
        return delegate.scrollDown(x, y);
    }

    public Item scrollUp( int x, int y ) {
        return delegate.scrollUp(x, y);
    }

    public void setData( String key, Object value ) {
        delegate.setData(key, value);
    }

    public void setSelection( ISelection selection ) {
        delegate.setSelection(selection);
    }

    public String toString() {
        return delegate.toString();
    }

}
