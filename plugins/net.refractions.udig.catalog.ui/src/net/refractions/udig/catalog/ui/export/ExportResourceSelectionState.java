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
package net.refractions.udig.catalog.ui.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.core.AdapterUtil;
import net.refractions.udig.core.Pair;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

public class ExportResourceSelectionState extends State {

    List<Data> layers;
    List<Data> selectedLayers;
    ISelection selection = null;
	private String exportDir = Platform.getLocation().toOSString();
    
    /**
     * The selection to save.  Expected value is a map or layers
     * 
     * @param selection
     */
    public ExportResourceSelectionState( ISelection selection ) {
        this.selection=selection;
    }

    public ExportResourceSelectionState() {
        this(null);
    }
    
    public List<Data> getLayers() {
        return layers;
    }
    
    public List<Data> getExportData() {
        return selectedLayers;
    }

    public void setSelectedLayers(List<Data> selectedLayers) {
        this.selectedLayers = selectedLayers; 
    }
    
    @Override
    public void init( IProgressMonitor monitor ) throws IOException {
        super.init(monitor);

        layers = new ArrayList<Data>();
        
        Object items[] = new Object[0];
        if (selection instanceof StructuredSelection) {
            items = ((StructuredSelection) selection).toArray();
        }
        if( items.length==0) {
            items = loadNonWorkbencSelection();
        }
        for (int i = 0; i < items.length; i++) {
            Object object = items[i];
            layers.addAll(convertToGeoResource(object));
        }
    }

    /**
     * If the workbench selection has no selection then this method is called to see if there is another
     * logical selection.  For example a map or a catalog depending on the view selected.
     * 
     * TODO Created to avoid implementing a new Selection provider for certain views. 
     *
     * @return
     */
    protected Object[] loadNonWorkbencSelection() {
        return new Object[0];
    }

    /**
     * Creates data objects from the object.  The object was part of the selection.
     * Takes the object and some how adapts it to an IGeoResource.  Protected so that a subclass 
     * can extend the behaviour to try other methods.
     *
     * @param object an object in the selection.
     * @return 

     * 
     * @param object the object to export feature from.
     * @return a collection of data objects.
     * @throws IOException
     */
    protected Collection<Data> convertToGeoResource( Object object ) throws IOException {
        Collection<Data> data = new HashSet<Data>();
        
        tryAdaptingToGeoResource(object, data);
        tryAdaptingToService(object, data);
        
        return data;
    }

    private void tryAdaptingToGeoResource( Object object, Collection<Data> data ) throws IOException {
        if (object instanceof IGeoResource) {
            data.add(new Data((IGeoResource) object));
        } else {
            
            IGeoResource resource = AdapterUtil.instance.adaptTo(IGeoResource.class, object, ProgressManager.instance().get());
            if( resource!=null ){
               data.add(new Data(resource)); 
            }
        }
    }

    private void tryAdaptingToService( Object object, Collection<Data> data ) throws IOException {
        if (object instanceof IService) {
            IService service = (IService) object;
            List< ? extends IGeoResource> resources = service.resources(ProgressManager.instance().get());
            for( IGeoResource resource : resources ) {
                data.add(new Data(resource));
            }
        } 
        
    }

    @Override
    public Pair<Boolean, State> dryRun() {
        // complete if all the resources have been "selected"
        boolean result = true;
        if (layers == null || layers.isEmpty() || selectedLayers == null || selectedLayers.isEmpty())
            result = false;
        return new Pair<Boolean, State>(result, null);
    }
    
    @Override
    public boolean run( IProgressMonitor monitor ) throws IOException {
        return dryRun().getLeft();
    }

	@Override
	public String getName() {
		return Messages.CatalogExport_exportLayersTask; 
	}

	public String getExportDir() {
		return exportDir;
	}

	public void setExportDir(String text) {
		exportDir = text;
	}

}
