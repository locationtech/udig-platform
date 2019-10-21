/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.internal.Messages;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.geotools.data.DataStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import org.locationtech.jts.geom.Geometry;

/**
 * Opens a dialog that allows a SimpleFeatureType to be defined.
 * 
 * @author Jesse
 * @author Andrea Antonello (www.hydrologis.com)
 * @since 1.1.0
 */
public class FeatureTypeEditorDialog extends Dialog {
    final FeatureTypeEditor editor;
    private SimpleFeatureType defaultFeatureType;
    private DataStore dataStore;
    private SimpleFeatureType result;
    private ValidateFeatureType validateFeatureType;

    public FeatureTypeEditorDialog( Shell parentShell, ValidateFeatureType strategy ) {
        super(parentShell);
        editor = new FeatureTypeEditor();
        defaultFeatureType=editor.createDefaultFeatureType(); 
        this.validateFeatureType=strategy;
        setShellStyle(SWT.RESIZE|SWT.DIALOG_TRIM|SWT.CLOSE);
    }
    
    @Override
    protected Control createDialogArea( Composite parent ) {
        getShell().setText(Messages.FeatureTypeEditorDialog_ShellTitle);
        Composite composite=new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(8, false);
        gridLayout.marginWidth=0;
        gridLayout.marginHeight=0;
        
        composite.setLayout(gridLayout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        editor.createFeatureTypeNameText(composite, new GridData(SWT.FILL, SWT.FILL, true,false, 8,1));
        
        Composite buttons=new Composite(composite, SWT.NONE);
        gridLayout = new GridLayout();
        gridLayout.marginWidth=0;
        gridLayout.marginHeight=0;
        gridLayout.horizontalSpacing=0;
        gridLayout.verticalSpacing=0;
        buttons.setLayout(gridLayout);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, false,true, 1,1);
        gridData.widthHint=32;
        buttons.setLayoutData(gridData);
        createButton(buttons, editor.getCreateAttributeAction());
        createButton(buttons, editor.getDeleteAction());
        
        editor.createTable(composite, new GridData(SWT.FILL, SWT.FILL, true,true, 7,1),
                defaultFeatureType, true);
        editor.createContextMenu();
        
        return composite;
    }

    private void createButton( Composite composite, final IAction action ) {
        final Button button = new Button(composite, SWT.PUSH | SWT.FLAT);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
        button.setLayoutData(data);
        button.setToolTipText(action.getToolTipText());

        ImageRegistry images = UiPlugin.getDefault().getImageRegistry();
        Image image = images.get(action.getId());
        if (image == null || image.isDisposed()) {
            images.put(action.getId(), action.getImageDescriptor());
            image = images.get(action.getId());
        }
        button.setImage(image);

        button.addListener(SWT.Selection, new Listener(){
            public void handleEvent( Event event ) {
                action.runWithEvent(event);
            }
        });
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 500);
    }
    
    @Override
    public boolean close() {
        result = editor.getFeatureType();
        editor.getControl().setFocus();
        return super.close();
    }
    
    public void setDataStore(DataStore dataStore){
        this.dataStore=dataStore;
        if( dataStore instanceof ShapefileDataStore ){
            List<LegalAttributeTypes> list=new ArrayList<LegalAttributeTypes>(editor.getLegalTypes());
            for( Iterator<LegalAttributeTypes> iter=list.iterator(); iter.hasNext(); ){
                LegalAttributeTypes type=iter.next();
                if( type.getType()==Geometry.class ){
                    iter.remove();
                }
            }
            editor.setLegalTypes(list);
        }
    }
    
    @Override
    protected void okPressed() {
        String errorMessage = validateFeatureType.validate(editor.getFeatureType());
        if( errorMessage != null ){
        	editor.setErrorMessage(errorMessage);
        }else{
        	editor.builderChanged();
            super.okPressed();
        }
    }
    
	public FeatureTypeEditor getEditor() {
        return editor;
    }

    @Override
    public int open() {
        result=null;
        return super.open();
    }
    
    /**
     * Returns the feature type defined by user or null if it is not a legal feature type for the setDataStore.  
     * 
     * <p>If setDataStore has previously been called then the feature typename
     * will be checked to determine if the typename already exists.  
     * If it does then null is returned and the dialog should be opened a 
     * second time.</p>
     * 
     * @param checkForDuplicateFeatureType If true null will be returned if 
     *                  the datastore has a feature type with the same
     *                  feature type name.
     * @return the feature type defined by user
     */
    public SimpleFeatureType getFeatureType(boolean checkForDuplicateFeatureType) {
        if (result!=null) {
            try {
                if (!checkForDuplicateFeatureType || isFeatureTypeOK()) {
                    return result;
                }
            } catch (SchemaException e) {
                UiPlugin.log("Error creating feature type", e); //$NON-NLS-1$
            }
        }
        return null;

    }
    
    private boolean isFeatureTypeOK( ) throws SchemaException {
        if( dataStore==null )
            return true;
        try {
            // verify that the typename does not already exist. if it doesn't
            // getSchema throws an exception
            dataStore.getSchema(defaultFeatureType.getName());
            
            /*
             * FIXME not sure if it is enough to recreate the featureType, or if
             * somewhere the reference to the old object is needed. 
             */
            SimpleFeatureTypeBuilder ftB = new SimpleFeatureTypeBuilder();
            ftB.setName(Messages.NewFeatureTypeOp_duplicateTypeName);
            ftB.init(defaultFeatureType);
            defaultFeatureType = ftB.buildFeatureType();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * Validates the feature type to determine whether it is acceptable and can be created.
     * 
     * <p>This is used to determine if the dialog can close.</p>
     * 
     * @author Jesse
     * @author Andrea Antonello (www.hydrologis.com)
     * @since 1.2
     */
    public interface ValidateFeatureType {
        /**
         * Returns true if the feature type builder is ok and the dialog may close. 
         * 
         * <p>Changes to the builder will be reflected in the dialog.</p>
         *
         * @param featureType the {@link FeatureType} to validate.
         * @return  null if the feature type is ok and the dialog may close, otherwise an error message
         */
        String validate(SimpleFeatureType featureType);
    }

	public SimpleFeatureTypeBuilder getDefaultBuilder() {
	    SimpleFeatureTypeBuilder ftB = new SimpleFeatureTypeBuilder();
	    ftB.setName(defaultFeatureType.getName());
	    ftB.init(defaultFeatureType);
		return  ftB;
	}

	public void setDefaultFeatureType(SimpleFeatureType defaultFeatureType) {
		this.defaultFeatureType = defaultFeatureType;
	}
}
