/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.simple;

import java.awt.Color;
import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.geotools.styling.Fill;
import org.geotools.styling.StyleBuilder;
import org.locationtech.udig.style.sld.AbstractSimpleConfigurator;
import org.locationtech.udig.style.sld.SLDPlugin;
import org.locationtech.udig.style.sld.internal.Messages;
import org.locationtech.udig.ui.ColorEditor;
import org.locationtech.udig.ui.graphics.SLDs;

/**
 * Allows editing/viewing of a Style Layer Descriptor "Stroke".
 * <p>
 * Here is the pretty picture: <pre><code>
 *          +-+ +-------+ +------+             
 *    Fill: |x| | color | | 90%\/| 
 *          +-+ +-------+ +------+
 * </code></pre>
 * </p>
 * <p>
 * Workflow:
 * <ol>
 * <li>createControl( parent ) - set up controls
 * <li>setFill( stroke, mode ) - provide content from SimpleStyleConfigurator
 *    <ol>
 *    <li> Symbolizer values copied into fields based on mode
 *    <li> fields copied into controls
 *    <li> controls enabled based on mode & fields
 *    </ol>
 * <li>Listener.widgetSelected/modifyText - User performs an "edit"
 * <li>Listener.sync( SelectionEvent ) - update fields with values of controls
 * <li>fire( SelectionSevent ) - notify SimpleStyleConfigurator of change
 * <li>getFill( StyleBuilder ) - construct a Fill based on fields
 * </ul>
 * </p>  
 * @author Jody Garnett
 * @since 1.0.0
 */
public class FillViewer {
    boolean enabled;
    Color color;
    double opacity;
    
    Button on;
    ColorEditor chooser;
    Combo percent;
    
    private class Listener implements SelectionListener,ModifyListener {
        public void widgetSelected( SelectionEvent e ) { sync( e ); };            
        public void widgetDefaultSelected( SelectionEvent e ) { sync( e ); };
        public void modifyText( ModifyEvent e ) {
            sync(AbstractSimpleConfigurator.selectionEvent(e));
        };            
        private void sync(SelectionEvent selectionEvent ){
            try {
                FillViewer.this.enabled = FillViewer.this.on.getSelection();
                FillViewer.this.color = FillViewer.this.chooser.getColor();
                try {
                    String ptext = FillViewer.this.percent.getText();
                    if(ptext.endsWith("%") ){ //$NON-NLS-1$
                        ptext = ptext.substring(0, ptext.length() - 1);
                        FillViewer.this.opacity = Double.parseDouble(ptext) / 100.0;
                    } else {
                        FillViewer.this.opacity = Double.parseDouble(ptext);
                        if(FillViewer.this.opacity > 1) {
                            FillViewer.this.opacity /= 100.0;
                        }
                    }
                    SLDPlugin.trace( "opacity:"+FillViewer.this.opacity, null ); //$NON-NLS-1$
                } catch (NumberFormatException nan ){
                    // well lets just leave opacity alone
                    SLDPlugin.trace( "opacity:"+nan , nan ); //$NON-NLS-1$
                    throw nan;
                }
                fire( selectionEvent ); // everything worked
            }
            catch( Throwable t ){
                SLDPlugin.trace( "Fill sync failure", t ); //$NON-NLS-1$
            }
            finally {                    
                FillViewer.this.chooser.setEnabled( FillViewer.this.enabled );
                FillViewer.this.percent.setEnabled( FillViewer.this.enabled );                                
            }
        }
        
    };
    Listener sync = new Listener();
    private SelectionListener listener; 
    
    /**
     * Accepts a listener that will be notified when content changes.
     * @param listener1 
     */
    public void addListener( SelectionListener listener1 ) {
        this.listener = listener1;
    }

    /**
     * Remove listener.
     * @param listener1 
     */
    public void removeListener( SelectionListener listener1 ) {
        if (this.listener == listener1)
            this.listener = null;
    }

    /**
     * TODO summary sentence for fire ...
     * 
     * @param event
     */
    protected void fire( SelectionEvent event ) {
        if (this.listener == null)
            return;
        this.listener.widgetSelected(event);
    }
    /**
     * TODO summary sentence for createControl ...
     * 
     * @param parent
     * @param kListener 
     * @return Generated composite
     */
    public Composite createControl(Composite parent, KeyListener kListener) {
        Composite part = AbstractSimpleConfigurator.subpart( parent, Messages.SimpleStyleConfigurator_fill_label );
        
        this.on = new Button( part, SWT.CHECK );
        this.on.addSelectionListener( this.sync );                
        
        this.chooser = new ColorEditor( part, this.sync );
        
        this.percent = new Combo( part, SWT.DROP_DOWN );
        this.percent.setItems( new String[]{ "0%","25%","50%","75%","100%"} );  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        this.percent.setTextLimit( 4 );
        this.percent.addKeyListener(kListener);
        this.percent.setToolTipText(Messages.FillViewer_percent_tooltip); 
        return part; 
    }
    
    /**
     * TODO summary sentence for getFill ...
     * @param build 
     * 
     * @return Fill defined by this model
     */
    public Fill getFill( StyleBuilder build ) {
        if(!this.enabled) return null;
        if(this.opacity != Double.NaN) {
            return build.createFill(this.color, this.opacity);
        }
        return build.createFill(this.color);
    }
    
    void listen(boolean listen) {
        if (listen) {
            this.on.addSelectionListener(this.sync);
            this.chooser.addButtonSelectionListener(this.sync);
            this.percent.addSelectionListener(this.sync);
            this.percent.addModifyListener(this.sync);
        } else {
            this.on.removeSelectionListener(this.sync);
            this.chooser.removeButtonSelectionListener(this.sync);
            this.percent.removeSelectionListener(this.sync);
            this.percent.removeModifyListener(this.sync);
        }
    }
    
    /**
     * TODO summary sentence for setFill ...
     * 
     * @param fill
     * @param mode 
     * @param enabled 
     */
    public void setFill(Fill fill2, Mode mode, Color defaultColor ) {
        listen( false );
        try {
            
            boolean enabled=true;
            Fill fill=fill2;
            if(fill==null ){
                StyleBuilder builder=new StyleBuilder(); 
                fill=builder.createFill(defaultColor, 0.5);
                enabled=false;
            }
            
            this.enabled = enabled && ((mode != Mode.NONE && mode != Mode.LINE) && fill != null);
            this.color = SLDs.color(fill);
            this.opacity = SLDs.opacity(fill);
            
            // Fill is used in point and polygon
            this.on.setEnabled(mode != Mode.NONE && mode != Mode.LINE);
            this.chooser.setColor(this.color);
            
            String text = MessageFormat.format( "{0,number,#0%}", this.opacity); //$NON-NLS-1$
            this.percent.setText(text);
            this.percent.select(this.percent.indexOf(text));
    
            this.on.setSelection( this.enabled );
            this.chooser.setEnabled( this.enabled );
            this.percent.setEnabled( this.enabled );            
        }
        finally {
            listen( true );
        }
    }
}
