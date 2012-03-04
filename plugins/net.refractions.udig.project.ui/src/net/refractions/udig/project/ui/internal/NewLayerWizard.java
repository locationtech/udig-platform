package net.refractions.udig.project.ui.internal;

import java.util.List;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Richard Gould
 */
public class NewLayerWizard extends Wizard implements INewWizard {

    SelectMapPage selectMapPage;
    SelectLayersPage selectLayersPage;
    MapStylePage selectStylePage;

    /**
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        selectMapPage = new SelectMapPage();
        selectLayersPage = new SelectLayersPage();

        addPage(selectMapPage);
        addPage(selectLayersPage);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     */
    public boolean canFinish() {
        return selectMapPage.isPageComplete() && selectLayersPage.isPageComplete();
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    public boolean performFinish() {
        Display.getDefault().asyncExec(new Runnable(){
            public void run() {
                Map map = selectMapPage.selectedMap;
                List layers = selectLayersPage.getLayerRefs();

                for( int i = 0; i < layers.size(); i++ ) {
                    Layer layer = (Layer) layers.get(i);
                    map.getContextModel().getLayers().add(layer);
                }
            }
        });
        return true;
    }
    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        // TODO Auto-generated method stub
    }
}
