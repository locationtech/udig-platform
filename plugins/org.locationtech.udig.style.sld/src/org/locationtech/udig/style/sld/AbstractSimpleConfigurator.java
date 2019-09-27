/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.style.sld;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.IStyleConfigurator;

/**
 * This is here to save me some typing and ensure that the simple raster and 
 * feature configurators look similar.
 * @author mleslie
 * @since 0.6.0
 */
public abstract class AbstractSimpleConfigurator extends IStyleConfigurator {
    /** <code>build</code> field */
    protected StyleBuilder build = new StyleBuilder();
    
    /**
     * Construct <code>AbstractSimpleConfigurator</code>.
     *
     */
    public AbstractSimpleConfigurator() {
        super();
    }

    @Override
    public abstract boolean canStyle( Layer aLayer );

    @Override
    protected abstract void refresh();

    @Override
    public abstract void createControl( Composite parent );
    
    /**
     * TODO summary sentence for synchronize ...
     * 
     */
    public abstract void synchronize();

    protected void setLayout(Composite parent) {
        RowLayout layout = new RowLayout();        
        layout.pack = false;
        layout.wrap = true;
        layout.type = SWT.HORIZONTAL;
        layout.fill = true;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.spacing = 0;
        parent.setLayout(layout); 
    }

    /**
     * Retrieves the style object from the style blackboard.
     * 
     * @return Style
     */
    protected Style getStyle(){
        Layer layer = getLayer();
        assert( canStyle( layer ));
        // TODO: Ensure framework does not show me when I can't handle this
        // layer
        // TODO: Stop commenting in the first person, as though I am the 
        // class.  It's creepy.
        Style style = (Style) getStyleBlackboard().get(SLDContent.ID);
        
        // if no style information, create default
        if (style == null) {
            // TODO: Why do "I" have to do this, can't the framework deal?
            // (It could if I advertise that I deal with SLDContentID type?)
            style = SLDContent.createDefaultStyle();
            getStyleBlackboard().put(SLDContent.ID, style);
            // XXX: This may throw out an event and cause some debugging grief?
        }
        return style;
    }
    
    /**
     * Construct a subpart labeled with the provided tag.
     * <p>
     * Creates a composite with a grid layout of the specifed columns,
     * and a label with text from label.
     * </p>
     * @param parent
     * @param label
     * @return Composite with one label 
     */
    public static Composite subpart( Composite parent, String label ){
        Composite subpart = new Composite( parent, SWT.NONE );        
        RowLayout across = new RowLayout();
        across.type = SWT.HORIZONTAL;
        across.wrap = true;
        across.pack = true;
        across.fill = true;
        across.marginBottom = 1;
        across.marginRight = 2;
        
        subpart.setLayout( across );
        
        Label labell = new Label( subpart, SWT.LEFT );
        labell.setText( label );  
        
        RowData data = new RowData();
        data.width = 40;
        //check to see if width is not enough space
        GC gc = new GC(parent.getParent());
        gc.setFont(parent.getParent().getFont());
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();
        int labelWidth = Dialog.convertWidthInCharsToPixels(fontMetrics, labell.getText().length()+1);
        if (labelWidth > data.width) {
        	data.width = labelWidth; 
        }
        // TODO: adjust the methods that call this one to keep a consistent
		// width (otherwise they're misaligned)
        data.height = 10;
        labell.setLayoutData( data );
      
        return subpart;
    }    
    /**
     * Morph a text ModifyEvent into a SelectionEvent as best we can.
     * <p>
     * This may be a bad abuse of the event system, it appears to be in
     * use because we are too lazy to specify a new event type for style
     * modification.
     * </p>
     * <p>
     * However this does seem to be in keeping with the purpose of SelectionEvent
     * it already isolates out code from TypedEvents by providing a summary of what
     * changed in which widet.
     * </p>
     * @param e
     * @return A SelectionEvent based on the provided modify event
     */
    public static SelectionEvent selectionEvent(final ModifyEvent e ) {
        Event event = new Event();
        event.widget = e.widget;
        event.data = e.data;
        event.display = e.display;
        event.time = e.time;
        return new SelectionEvent(event){
            /** <code>serialVersionUID</code> field */
            private static final long serialVersionUID = 6544345585295778029L;

            @Override
            public Object getSource() {
                return e.getSource();
            }
        };
    };
}
