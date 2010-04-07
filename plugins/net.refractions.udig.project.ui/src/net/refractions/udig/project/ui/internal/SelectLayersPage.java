/*
 * Created on 4-Oct-2004 TODO To change the template for this generated file go to Window - Preferences - Java - Code
 * Style - Code Templates
 */
package net.refractions.udig.project.ui.internal;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.CatalogTreeViewer;
import net.refractions.udig.project.internal.ProjectFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Richard Gould TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class SelectLayersPage extends WizardPage implements ISelectionChangedListener {

    private net.refractions.udig.catalog.ui.CatalogTreeViewer layers;
    private IStructuredSelection selection;

    List layerRefs;

    protected SelectLayersPage() {
        super(Messages.SelectLayersPage_page_title); 
        this.setDescription(Messages.SelectLayersPage_page_description); 
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {
        Composite composite = new Composite(parent, SWT.NULL);

        GridLayout gridLayout = new GridLayout(2, false);
        composite.setLayout(gridLayout);

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.SelectLayersPage_label_selectLayers_text); 
        label.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
        label.setToolTipText(Messages.SelectLayersPage_label_selectLayers_tooltip); 

        layers = new CatalogTreeViewer(composite);
        layers.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        layers.addSelectionChangedListener(this);

        setControl(composite);
        setPageComplete(true);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        selection = (IStructuredSelection) event.getSelection();

        getWizard().getContainer().updateButtons();
    }

    /**
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     */
    public boolean canFlipToNextPage() {
        return isPageComplete();
    }

    /**
     * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
     */
    public boolean isPageComplete() {
        if (selection == null || selection.isEmpty()) {
            return false;
        }

        Iterator iter = selection.iterator();
        while( iter.hasNext() ) {
            Object object = iter.next();
            if ((object instanceof IService) || (object instanceof IGeoResource)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
     */
    public IWizardPage getNextPage() {
        if (((NewLayerWizard) getWizard()).selectStylePage == null) {
            ((NewLayerWizard) getWizard()).selectStylePage = new MapStylePage(getLayerRefs());
            ((NewLayerWizard) getWizard()).addPage(((NewLayerWizard) getWizard()).selectStylePage);
        }
        return ((NewLayerWizard) getWizard()).selectStylePage;
    }

    List getLayerRefs() {
        if (layerRefs == null)
            try {
                layerRefs = ProjectFactory.eINSTANCE.createLayerFactory().getLayers(
                        selection.toList());
            } catch (IOException e) {
                // fall through
                ProjectUIPlugin.getDefault().getLog().log(
                        new Status(IStatus.WARNING, "net.refractions.udig.project.ui", 0, "", e)); //$NON-NLS-1$ //$NON-NLS-2$
            }
        return layerRefs;
    }
}
