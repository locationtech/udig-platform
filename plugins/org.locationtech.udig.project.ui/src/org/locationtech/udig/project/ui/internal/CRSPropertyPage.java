/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.command.navigation.SetViewportBBoxCommand;
import org.locationtech.udig.project.internal.commands.ChangeCRSCommand;
import org.locationtech.udig.project.ui.internal.commands.SetLayerCRSCommand;
import org.locationtech.udig.ui.CRSChooser;
import org.locationtech.udig.ui.Controller;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A user interface that allow a user to modify/create and set the current and default
 * CoordinateReferenceSystems used for maps.
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Allow creation of user defined CRSs</li>
 * <li>Allow modification of the current CRS</li>
 * <li>Allow the CRS of a map to be set</li>
 * <li>Allow the default CRS of a workbench to be set</li>
 * </ul>
 * </p>
 * 
 * @author jeichar
 * @since 0.3
 */
public class CRSPropertyPage extends PropertyPage {

    public static class MapStrategy implements ApplyCRSStrategy {

        Map map;
        public MapStrategy( Map map ) {
            this.map = map;
        }

        public void applyCoordinateReferenceSystem( CoordinateReferenceSystem crs ) {
            map.sendCommandASync(new ChangeCRSCommand(crs));
        }
        public CoordinateReferenceSystem getCurrentCoordinateReferenceSystem() {
            return map.getViewportModel().getCRS();
        }
    }
    
    public static class LayerStrategy implements ApplyCRSStrategy {
        private static final String SHOW_DIALOG_KEY="SHOW_DIALOG_WHEN_UPDATE_LAYER_CRS_KEY"; //$NON-NLS-1$
        private static final String UPDATE_MAP_CRS_KEY="UPDATE_MAP_CRS_WHEN_UNKOWN_IS_CHANGED"; //$NON-NLS-1$
        Layer layer;
        public LayerStrategy( Layer layer ) {
            this.layer = layer;
        }

        public void applyCoordinateReferenceSystem( CoordinateReferenceSystem crs ) {
            UndoableComposite commands=new UndoableComposite();
            List<MapCommand> commandList = commands.getCommands();
            commandList.add(new SetLayerCRSCommand(layer, crs));
            if( layer.getCRS()==ILayer.UNKNOWN_CRS && 
                    layer.getMap().getViewportModel().getCRS().equals(ILayer.UNKNOWN_CRS) ){
                IPreferenceStore store=ProjectUIPlugin.getDefault().getPreferenceStore();
                store.setDefault(SHOW_DIALOG_KEY, true);
                boolean openDialog=store.getBoolean(SHOW_DIALOG_KEY);
                boolean updateMapCRS;
                if( openDialog ){
                    Shell shell=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                    MessageDialogWithToggle dialog=MessageDialogWithToggle.openYesNoQuestion(shell, 
                            Messages.CRSPropertyPage_title, 
                            Messages.CRSPropertyPage_message, 
                            Messages.CRSPropertyPage_toggle_message, 
                            false, 
                            store, 
                            SHOW_DIALOG_KEY );
                    int returnCode=dialog.getReturnCode();
                    updateMapCRS=returnCode==IDialogConstants.YES_ID?true:false;
                    store.setValue(UPDATE_MAP_CRS_KEY, updateMapCRS);
                }else{
                    updateMapCRS=store.getBoolean(UPDATE_MAP_CRS_KEY);
                }
                if( updateMapCRS ){
                    commandList.add(new ChangeCRSCommand(crs));
                    commandList.add(new SetViewportBBoxCommand(layer.getBounds(new NullProgressMonitor(), crs)));
                }
            }
            layer.getMap().sendCommandASync(commands);
        }
        public CoordinateReferenceSystem getCurrentCoordinateReferenceSystem() {
            return layer.getCRS();
        }
    }

    /**
     * In order to make CRSPropertyPage more re-usable this strategy is used to apply the CRS to the
     * object that this page applies to. In addition it retrieves the current CRS from the object.
     * 
     * @author Jesse
     * @since 1.1.0
     */
    public static interface ApplyCRSStrategy {
        /**
         * Called when a new CRS has been chosen. Should set the crs on the object.
         * 
         * @param crs new CRS. Will not be the same as returned by
         *        {@link #getCurrentCoordinateReferenceSystem()}
         */
        public void applyCoordinateReferenceSystem( CoordinateReferenceSystem crs );
        /**
         * Returns the current CRS. Should not return null.
         * 
         * @return current crs of object.
         */
        public CoordinateReferenceSystem getCurrentCoordinateReferenceSystem();
    }

    CRSChooser chooser = new CRSChooser(new Controller(){

        public void handleClose() {
            getControl().getShell().close();
        }

        public void handleOk() {
            CRSPropertyPage.this.performOk();
        }
        
    });

    private ApplyCRSStrategy strategy;

    public CRSPropertyPage( ) {
        setTitle(Messages.CRSPropertyPage_coordinateSystems_title); 
    }
    
    public void setStrategy( ApplyCRSStrategy strategy){
        this.strategy=strategy;
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        CoordinateReferenceSystem crs = chooser.getCRS();
        if (crs == null)
            return false;

        if (crs.equals(strategy.getCurrentCoordinateReferenceSystem()))
            return true;

        strategy.applyCoordinateReferenceSystem(crs);
        // BasicCommandFactory factory = BasicCommandFactory.getInstance();
        // MapCommand command = factory.createChangeCRS(map, crs);
        // map.sendCommandSync(command);

        return super.performOk();
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        chooser.clearSearch();
        chooser.gotoCRS(strategy.getCurrentCoordinateReferenceSystem());
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
     */
    public IPreferenceStore getPreferenceStore() {
        return ProjectPlugin.getPlugin().getPreferenceStore();
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     * @param parent
     */
    protected Control createContents( Composite parent ) {
        CoordinateReferenceSystem crs = null;
        Control control;

        crs = strategy.getCurrentCoordinateReferenceSystem();
        if (crs == null)
            control = chooser.createControl(parent);
        else
            control = chooser.createControl(parent, crs);
        chooser.setFocus();
        return control;
    }
    
    /**
     * @see org.eclipse.jface.preference.PreferencePage#doComputeSize()
     */
    protected Point doComputeSize() {
        return new Point(200, 300);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init( IWorkbench workbench ) {
    }

    
}
