/**
 * LGPL
 */
package net.refractions.udig.project.ui.wizard.export.map;

import java.util.Collection;

import net.refractions.udig.core.internal.Icons;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.wizard.export.MapSelectorPage;
import net.refractions.udig.project.ui.wizard.export.image.MapSelectorPageWithScaleColumn;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard for exporting a project, its maps and all associated data to a project.
 * 
 * @author Jesse
 */
public class ExportMapWizard extends Wizard implements IExportWizard {

	MapSelectorPage mapSelector;
	
	/**
	 * Create a new instance
	 */
	public ExportMapWizard() {
	    setWindowTitle("Export Map");
	    String title = null; // will use default page title
	    ImageDescriptor banner = ProjectUIPlugin.getImageDescriptor( Icons.WIZBAN +"exportselection_wiz.gif" );
	    //setDefaultPageImageDescriptor(banner);
	    mapSelector = new MapSelectorPageWithScaleColumn("Export Selection", title, banner );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Collection<IMap> maps = mapSelector.getMaps();
		for (IMap map : maps) {
			collect(map);
		}
		
		zip();
		return true;
	}

	private void collect(IMap map) {
		
	}

	private void zip() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		mapSelector.setSelection(selection);
	}

}
