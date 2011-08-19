/**
 * 
 */
package net.refractions.udig.project.ui.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.ITransientResolve;
import net.refractions.udig.catalog.ui.export.CatalogExport;
import net.refractions.udig.catalog.ui.export.ExportResourceSelectionState;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.EditManager;

import org.eclipse.jface.viewers.StructuredSelection;

/**
 * Save strategy for Map Palette
 * 
 * @author Jody Garnett
 * @since 1.3.0
 * @version 1.3.0
 */
class SaveMapPaletteRunnable implements Runnable{
    	private final MapEditorWithPalette mapEditor;
    	private final boolean[] success;
    	

		public SaveMapPaletteRunnable(MapEditorWithPalette mapEditor, boolean[] success) {
			super();
			this.mapEditor = mapEditor;
			this.success = success;
		}
    	
    	private enum Result{NO_TEMP_LAYERS, EXPORT_WIZARD_RUNNING};
            public void run() {
                try{
                	
                    EditManager editManagerInternal = mapEditor.getMap().getEditManagerInternal();
					
                    SaveMapPaletteRunnable.Result result = saveTemporaryLayers();
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

            private SaveMapPaletteRunnable.Result saveTemporaryLayers( ) {
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
                
                CatalogExport exp = new MapPaletteSaveStrategy(layerState, mapEditor);

                // open the export dialog
                exp.open();
                 
                // Since dialog is opened after this returns return cancelled and we'll make sure the MapSaveStrategy will clear the
                // dirty state if the export takes place.
                
                return Result.EXPORT_WIZARD_RUNNING;
            }

    }