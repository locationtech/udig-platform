/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.locationtech.udig.project.internal.SetDefaultStyleProcessor;
import org.locationtech.udig.style.internal.StyleLayer;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.style.sld.internal.Messages;
import org.locationtech.udig.ui.graphics.SLDs;

/**
 * Listen to the style editor workflow.
 * 
 * @since 1.1.0
 */
class StyleEditorButtonListener implements Listener {

    /** StyleEditorButtonListener styleEditorDialog field */
    private final StyleEditorDialog styleEditorDialog;

    /**
     * @param styleEditorDialog
     */
    StyleEditorButtonListener( StyleEditorDialog styleEditorDialog ) {
        this.styleEditorDialog = styleEditorDialog;
    }

    /**
     * Will dispatch the even to the correct method (doApply, doRevert, etc...).
     */
    public void handleEvent( Event event ) {
        
        int buttonId = (Integer) event.widget.getData();
        
        switch( buttonId ) {
        case StyleEditorDialog.IMPORT_ID:
            doImport();
            break;
        case StyleEditorDialog.EXPORT_ID:
            doExport();
            break;
        case StyleEditorDialog.DEFAULTS_ID:
            doDefaults();
            break;
        case StyleEditorDialog.APPLY_ID:
            doApply();
            break;
        case StyleEditorDialog.REVERT_ID:
            doRevert();
            break;
        case StyleEditorDialog.OK_ID:
            if( doApply() ){
                this.styleEditorDialog.close();
            }
            break;
        case StyleEditorDialog.CANCEL_ID:
            this.styleEditorDialog.close();
            break;

        default:
            break;
        }
        
    }

    private boolean doApply() {
        if( this.styleEditorDialog.getCurrentPage() == null){
            return false;
        }
        if (this.styleEditorDialog.getCurrentPage().performApply()) {
            this.styleEditorDialog.setExitButtonState();
            this.styleEditorDialog.selectedLayer.apply();
            return true;
        }
        return false;
    }
    private void doDefaults() {
            StyleLayer layer = styleEditorDialog.selectedLayer;
            layer.getStyleBlackboard().clear();
            SetDefaultStyleProcessor p = new SetDefaultStyleProcessor(layer.getGeoResource(), layer);
            p.run();
            Style style = (Style) layer.getStyleBlackboard().get(SLDContent.ID);
            StyledLayerDescriptor oldSLD=null;
            if(style!=null){
                oldSLD = this.styleEditorDialog.getSLD();
            }
            this.styleEditorDialog.selectedLayer.apply();
            this.styleEditorDialog.selectedLayer.getMap().getRenderManager().refresh(this.styleEditorDialog.selectedLayer, null);
            
            if( oldSLD!=null ){
                StyledLayerDescriptor newSLD = this.styleEditorDialog.getSLD();
            }
            this.styleEditorDialog.setExitButtonState();
            this.styleEditorDialog.getCurrentPage().refresh();
    }
    private void doRevert() {
        //store the old sld
        //StyledLayerDescriptor oldSLD = this.styleEditorDialog.getSLD();
        
        //return to the blackboard state before we loaded the dialog
        this.styleEditorDialog.selectedLayer.revertAll();
        this.styleEditorDialog.selectedLayer.apply();
        this.styleEditorDialog.selectedLayer.getMap().getRenderManager().refresh(this.styleEditorDialog.selectedLayer, null);
        
        //move listeners to new sld
        //StyledLayerDescriptor newSLD = this.styleEditorDialog.getSLD();
        this.styleEditorDialog.setExitButtonState();
        
        // TODO: update button states, page updates
        this.styleEditorDialog.getCurrentPage().refresh();
    }
    
    private void doImport() {
        ImportSLD importe = new ImportSLD();
        StyledLayerDescriptor sld = null;
        File file = importe.promptFile(Display.getDefault(), sld);
        if (file != null) {
            try {
                sld = (StyledLayerDescriptor) importe.importFrom(file, null);
            } catch (Exception e1) {
                MessageBox mb = new MessageBox(this.styleEditorDialog.getShell(), SWT.ICON_ERROR | SWT.OK);
                mb.setMessage(MessageFormat.format(Messages.StyleEditor_import_failed, e1.getLocalizedMessage())); 
                mb.open();
                throw (RuntimeException) new RuntimeException().initCause(e1);
            }
        }
        if (sld != null) {
            Style newStyle = SLDs.getDefaultStyle(sld);
            // TODO: assert there is only 1 style
            this.styleEditorDialog.setStyle(newStyle);
            //refresh the page (there's a new SLD in town)
            this.styleEditorDialog.getCurrentPage().refresh();
        }
    }
    
    private void doExport() {
        StyledLayerDescriptor sld = this.styleEditorDialog.getSLD();
        ExportSLD export = new ExportSLD();
        File file = export.promptFile(Display.getDefault(), sld);
        if (file != null) {
            try {
                export.exportTo(sld, file, null);
            } catch (Exception e1) {
                // TODO Handle Exception
                throw (RuntimeException) new RuntimeException().initCause(e1);
            }
        }
    }
    
}
