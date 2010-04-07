package net.refractions.udig.project.ui.internal;

import java.util.List;

import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.ViewportModel;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A Wizard page for selecting a map. Part of the New Layer wizard.
 * 
 * @author Richard Gould
 */
public class SelectMapPage extends WizardPage implements ISelectionChangedListener {

    Map selectedMap;

    private TreeViewer tree;

    private IStructuredSelection selection;

    protected SelectMapPage() {
        super(Messages.SelectMapPage_page_title); 
        setDescription(Messages.SelectMapPage_page_description); 
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {

        Composite composite = new Composite(parent, SWT.NULL);

        GridLayout gridLayout = new GridLayout(2, false);
        composite.setLayout(gridLayout);

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.SelectMapPage_label_selectAMap_text); 
        label.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
        label.setToolTipText(Messages.SelectMapPage_page_description); 

        tree = new TreeViewer(composite, SWT.SINGLE);

        tree.setAutoExpandLevel(2);
        tree.setContentProvider(new AdapterFactoryContentProvider(
                ProjectUIPlugin.getDefault().getAdapterFactory()));

        tree.setLabelProvider(new AdapterFactoryLabelProvider(ProjectUIPlugin.getDefault().getAdapterFactory()));
        tree.setInput(ProjectPlugin.getPlugin().getProjectRegistry());

        tree.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tree.addSelectionChangedListener(this);

        setControl(composite);
        setPageComplete(true);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        selection = (IStructuredSelection) event.getSelection();
        Object obj = selection.getFirstElement();
        if (obj instanceof Map) {
            getWizard().getContainer().updateButtons();
            return;
        } else if (obj instanceof Project) {
            List maps = ((Project) obj).getElements(Map.class);
            selection = new StructuredSelection(maps.size() > 0 ? maps.get(0) : obj);
        } else if (obj instanceof ContextModel) {
            selection = new StructuredSelection(((ContextModel) obj).getMap());
        } else if (obj instanceof ViewportModel) {
            selection = new StructuredSelection(((ViewportModel) obj).getMapInternal());
        } else if (obj instanceof RenderManager) {
            selection = new StructuredSelection(((RenderManager) obj).getMapInternal());
        } else if (obj instanceof Layer) {
            selection = new StructuredSelection(((Layer) obj).getContextModel().getMap());
        } else
            return;
        tree.setSelection(selection, true);
        getWizard().getContainer().updateButtons();

    }

    /**
     * @see org.eclipse.jface.wizard.IWizardPage#canFlipToNextPage()
     */
    public boolean canFlipToNextPage() {
        // TODO Auto-generated method stub
        return isPageComplete();
    }

    /**
     * @see org.eclipse.jface.wizard.IWizardPage#isPageComplete()
     */
    public boolean isPageComplete() {
        if (selection == null) {
            return false;
        }
        if (selection.isEmpty()) {
            return false;
        }
        if (selection.getFirstElement() instanceof Map) {
            selectedMap = (Map) selection.getFirstElement();
            System.out.println(selectedMap);
            return true;
        }
        return false;
    }
}
