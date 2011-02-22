/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.SchemaException;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Opens a dialog that allows a FeatureType to be defined.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class FeatureTypeEditorDialog extends Dialog {
    final FeatureTypeEditor editor;
    private FeatureTypeBuilder defaultBuilder;
    private DataStore dataStore;
    private FeatureTypeBuilder result;
    private ValidateFeatureTypeBuilder validateFeatureType;

    public FeatureTypeEditorDialog( Shell parentShell, ValidateFeatureTypeBuilder strategy ) {
        super(parentShell);
        editor = new FeatureTypeEditor();
        defaultBuilder=editor.createDefaultFeatureType();
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
                defaultBuilder, true);
        editor.createContextMenu();

        return composite;
    }

    private void createButton( Composite composite, final IAction action ) {
        final Button button=new Button(composite, SWT.PUSH|SWT.FLAT);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
        button.setLayoutData(data);
        button.setToolTipText(action.getToolTipText());

        ImageRegistry images = UiPlugin.getDefault().getImageRegistry();
        Image image=images.get(action.getId());
      if( image==null || image.isDisposed() ){
          images.put(action.getId(),
                  action.getImageDescriptor());
          image=images.get(action.getId());
      }
        button.setImage(image);

//        button.addPaintListener(new PaintListener(){
//            ImageRegistry images = UiPlugin.getDefault().getImageRegistry();
//            public void paintControl( PaintEvent e ) {
//                Image image=images.get(action.getId());
//                if( image==null || image.isDisposed() ){
//                    images.put(action.getId(),
//                            action.getImageDescriptor());
//                    image=images.get(action.getId());
//                }
//
//                Point size = button.computeSize(SWT.DEFAULT,SWT.DEFAULT);
//                Rectangle imageBounds = image.getBounds();
//                e.gc.drawImage(image,0,(size.y-2-imageBounds.height)/2);
//            }
//        });

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
        result=editor.getFeatureTypeBuilder();
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
        boolean ok=validateFeatureType.validate(editor.getFeatureTypeBuilder());
        editor.builderChanged();

        if( ok )
            super.okPressed();
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
     * If setDataStore has previously been called then the feature typename will be checked to determine if the
     * typename already exists.  If it does then null is returned and the dialog should be opened a second time.
     * @param checkForDuplicateFeatureType If true null will be returned if the datastore has a feature type with the same
     * feature type name.
     *
     * @return
     */
    public FeatureType getFeatureType(boolean checkForDuplicateFeatureType) {
        if (result!=null) {
            try {
                if (!checkForDuplicateFeatureType || isFeatureTypeOK()) {
                    return result.getFeatureType();
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
            dataStore.getSchema(defaultBuilder.getName());
            defaultBuilder
                    .setName(Messages.NewFeatureTypeOp_duplicateTypeName);
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * Validates the feature type to determine whether it is acceptable and can be created.  This is used to determine if the
     * dialog can close.
     *
     * @author Jesse
     * @since 1.1.0
     */
    public interface ValidateFeatureTypeBuilder {
        /**
         * Returns true if the feature type builder is ok and the dialog may close.  Changes to the builder will be reflected in
         * the dialog.
         *
         * @param featureBuilder builder to validate.
         * @return  true if the feature type builder is ok and the dialog may close
         */
        boolean validate(FeatureTypeBuilder builder);
    }

	public FeatureTypeBuilder getDefaultBuilder() {
		return defaultBuilder;
	}

	public void setDefaultBuilder(FeatureTypeBuilder defaultBuilder) {
		this.defaultBuilder = defaultBuilder;
	}
}
