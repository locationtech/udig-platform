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
package net.refractions.udig.style.sld.simple;

import java.text.MessageFormat;

import net.refractions.udig.style.sld.AbstractSimpleConfigurator;
import net.refractions.udig.style.sld.internal.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.StyleBuilder;
import org.opengis.filter.expression.Expression;

/**
 * Allows editing/viewing of a Style Layer Descriptor "Raster".
 * <p>
 * Here is the pretty picture: <pre><code>
 *          +-+ +----------------+
 *   Label: |x| |       opacity\/|
 *          +-+ +----------------+
 * </code></pre>
 * </p>
 * <p>
 * Workflow:
 * <ol>
 * <li>createControl( parent ) - set up controls
 * <li>set( raster, mode ) - provide content from SimpleStyleConfigurator
 *    <ol>
 *    <li> Symbolizer values copied into fields based on mode
 *    <li> fields copied into controls
 *    <li> controls enabled based on mode & fields
 *    </ol>
 * <li>Listener.widgetSelected/modifyText - User performs an "edit"
 * <li>Listener.sync( SelectionEvent ) - update fields with values of controls
 * <li>fire( SelectionSevent ) - notify SimpleStyleConfigurator of change
 * <li>get( StyleBuilder ) - construct based on fields
 * </ul>
 * </p>  
 * @author mleslie
 * @since 1.0.0
 */
public class RasterViewer {
    boolean enabled = false;
    double opacity = Double.NaN;
    
    Button on;
    Combo percent;
    private SelectionListener listener; 

    
    private class Listener implements SelectionListener,ModifyListener {
        public void widgetSelected( SelectionEvent e ) { sync( e ); };            
        public void widgetDefaultSelected( SelectionEvent e ) { sync( e ); };
        public void modifyText( ModifyEvent e ) {
            sync(AbstractSimpleConfigurator.selectionEvent(e));
        };           
        private void sync(SelectionEvent e){
            try {
                RasterViewer.this.enabled = RasterViewer.this.on.getSelection();
                try {
                    String ptext = RasterViewer.this.percent.getText();
                    if(ptext.endsWith("%") ){ //$NON-NLS-1$
                        ptext = ptext.substring(0, ptext.length() - 1);
                        RasterViewer.this.opacity = Double.parseDouble(ptext);
                        RasterViewer.this.opacity /= 100.0;
                    } else {
                        RasterViewer.this.opacity = Double.parseDouble(ptext);
                        if(RasterViewer.this.opacity > 1) {
                            RasterViewer.this.opacity /= 100.0;
                        }
                    }                        
                } catch (NumberFormatException nan ){
                    // well lets just leave opacity alone
                }
                fire(e); // everything worked
            }
            catch( Throwable t ){
                // meh
            }
            finally {                    
                RasterViewer.this.percent.setEnabled(RasterViewer.this.enabled);
            }
        }
        
    };
    Listener sync = new Listener();
    
    /**
     * TODO summary sentence for createControl ...
     * 
     * @param parent
     * @param listener1 
     * @return Generated composite
     */
    public Composite createControl( Composite parent, KeyListener listener1 ){
        Composite part = AbstractSimpleConfigurator.subpart( parent, Messages.SimpleStyleConfigurator_raster_label );
        
        this.on = new Button( part, SWT.CHECK );
        
        this.percent = new Combo( part, SWT.DROP_DOWN );
        this.percent.setItems( new String[]{ "0%","25%","50%","75%","100%"} );    //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
        this.percent.setTextLimit( 4 );
        this.percent.addKeyListener(listener1);
        this.percent.setToolTipText(Messages.RasterViewer_percent_tooltip); 
        return part;
    }
    
    void listen( boolean listen ){
        if( listen ){
            this.on.addSelectionListener(this.sync);
            this.percent.addSelectionListener( this.sync );
            this.percent.addModifyListener( this.sync );
        }
        else {
            this.on.removeSelectionListener(this.sync);
            this.percent.removeSelectionListener( this.sync );
            this.percent.removeModifyListener( this.sync );
        }
    }
    
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
     * Called to set up this "viewer" based on the provided symbolizer 
     * @param sym 
     */
    public void set( RasterSymbolizer sym2 ){
        listen( false ); // don't sync when setting up
        try {
            RasterSymbolizer sym=sym2;
            this.enabled = (sym != null);
            
            if( sym==null ){
                StyleBuilder builder=new StyleBuilder();
                sym=builder.createRasterSymbolizer();
                sym.setOpacity((Expression) CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints()).literal(1.0));
            }
            this.opacity = SLD.rasterOpacity(sym);
            String text = MessageFormat.format( "{0,number,#0%}", this.opacity); //$NON-NLS-1$
            this.percent.setText(text);
            this.percent.select(this.percent.indexOf(text));
            this.on.setSelection(this.enabled);
            this.percent.setEnabled(this.enabled);
        }
        finally {
            listen( true ); // listen to user now
        }
    }
    
    /**
     * TODO summary sentence for getStroke ...
     * @param build 
     * 
     * @return Stroke defined by this model
     */
    public RasterSymbolizer get(StyleBuilder build) {
        return (opacity == Double.NaN || !enabled) ? null : 
                build.createRasterSymbolizer(null, this.opacity);
    }
}
