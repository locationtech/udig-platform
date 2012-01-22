/**
 * 
 */
package eu.udig.tools.merge;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.simple.SimpleFeature;

import eu.udig.tools.merge.internal.view.MergeView;

/**
 * 
 * @author Mauricio Pazos
 * @deprecated 
 */
final class MergeViewOpenCommand  extends AbstractCommand implements UndoableMapCommand{

	
	private static final Logger LOGGER = Logger.getLogger(MergeViewOpenCommand.class.getName());
	private MergeContext mergeContext;
	private List<SimpleFeature> selectedFeatures = Collections.emptyList();


	public MergeViewOpenCommand(MergeContext mergeContext, List<SimpleFeature> selectedFeatures) {

		assert mergeContext != null : "merge context is null"; //$NON-NLS-1$
		assert selectedFeatures != null : "merge context is null"; //$NON-NLS-1$

		this.mergeContext = mergeContext;
		this.selectedFeatures  = selectedFeatures;
	}
	public MergeViewOpenCommand(MergeContext mergeContext) {

		assert mergeContext != null : "merge context is null"; //$NON-NLS-1$

		this.mergeContext = mergeContext;
	}

	/**
	 * Opens the merge view and set its parameters. When all the parameters are
	 * set, the view will populate its widget with the correspondent data.
	 * @param monitor
	 * @throws Exception
	 */
	@Override
	public void run(IProgressMonitor monitor) throws Exception {

		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {

				// retrieve the reference to the merge view
				MergeView view = (MergeView)ApplicationGIS.getView(true, MergeView.ID);
				if(view == null){
					// crates a new merge view
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					view = (MergeView) page.findView(MergeView.ID);
				}
				assert view != null : "view is null"; //$NON-NLS-1$
				
				// associates this the merge view with the merge context
				view.setMergeContext(mergeContext);
				mergeContext.setMergeView(view);
	
				// FIXME view.setFeatures(selectedFeatures);
				
			}
		});
	}

	@Override
	public String getName() {
		String name = this.getClass().getName();
		return name;
	}


	@Override
	public void rollback(IProgressMonitor monitor) throws Exception {
		closeMergeView();
	}

	/**
	 * Closes the view.
	 */
	private void closeMergeView() {
		
		ApplicationGIS.getView(false, MergeView.ID);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(MergeView.ID);
		page.hideView(viewPart);

	}	



}
