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
package org.locationtech.udig.project.ui.controls;

import java.text.NumberFormat;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.project.internal.commands.SetScaleCommand;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.render.IViewportModelListener;
import org.locationtech.udig.project.render.ViewportModelEvent;
import org.locationtech.udig.project.render.ViewportModelEvent.EventType;
import org.locationtech.udig.project.ui.internal.MapEditorPart;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.ui.ZoomingDialog;

/**
 * Displays the current scale ratio on the status bar.
 * 
 * @author Andrea Aime
 */
public class ScaleRatioLabel extends ContributionItem implements KeyListener, FocusListener {
    public final static int STATUS_LINE_HEIGHT;
    static {
        if (Platform.getWS().equals(Platform.WS_WIN32)) {
            STATUS_LINE_HEIGHT = 24;
        } else {
            STATUS_LINE_HEIGHT = 32;
        }
    }
    /** ScaleRatioLabel editor field */
    private final MapEditorPart mapPart;
    public static final String SCALE_ITEM_ID = "Current scale"; //$NON-NLS-1$
    NumberFormat nf = NumberFormat.getIntegerInstance();
    
    Combo combo;    
    IViewportModel viewportModel;
    
    /** Listens to viewport changes and updates the displayed scale accordingly */
    IViewportModelListener listener = new IViewportModelListener(){
        public void changed( ViewportModelEvent event ) {
            if (event.getType() == EventType.CRS || event.getType() == EventType.BOUNDS) {
                Display display = PlatformUI.getWorkbench().getDisplay();
                if (display == null)
                    display = Display.getDefault();

                display.asyncExec(new Runnable(){

                    public void run() {
                        updateScale();
                    }
                });
            }
        }
    };    

    public ScaleRatioLabel(MapEditorPart editor) {
        super(SCALE_ITEM_ID);
        this.mapPart = editor;
    }

    /**
     * Sets the current viewport model. Should be called every time the map changes in order
     * update the shared ratio label
     */
    public void setViewportModel( IViewportModel newViewportModel ) {
        // if(newViewportModel != null)
        // System.out.println(System.currentTimeMillis() + " - changing viewport model - map " +
        // newViewportModel.getMap().getName()); //$NON-NLS-1$
        if (newViewportModel != this.viewportModel) {
            if (viewportModel != null) {
                viewportModel.removeViewportModelListener(listener);
            }
            viewportModel = newViewportModel;
            viewportModel.addViewportModelListener(listener);
            updateScale();
        }
    }

    /**
     * @see org.eclipse.jface.action.IContributionItem#isDynamic()
     */
    public boolean isDynamic() {
        return true;
    }

    /**
     * @see org.eclipse.jface.action.ContributionItem#dispose()
     */
    public void dispose() {
        if (combo != null)
            combo.dispose();
        if (viewportModel != null) {
            viewportModel.removeViewportModelListener(listener);
            viewportModel = null;
        }
    }

    /**
     * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void fill( Composite c ) {
        Label separator = new Label(c, SWT.SEPARATOR);
        StatusLineLayoutData data = new StatusLineLayoutData();
        separator.setLayoutData(data);
        data.widthHint = 1;
        data.heightHint = STATUS_LINE_HEIGHT;
        
        combo = new Combo(c, SWT.BORDER|SWT.CENTER);
        
        combo.addKeyListener(this);
        combo.addFocusListener(this);
        combo.addListener(SWT.MouseDown, new Listener(){
           public void handleEvent(Event e){
               if( combo.getText().contains(":") ) //$NON-NLS-1$
                   formatForEditing();
           }
        });
        combo.addSelectionListener( new SelectionListener(){            
            public void widgetSelected( SelectionEvent e ) {
                if( combo.getText().contains(":") ) //$NON-NLS-1$
                    formatForEditing();
                go();
            }
            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });
        data = new StatusLineLayoutData();
        combo.setLayoutData(data);
        updateScale();
        data.widthHint = 80;
        data.heightHint = STATUS_LINE_HEIGHT;
        this.mapPart.setFont(combo);
        
    }

    public void keyPressed(KeyEvent e) {
        if( combo.getText().contains(":") ) //$NON-NLS-1$
            formatForEditing();
        if( !isLegalKey(e) ){
            e.doit=false;
        }
    }
    
    public boolean isLegalKey(KeyEvent e){
        char c=e.character;
        
        if( c == '0' ||
                c == '1' ||
                c == '2' ||
                c == '3' ||
                c == '4' ||
                c == '5' ||
                c == '6' ||
                c == '7' ||
                c == '8' ||
                c == '9' ||
                c == SWT.DEL ||
                c == SWT.BS ){
            return true;
        }
        
        if( e.keyCode == SWT.ARROW_LEFT ||
                e.keyCode == SWT.ARROW_RIGHT ||
                e.keyCode == SWT.HOME ||
                e.keyCode == SWT.END ||
                e.keyCode == SWT.OK)
            return true;
        
        return false;
            
    }

    public void keyReleased(KeyEvent e) {
        if (e.character == SWT.Selection) {
            go();
        } else if (e.character == SWT.ESC) {
            updateScale();
        }
        
    }

    private void go() {
        String newScale=combo.getText().trim();
        try{
        	double d = nf.parse(newScale.replace(" ","")).doubleValue();
            SetScaleCommand command=new SetScaleCommand(d);
            this.mapPart.getMap().sendCommandASync(command);
        }catch(Exception e){
            org.eclipse.swt.graphics.Rectangle start=ZoomingDialog.calculateBounds(combo);
            
            ZoomingDialog.openErrorMessage(start, this.mapPart
					.getMapEditorSite().getShell(),
					Messages.MapEditor_illegalScaleTitle,
					Messages.MapEditor_illegalScaleMessage);
        }
    }

    public void focusGained(FocusEvent e) {
        formatForEditing();
    }

    private void formatForEditing(){
        String text=combo.getText();
        if( text.contains(":")) //$NON-NLS-1$
            text=text.substring(2);
        StringBuilder builder=new StringBuilder();
        for( int i=0; i<text.length(); i++ ){
            char c=text.charAt(i);
            if( c!=',' )
                builder.append(c);
        }
        combo.setText(builder.toString());
        int end = combo.getText().length();
        combo.setSelection( new Point(0, end));
    }
    
    public void focusLost(FocusEvent e) {
        updateScale();
    }
    
    String toLabel( double scaleDenominator ){
        return "1:" + nf.format( scaleDenominator );
    }
    
    private void updateScale() {
        if (combo == null || combo.isDisposed())
            return;

        if (viewportModel != null) {
            combo.removeAll();
            for( double scaleDenominator : viewportModel.getPreferredScaleDenominators() ){
                String item = toLabel( scaleDenominator );
                combo.add( item );
            }
            combo.setText( toLabel(viewportModel.getScaleDenominator())); //$NON-NLS-1$
            combo.setToolTipText(combo.getText());
        } else {
            combo.setText(""); //$NON-NLS-1$
        }
    }

}
