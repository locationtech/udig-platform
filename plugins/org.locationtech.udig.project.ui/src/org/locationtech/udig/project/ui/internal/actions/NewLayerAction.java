/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.actions;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.geotools.data.FeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.core.AdapterUtil;
import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.ui.FeatureTypeEditor;
import org.locationtech.udig.ui.FeatureTypeEditorDialog;
import org.locationtech.udig.ui.FeatureTypeEditorDialog.ValidateFeatureType;
import org.locationtech.udig.ui.ProgressManager;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;

/**
 * Allows a SimpleFeatureType to be created and adds the feature type to the current map (or creates a new map if there is no current map)
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class NewLayerAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {
    private IGeoResource resource=null;

    private final ValidateFeatureType performOK=new ValidateFeatureType(){

        public String validate( SimpleFeatureType featureType ) {
            try {
                resource = CatalogPlugin.getDefault().getLocalCatalog().
                    createTemporaryResource(featureType);
                return null;
            } catch (Exception e) {
                return Messages.NewLayerAction_duplicateName;
            }            
        }
        
    };

	private IWorkbenchWindow window;

    @Override
    public void runWithEvent( IAction action, Event event ) {
        // Open a dialog for user to create featuretype
        FeatureTypeEditorDialog dialog = new FeatureTypeEditorDialog(event.display.getActiveShell(), performOK);
        setDefaultGeomType(dialog);

        dialog.setBlockOnOpen(true);
        int code = dialog.open();

        if (code == Window.CANCEL)
            return;

        if (resource != null) {
            IMap map = ApplicationGIS.getActiveMap();
            int index = 0;
            if (map != ApplicationGIS.NO_MAP) {
                index = map.getMapLayers().size();
            }
            ApplicationGIS.addLayersToMap(map, Collections.singletonList(resource), index);
        }

    }

	/**
	 * @param dialog
	 */
	private void setDefaultGeomType(FeatureTypeEditorDialog dialog) {
		if( window.getSelectionService()==null ){
			return;
		}
		
		ISelection selection = window.getSelectionService().getSelection();
		
		GeometryDescriptor geom = findGeometryType(selection);
		
		if( geom==null ){
		    IMap map = ApplicationGIS.getActiveMap();
		    if( map==ApplicationGIS.NO_MAP ){
		        return;
		    }
		    
		    IEditManager editManager = map.getEditManager();
		    if( editManager == null ){
		        return;
		    }
            ILayer selectedLayer = editManager.getSelectedLayer();
            if( selectedLayer == null ){
                return;
            }
            SimpleFeatureType schema = selectedLayer.getSchema();
		    if( schema ==null ){
		        return;
		    }
		    geom = schema.getGeometryDescriptor();		    
		}		
		FeatureTypeEditor editor = dialog.getEditor();
        SimpleFeatureType ft = editor.createDefaultFeatureType();
        SimpleFeatureTypeBuilder builder = editor.builderFromFeatureType(ft);
        String defaultGeometry = ft.getGeometryDescriptor().getLocalName();
        if( defaultGeometry == null ){
            return;
        }
        builder.remove(defaultGeometry);
        builder.add(geom);
        builder.setDefaultGeometry(geom.getLocalName());
        dialog.setDefaultFeatureType(builder.buildFeatureType());
	}

    @SuppressWarnings("unchecked")
	private GeometryDescriptor findGeometryType(ISelection selection) {

        if (selection == null || selection.isEmpty()) {
            return null;
        }
    	if (selection instanceof IStructuredSelection) {
			IStructuredSelection structured = (IStructuredSelection) selection;
			Iterator iter = structured.iterator();
			while(iter.hasNext()){
				Object elem = iter.next();
				try {
					FeatureSource<SimpleFeatureType, SimpleFeature> source = AdapterUtil.instance.adaptTo(FeatureSource.class, elem, ProgressManager.instance().get());
					if( source != null ){
						return source.getSchema().getGeometryDescriptor();
					}
					if (elem instanceof IMap) {
						IMap map = (IMap) elem;
						ILayer layer = map.getEditManager().getSelectedLayer();
						if( layer!=null ){
							source = AdapterUtil.instance.adaptTo(FeatureSource.class, layer, ProgressManager.instance().get());
							if( source != null ){
								return source.getSchema().getGeometryDescriptor();
							}
						}
					}
				} catch (IOException e) {
					// continue trying
				}
			}
		}
    	return null;
	}

	public void init( IWorkbenchWindow window ) {
    	this.window = window;
    }

}
