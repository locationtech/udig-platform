/**
 * 
 */
package org.locationtech.udig.project.ui.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.ITransientResolve;
import org.locationtech.udig.catalog.ui.export.CatalogExport;
import org.locationtech.udig.catalog.ui.export.ExportResourceSelectionState;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.EditManager;

class SaveMapRunnable implements Runnable{
    	private final MapEditorPart mapEditor;
    	private final boolean[] success;
    	

		public SaveMapRunnable(MapEditorPart mapEditor, boolean[] success) {
			super();
			this.mapEditor = mapEditor;
			this.success = success;
		}
    	
    	private enum Result{NO_TEMP_LAYERS, EXPORT_WIZARD_RUNNING};
            public void run() {
                try{
                	
                    EditManager editManagerInternal = mapEditor.getMap().getEditManagerInternal();
					
                    SaveMapRunnable.Result result = saveTemporaryLayers();
                    if( result == Result.NO_TEMP_LAYERS ){
    					editManagerInternal.commitTransaction();

    					success[0] = true;
                    } else {
                    	success[0] = false;
                    }
                }catch (IOException e) {
                    ProjectUIPlugin.log("Error saving", e); //$NON-NLS-1$
                    success[0] = false;
                }
            }

            private SaveMapRunnable.Result saveTemporaryLayers( ) {
                List<IGeoResource> resources=new ArrayList<IGeoResource>();
                for( ILayer layer : mapEditor.getMap().getMapLayers() ) {
                    if( layer.hasResource(ITransientResolve.class) )
                        resources.addAll(layer.getGeoResources());
                }
                
                if( resources.isEmpty() ){
                    return Result.NO_TEMP_LAYERS;
                }
                
                final StructuredSelection selection = new StructuredSelection(resources);
                final ExportResourceSelectionState layerState = new ExportResourceSelectionState(selection);
                
                CatalogExport exp = new MapSaveStrategy(layerState, mapEditor);

                // open the export dialog
                exp.open();
                 
                // Since dialog is opened after this returns return cancelled and we'll make sure the MapSaveStrategy will clear the
                // dirty state if the export takes place.
                
                return Result.EXPORT_WIZARD_RUNNING;
            }

    }
