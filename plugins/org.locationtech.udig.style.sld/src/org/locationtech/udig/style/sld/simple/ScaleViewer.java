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

import org.locationtech.udig.style.sld.AbstractSimpleConfigurator;
import org.locationtech.udig.style.sld.SLDPlugin;
import org.locationtech.udig.style.sld.internal.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Allows editing/viewing of a scale. Used to build the min/max scale editors for a rule.  
 * <p>
 * Here is the pretty picture: <pre><code>
 *          +-+ +-------------+             
 *    Scale:|x| | 90%       \/| 
 *          +-+ +-------------+
 * </code></pre>
 * </p>
 * <p>
 * Workflow:
 * <ol>
 * <li>createControl( parent ) - set up controls
 * <li>setFill( stroke, mode ) - provide fill from {@link SimpleStyleConfigurator} / {@link SimpleRasterConfigurator}
 *    <ol>
 *    <li> scale values got from rules
 *    <li> values copied into controls
 *    <li> controls enabled based on mode & fields
 *    </ol>
 * <li>Listener.widgetSelected/modifyText - User performs an "edit"
 * <li>Listener.sync( SelectionEvent ) - update fields with values of controls
 * <li>fire( SelectionSevent ) - notify SimpleStyleConfigurator/SimpleRasterConfigurator of change
 * <li>getScale( ) - returns the specified scale
 * </ul>
 * </p>  
 * @author Andrea Aime
 * @since 1.1
 */
public class ScaleViewer {
	public static final int MIN = 0;
	public static final int MAX = 1;
	
    boolean enabled;
    double scale;
    int type;
    
    Button on;
    Combo scaleEditor;
    
    private class Listener implements SelectionListener,ModifyListener {
        public void widgetSelected( SelectionEvent e ) { sync( e ); };            
        public void widgetDefaultSelected( SelectionEvent e ) { sync( e ); };
        public void modifyText( ModifyEvent e ) {
            sync(AbstractSimpleConfigurator.selectionEvent(e));
        };            
        private void sync(SelectionEvent selectionEvent ){
            try {
                ScaleViewer.this.enabled = ScaleViewer.this.on.getSelection();
                String ptext = ScaleViewer.this.scaleEditor.getText();
               	ScaleViewer.this.scale = Double.parseDouble(ptext);
                SLDPlugin.trace( "scale: "+ScaleViewer.this.scale, null ); //$NON-NLS-1$
                fire( selectionEvent ); // everything worked
            }
            catch( Throwable t ){
                SLDPlugin.trace( "Scale sync failure", t ); //$NON-NLS-1$
            }
            finally {                    
                ScaleViewer.this.scaleEditor.setEnabled( ScaleViewer.this.enabled );                                
            }
        }
        
    };
    Listener sync = new Listener();
    private SelectionListener listener;
    
    public ScaleViewer(int type) {
    	if(type != MIN && type != MAX)
    		throw new IllegalArgumentException("Type should be either MIN or MAX"); //$NON-NLS-1$
    	this.type = type;
    	this.scale = type == MIN ? 0 : Double.MAX_VALUE;
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
     * TODO summary sentence for createControl ...
     * 
     * @param parent
     * @param kListener 
     * @return Generated composite
     */
    public Composite createControl(Composite parent, KeyListener kListener) {
    	String labelId = type == MIN ? Messages.SimpleStyleConfigurator_minscaleden_label : Messages.SimpleStyleConfigurator_maxscaleden_label;
        Composite part = AbstractSimpleConfigurator.subpart( parent, labelId); 
        
        this.on = new Button( part, SWT.CHECK );
        this.on.addSelectionListener( this.sync );                
        
        this.scaleEditor = new Combo( part, SWT.DROP_DOWN );
        this.scaleEditor.setItems( new String[]{ "100","1000","10000","100000","1000000", "10000000"} );  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        this.scaleEditor.setTextLimit( 10 );
        this.scaleEditor.addKeyListener(kListener);
        String tooltip = type == MIN ? Messages.ScaleViewer_minscaleden_tooltip : Messages.ScaleViewer_maxscaleden_tooltip;
        this.scaleEditor.setToolTipText(tooltip);  
        return part; 
    }
    
    /**
     * Gets the scale denominator chosen by the user, or the default value for this type if none was selected.
     * Default values are 0 for MIN type, {@linkplain Double#MAX_VALUE} for the MAX type
     * @param build 
     * 
     * @return Fill defined by this model
     */
    public double getScale() {
    	if(!this.enabled)
    		return type == MIN ? 0 : Double.MAX_VALUE;
    	else
    		return scale;
    }
    
    /**
     * Sets the scale denominator, or disables the component if the provided scale is not a positive number
     * @param scale
     */
    public void setScale(double scale2, long defaultScale) {
    	listen( false );
        

        this.scale=scale2;
        this.enabled=true;
        if( Double.isNaN(scale) || Double.isInfinite(scale) || scale<=Double.MIN_VALUE || scale >= Double.MAX_VALUE){
            this.scale=defaultScale;
            this.enabled=false;
        }

    	scaleEditor.setText(Double.toString(scale)); 
        this.on.setSelection( this.enabled );
        this.scaleEditor.setEnabled( this.enabled );
        listen( true );
    }
    
    
    void listen(boolean listen) {
        if( listen ){
            this.on.addSelectionListener(this.sync);
            this.scaleEditor.addSelectionListener( this.sync );
            this.scaleEditor.addModifyListener(this.sync);
        } else {
            this.on.removeSelectionListener(this.sync);
            this.scaleEditor.removeSelectionListener( this.sync );
            this.scaleEditor.removeModifyListener( this.sync );
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
    
}
