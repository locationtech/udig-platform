/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

import java.util.EventObject;

import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.locationtech.udig.filter.ExpressionViewer;
import org.locationtech.udig.style.sld.IStyleEditorPageContainer;
import org.locationtech.udig.style.sld.SLDContentManager;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.filter.expression.Expression;

import net.miginfocom.swt.MigLayout;


/**
 * StyleEditorPage allowing the user to modify a LineSymbolizer
 * for the "default" Style.
 * @author Jody Garnett
 * @since 1.2.0
 */
public class LineEditorPage extends StyleEditorPage {
    protected StyleBuilder sb = new StyleBuilder();
    
    private ExpressionViewer size;
    private ISelectionChangedListener listener = new ISelectionChangedListener(){        
        public void selectionChanged( SelectionChangedEvent event ) {
            sync( event );
        }
    };
    private ExpressionViewer color;
    
    public LineEditorPage() {
    }

    @Override
    public void createPageContent( Composite parent ) {
        // leave enough room for feedback decoration on fields!
        int required = FieldDecorationRegistry.getDefault().getMaximumDecorationWidth();
        parent.setLayout( new MigLayout("","[right]"+required+"[left,grow]") );
    	
        Label label = new Label(parent, SWT.LEFT );
        label.setText("Size");

        size = new ExpressionViewer( parent, SWT.SINGLE | SWT.BORDER );
        size.setRequired(true);
        size.getControl().setLayoutData("growx, wrap");
        
        label = new Label(parent, SWT.LEFT );
        label.setText("Color");
        
        color = new ExpressionViewer( parent, SWT.SINGLE | SWT.BORDER );
        color.getControl().setLayoutData("growx, wrap");
        
        listen( true );
    }
    
    private void listen( boolean listen ) {
        if( listen ){
            size.addSelectionChangedListener( listener );
            color.addSelectionChangedListener( listener );
        }
        else {
            size.removeSelectionChangedListener( listener );
            size.removeSelectionChangedListener(listener);
        }
    }


    @Override
    public String getErrorMessage() {
        String message = null; // everything is good so far
        
        if( message != null ){
            if( size.getInput() == Expression.NIL ){
                message = "Size is required";   
            }
        }
        // check over content and return
        // non null if in error
        return message;
    }

    @Override
    public String getLabel() {
        return null; // not sure what this is for we need to update the javadocs
    }

    @Override
    public void gotFocus() {
        refresh();
        size.getControl().setFocus();
    }

    @Override
    public boolean performCancel() {
        return true; // simply do not do anything
    }

    @Override
    public void styleChanged( Object source ) {
        // grab our line symbolizer again ...
        // and refresh
        System.out.println( source );
        
        refresh();
    }

    public boolean okToLeave() {
        return getErrorMessage() == null;
    }

    public boolean performApply() {
        // we always update the style
        // so we will return true here saying we are good to go
        // sync( null );
        return true;
    }
    
    /** Grab to Stroke from the lineSymbolizer creating if needed */
    Stroke toStroke( LineSymbolizer lineSymbolizer ){        
        Stroke stroke = SLDs.stroke( lineSymbolizer );
        if( stroke == null ){
            stroke = sb.createStroke();
            lineSymbolizer.setStroke( stroke );
        }
        return stroke;
    }
    
    /**
     * Callback from all the individual change listeners used to update the
     * Style with any modification done by the user.
     * @param event That caused the change; null to sync all controls
     */
    void sync( EventObject event ){
        SLDContentManager content = new SLDContentManager( sb, getContainer().getStyle() );        
        LineSymbolizer lineSymbolizer = content.getSymbolizer( LineSymbolizer.class );
        
        Object source = event != null ? event.getSource() : null;        
        if( source == null || source == size ){
            toStroke( lineSymbolizer ).setWidth( size.getInput() );
        }
        if( source == null || source == color ){
            toStroke( lineSymbolizer ).setColor( color.getInput() );
        }
    }
        
    public boolean performOk() {
        return performApply();
    }
            
    public void refresh() {        
        IStyleEditorPageContainer container = getContainer();
        // StyleLayer layer = container.getSelectedLayer();
        // SimpleFeatureType schema = layer.getSchema();
        Style style = container.getStyle();
        LineSymbolizer lineSymbolizer = SLDs.lineSymbolizer(style);
        
        try {
            listen( false ); // do not issue events while controls being updated by hand
            if( lineSymbolizer == null ){
                // nothing to do here - kind of like a geometry of "(none)"
                //
                size.getControl().setEnabled(false);
                size.setInput( Expression.NIL );
                color.getControl().setEnabled(false);
                color.setInput( Expression.NIL );
                return;
            }
            Stroke stroke = lineSymbolizer.getStroke();            
            if( stroke == null ){
                // no stroke? kind of like width == 0
                size.setInput( Expression.NIL );
                color.setInput( Expression.NIL );
            }
            size.setInput( stroke.getWidth() );
            size.getControl().setEnabled(true);
            color.setInput( stroke.getColor() );
            color.getControl().setEnabled(true);
        }
        finally {
            listen( true );
        }
    }
}
