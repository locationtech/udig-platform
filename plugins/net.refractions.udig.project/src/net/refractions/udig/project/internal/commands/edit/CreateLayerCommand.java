/**
 * 
 */
package net.refractions.udig.project.internal.commands.edit;

import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Creates a layer and adds it to the executing map.  The resources must already exist
 * in the catalog.
 * 
 * @author jones
 *
 */
public class CreateLayerCommand extends AbstractCommand implements UndoableMapCommand{

	IGeoResource resource;
	private URL id;
	private Layer layer;
	
	/**
	 * Creates a layer from the resource.
	 * 
	 * @param resource a resource to create the layer from.
	 */
	public CreateLayerCommand(IGeoResource resource) {
		this.resource=resource;
		id=resource.getIdentifier();
	}
	
	/**
	 * Create a layer from a IGeoResource identifier url.
	 * @param id2 Must identify a IGeoResource.  
	 */
	public CreateLayerCommand(URL id2) {
		assert id2.getRef()!=null && id2.getRef().trim().length()!=0;
		this.id=id2;
	}
	
	/* (non-Javadoc)
	 * @see net.refractions.udig.project.command.Command#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws Exception {
		if( resource==null ){
			List<IResolve> resources = CatalogPlugin.getDefault().getLocalCatalog().find(id, monitor);
			if( resources.size()==0 )
				throw new Exception(Messages.CreateLayerCommand_badID+id+Messages.CreateLayerCommand_badID2);  
			
			resource=(IGeoResource) resources.get(0);
		}
		this.layer=getMap().getLayerFactory().createLayer(resource);
		getMap().getLayersInternal().add(this.layer);
	}

	/* (non-Javadoc)
	 * @see net.refractions.udig.project.command.Command#getName()
	 */
	public String getName() {
		return Messages.CreateLayerCommand_name+id; 
	}

	public void rollback(IProgressMonitor monitor) throws Exception {
		if (this.layer==null)
			throw new IllegalStateException( Messages.CreateLayerCommand_illegalRollback + 
					Messages.CreateLayerCommand_illegalRollback2); 
		getMap().getLayersInternal().remove(layer);
	}

}
