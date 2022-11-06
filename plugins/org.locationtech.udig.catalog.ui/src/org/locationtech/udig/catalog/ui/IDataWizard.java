/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * A wizard which is used to import data into udig.
 * <p>
 * This is wizard is made up of <b>primary</b> pages, and <b>secondary</b> pages. A primary page is
 * an
 *
 * @see org.eclipse.jface.wizard.IWizardPage that the wizard declares it will contain. A secondary
 *      page is a page which is dynamically contributed to the wizard via a primary page, or another
 *      secondary page.
 *      </p>
 *      <p>
 *      Sublcasses declare the ordered set of primary pages with the getPrimaryPages() method.
 *      Secondary pages are contributed dynamically by returning them from a call to
 * @see org.eclipse.jface.wizard.IWizardPage#getNextPage().
 *      </p>
 *      <p>
 *      Secondary page processing will continue, until a page returns null from getNextPage().
 *      Processing then continues at the next primary page. If no more primary pages exist, the
 *      wizard finishes.
 *      </p>
 *      <p>
 *      If using an IDataWizard outside of the workbench wizard framework, it is up to client code
 *      to call
 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
 *      org.eclipse.jface.viewers.IStructuredSelection) immediatly after instantiating the wizard.
 *      </p>
 *      <p>
 *      The following are requirements on the pages of the wizard.
 *      <ul>
 *      <li>They must extend from
 * @see org.eclipse.jface.wizard.WizardPage
 *      <li>getNextPage must either return a new wizard page, or super.getNextPage()
 *      <li>init(WizardPage) must be called before returning a page from
 *      </ul>
 *      <p>
 *      Example: <code>
 *      public IWizardPage getNextPage() {
 *          if (returnNewPage) {
 *              WizardPage newPage = new WizardPage(....);
 *              IDataWizard wizard = (IDataWizard) getWizard();
 *              wizard.init(page);
 *              return page;
 *          }
 *          return super.getNextPage();
 *      }
 *      </code>
 *      </p>
 *      It is important to note that pages inside this wizard must return a page or
 *      super.getNextPage() from getNextPage().
 *      </p>
 *      <p>
 *      This wizard creates dialog settings for pages upon creation.
 *      </p>
 *
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 */
public abstract class IDataWizard extends Wizard implements IWorkbenchWizard {

    static final String SETTINGS = "IDataWizard"; //$NON-NLS-1$

    /** the primary wizard pages * */
    WizardPage[] pages;

    /** the selection * */
    IStructuredSelection selection = new StructuredSelection();

    /** the workbench * */
    IWorkbench workbench;

    public IDataWizard() {
        // set up dialog settings
        IDialogSettings settings = CatalogUIPlugin.getDefault().getDialogSettings()
                .getSection(SETTINGS);
        if (settings == null) {
            settings = CatalogUIPlugin.getDefault().getDialogSettings().addNewSection(SETTINGS);
        }
        setDialogSettings(settings);
    }

    /**
     * @see IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;

        setNeedsProgressMonitor(true);
    }

    /**
     * @return the selection the wizard was initialized with.
     */
    public IStructuredSelection getSelection() {
        return selection;
    }

    /**
     * @return the workbench the wizard was initialized with.
     */
    public IWorkbench getWorkbench() {
        return workbench;
    }

    /**
     * Adds the primary pages to the wizard.
     *
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    @Override
    public void addPages() {
        this.pages = getPrimaryPages();
        for (int i = 0; i < pages.length; i++) {
            init(pages[i]);
            addPage(pages[i]);
        }
    }

    /**
     * Returns the next primary page in the page sequence. This method is called by the
     *
     * @see org.eclipse.jface.wizard.IWizardContainer when a page does not contribute a secondary
     *      page.
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        // a string of pages is done, find next primary
        return getNextPrimaryPage(page);
    }

    /**
     * Determines if the wizard has any more primary pages.
     *
     * @return True if so, otherwise false.
     */
    public boolean hasMorePrimaryPages() {
        return getNextPrimaryPage(getContainer().getCurrentPage()) != null;
    }

    /**
     * Returns the next primary page, based on the current page.
     *
     * @param page A primary page, or secondary page.
     * @return The next primary page, or null if no more in sequence.
     */
    private IWizardPage getNextPrimaryPage(IWizardPage page) {
        // back up until we reach a primary page
        IWizardPage[] primary = getPages();

        while (page != null) {
            // do an index of in primary list
            for (int i = 0; i < primary.length; i++) {
                if (page == primary[i]) {
                    // found it, make sure not last page
                    if (i == primary.length - 1) {
                        return null;
                    }
                    return primary[i + 1];
                }
            }
            page = page.getPreviousPage();
        }

        return null;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#needsPreviousAndNextButtons()
     */
    @Override
    public boolean needsPreviousAndNextButtons() {
        return true;
    }

    @Override
    /**
     * Determines if there are any more pages in the sequence.
     * <p>
     * The wizard can finish if the following 3 properies hold.
     * <ol>
     * <li>The current page is complete (@see IWizardPage#isPageComplete())
     * <li>The current page can not flip to the next page (@see IWizardPage#canFlipToNextPage())
     * <li>There are no more primary pages.
     * </ol>
     * </p>
     *
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     */
    public boolean canFinish() {
        // check if current page is complete
        IWizardPage page = getContainer().getCurrentPage();
        if (!page.isPageComplete())
            return false;

        // first as the page if it has more pages
        if (page.canFlipToNextPage()) {
            return false;
        }

        // find out if there is another primary page
        return getNextPrimaryPage(page) == null;
    }

    /**
     * Initializes a wizard page for use in the data wizard. This method should be called by pages
     * returning a new page from getNextPage().
     *
     * @param page The page to be initialized.
     */
    public void init(WizardPage page) {
        page.setWizard(this);
    }

    /**
     * Returns the set of primary pages. This method is called while the wizard is being
     * instantiated.
     */
    protected abstract WizardPage[] getPrimaryPages();

}
