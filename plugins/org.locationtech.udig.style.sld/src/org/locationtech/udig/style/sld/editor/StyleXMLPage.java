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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.xml.styling.SLDParser;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.style.internal.StyleLayer;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.style.sld.internal.Messages;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.coverage.grid.GridCoverage;

/**
 * This is the "advanced" page that shows the raw SLD file.
 */
public class StyleXMLPage extends StyleEditorPage {

    SashForm sash;
    Text sldTextBox;
    Text errTextBox;
    
    boolean dirty = false;
        
    @Override
    public void createPageContent( Composite parent ) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        comp.setLayoutData(gd);
        comp.setLayout(new GridLayout(3, false));

        sash = new SashForm(comp, SWT.VERTICAL); 
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = parent.getBounds().x;
        gd.heightHint = parent.getBounds().y;
        gd.horizontalSpan = 3;
        sash.setLayoutData(gd);

        sldTextBox = new Text(sash, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        sldTextBox.setEditable(true);
        sldTextBox.addModifyListener(new ModifyListener() {
            
            public void modifyText( ModifyEvent e ) {
                dirty = true;
            }
            
        });
        
        sldTextBox.addFocusListener(new FocusListener() {
            public void focusGained( FocusEvent e ) {
            }

            public void focusLost( FocusEvent e ) {
//                if (isValid())
//                    updateSLD();
            }
        });
        
        errTextBox = new Text(sash, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        errTextBox.setVisible(false);
        errTextBox.setEditable(false);

//TODO: add code for style reset        
//        Button resetButton = new Button(comp, SWT.RIGHT);
//        resetButton.setText("Reset");
//        gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
//        resetButton.setLayoutData(gd);
//        resetButton.addSelectionListener(new SelectionListener() {
//
//            public void widgetSelected( SelectionEvent e ) {
//                
//            }
//
//            public void widgetDefaultSelected( SelectionEvent e ) {
//                widgetSelected(e);
//            }
//            
//        });

        styleChanged(null);
    }

    @Override
    public boolean performCancel() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    private void resetCursor(Cursor c) {
        setDisplayCursor(null);
        c.dispose();
        c = null;
    }
    
    /**
     * Sets the given cursor for all shells currently active
     * for this window's display.
     *
     * @param c the cursor
     */
    private void setDisplayCursor(Cursor c) {
        Shell[] shells = Display.getCurrent().getShells();
        for (int i = 0; i < shells.length; i++)
            shells[i].setCursor(c);
    }
    
    @Override
    public boolean isValid() {
        return true;
    }

    public boolean okToLeave() {
        boolean readyToLeave = true;
        if (dirty) { //!dirty was a condition -- not required for the moment  
            readyToLeave = updateSLD();
        }
        if (readyToLeave) {
            return true;
        } else {
            //inform the user that the SLD is invalid -- fix it or lose it
            return MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                Messages.StyleEditor_xml_lose_changes_1, 
                Messages.StyleEditor_xml_lose_changes_2); 
        }
    }

    public boolean performOk() {
        return true;
    }
    
    private String styleToXML() {
        return StyleEditor.styleToXML( getSLD() );
    }
    
    private StyledLayerDescriptor XMLtoSLD(String xml) {
        return XMLtoSLD(xml, "UTF-8"); //$NON-NLS-1$
    }
    
    private StyledLayerDescriptor XMLtoSLD(String xml, String encoding) {
        //save changes to style object
        StyleFactory factory = CommonFactoryFinder.getStyleFactory();
        InputStream is = getXMLasInputStream(xml, encoding);
        if (is == null) return null;
        SLDParser stylereader = new SLDParser(factory, is);
        StyledLayerDescriptor sld = stylereader.parseSLD();
        return sld;
    }
    
    private InputStream getXMLasInputStream(String xml, String encoding) {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(xml.getBytes(encoding));
        } catch (UnsupportedEncodingException e1) {
            // TODO Handle UnsupportedEncodingException
            throw (RuntimeException) new RuntimeException( ).initCause( e1 );
        }
        return is;
    }
    
    public void gotFocus() {
        refresh();
        dirty = false;
    };
    
    @Override
    public void styleChanged( Object source ) {
        dirty = true;
    }
 
    public void refresh() {
        String xmlOrig = sldTextBox.getText();
        String xml = styleToXML();
        if (xml != null && !xml.equals(xmlOrig)) {
            sldTextBox.setText(xml);
            dirty = true;
        }
    }
    
    private boolean updateSLD() {
        //busy cursor
        Cursor waitCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
        setDisplayCursor(waitCursor);

        //generate the SLD
        StyledLayerDescriptor sld = null;
        Style style = null;
        String xml = sldTextBox.getText();
        if (xml == null) {
            resetCursor(waitCursor);
            return false;
        }
        try {
            sld = XMLtoSLD(xml);
            style = SLDs.getDefaultStyle(sld);
        } catch (Exception e) {
            
            boolean result = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    Messages.StyleEditor_xml_failure_1, 
                    Messages.StyleEditor_xml_failure_2);
            
            resetCursor(waitCursor);
            
            if( result ){
                try {
                    StyleLayer layer = getContainer().getSelectedLayer();
                    IGeoResource resource = layer.findGeoResource(FeatureSource.class);
                    if( resource!=null ){
                        style = (Style) new SLDContent().createDefaultStyle(resource, layer.getDefaultColor(), null);
                    }else{
                        resource = layer.findGeoResource(GridCoverage.class);
                        if( resource!=null ){
                            style = (Style) new SLDContent().createDefaultStyle(resource, layer.getDefaultColor(), null);
                        }
                    }
                    if( style!=null ){
                        sld = SLDContent.createDefaultStyledLayerDescriptor(style);
                        setStyle(style);
                        refresh();
                    }
                } catch (IOException e1) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e1 );
                }
            } else return false; // abort
            
        }
        //update the style / SLD
        if (sld != null && style != null) {
            setStyle(style);
            resetCursor(waitCursor);
            return true;
        }
        resetCursor(waitCursor);
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public String getLabel() {
        return null;
    }

    public boolean performApply() {
        return updateSLD();
    }
    
}
