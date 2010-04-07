package net.refractions.udig.browser.ui;

import java.net.URL;

import net.refractions.udig.browser.ExternalCatalogueImportPage;
import net.refractions.udig.browser.internal.Messages;
import net.refractions.udig.catalog.ui.IDataWizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Wizard used to select a catalog for display in the BrowserView
 * <p>
 *
 * </p>
 * @author mleslie
 * @since 1.0.0
 */
public class CatalogueBrowserWizard extends IDataWizard {

    public CatalogueBrowserWizard() {
        super();
    }
    
    @Override
    public boolean canFinish() {
        IWizardPage page = getContainer().getCurrentPage();
        if(page instanceof ExternalCatalogueImportPage) {
            return true;
        }
        if(page instanceof BrowserSelectionPage) {
            return ((BrowserSelectionPage)page).canFinish();
        }
        return false;
    }
    
    @Override
    protected WizardPage[] getPrimaryPages() {
		return new WizardPage[]{new BrowserSelectionPage()};
    }
    
    public void init( IWorkbench workbench, IStructuredSelection selection ) {
        super.init(workbench, selection);
        
        setWindowTitle(Messages.CatalogueBrowserWizard_windowTitle);
//        setDefaultPageImageDescriptor( Images.getDescriptor( ImageConstants.DATA_WIZBAN ));
    }

    @Override
    public boolean performFinish() {
        IWizardPage ipage = getContainer().getCurrentPage();
        String name = null;
        URL url = null;
        //String viewName = null;
        ImageDescriptor image = null;
        LocationListener listen = null;
        if(ipage instanceof BrowserSelectionPage) {
            BrowserSelectionPage page = (BrowserSelectionPage)ipage;
            listen = page.getListener();
            url = page.getUrl();
            image = page.getIconDescriptor();
            name = page.getTitle();
            //viewName = page.getViewName();
        } else {
            ExternalCatalogueImportPage page = (ExternalCatalogueImportPage)ipage;
                
            //if import page returns a new page, then an error occured
            IWizardPage next = page.getNextPage(); 
            if (next != null) {
                //error, 
                getContainer().showPage(next);
                return false;
            }
            listen = page.getListener();
            url = page.getURL();
            image = page.getIconDescriptor();
            name = page.getTitle();
            //viewName = page.getViewName();
        }
        IWorkbenchPage wbPage  = PlatformUI.getWorkbench().
                getActiveWorkbenchWindow().getActivePage();
        IViewPart part;
        try {
            part = wbPage.showView(BrowserContainerView.VIEW_ID);
        } catch (PartInitException e) {
            return false;
        }
        if(part instanceof BrowserContainerView) {
            BrowserContainerView view = (BrowserContainerView)part;
            view.addTab(name, url, image, listen);
        } else {
            return false;
        }
        return true;
    }

}
