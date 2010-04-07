package net.refractions.udig.mapgraphic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.ResolveLabelProviderSimple;
import net.refractions.udig.catalog.ui.ResolveTitlesDecorator;
import net.refractions.udig.mapgraphic.internal.MapGraphicResource;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.mapgraphic.internal.Messages;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog for selecting Map Graphic IGeoResources.
 * 
 * @author jesse
 * @since 1.1.0
 */
public final class MapGraphicChooserDialog extends TitleAreaDialog {
    public static class MapGraphicTreeContentProvider implements ITreeContentProvider {

        public Object[] getElements( Object inputElement ) {
            if (inputElement instanceof IService) {
                try {
                    return ((IService) inputElement).members(ProgressManager.instance().get())
                            .toArray();
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException().initCause(e);
                }
            }
            return null;
        }

        public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        }

        public Object[] getChildren( Object parentElement ) {
            return getElements(parentElement);
        }

        public Object getParent( Object element ) {
            return null;
        }

        public boolean hasChildren( Object element ) {

            if (element instanceof IService) {
                return true;
            }
            return false;
        }

        public void dispose() {
        }
    }
    private TreeViewer viewer;
    private final ArrayList<MapGraphicResource> resourceList = new ArrayList<MapGraphicResource>();
    private final boolean permitMultipleSelection;

    /**
     * Create instance
     * 
     * @param permitMultipleSelection if true then multiple resources can be selected
     */
    public MapGraphicChooserDialog( Shell parentShell, boolean permitMultipleSelection ) {
        super(parentShell);
        this.permitMultipleSelection = permitMultipleSelection;
    }
    @Override
    protected int getShellStyle() {
        return SWT.RESIZE | SWT.MAX | SWT.CLOSE | SWT.MIN | SWT.APPLICATION_MODAL;
    }
    @Override
    protected void configureShell( Shell newShell ) {
        newShell.setText(Messages.OtherAction_shellText);
        super.configureShell(newShell);
    }
    @Override
    protected Point getInitialSize() {
        return new Point(400, 400);
    }
    @Override
    protected Control createContents( Composite parent ) {
        Control control = super.createContents(parent);
        setTitle(Messages.OtherAction_wizardTitle);
        setMessage(Messages.OtherAction_message1);
        return control;
    }
    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite composite = (Composite) super.createDialogArea(parent);
        int style = SWT.V_SCROLL;
        if (permitMultipleSelection) {
            style |= SWT.MULTI;
        }

        org.eclipse.swt.widgets.Tree tree = new org.eclipse.swt.widgets.Tree(composite, style);
        viewer = new TreeViewer(tree);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = SWT.DEFAULT;
        gridData.widthHint = SWT.DEFAULT;
        gridData.verticalSpan = 4;
        tree.setLayoutData(gridData);
        viewer.setContentProvider(new MapGraphicTreeContentProvider());
        ResolveLabelProviderSimple resolveLabelProviderSimple = new ResolveLabelProviderSimple();
        ResolveTitlesDecorator resolveTitlesDecorator = new ResolveTitlesDecorator(
                resolveLabelProviderSimple, true);
        viewer.setLabelProvider(new DecoratingLabelProvider(resolveLabelProviderSimple,
                resolveTitlesDecorator));
        viewer.setInput(getMapGraphicService());
        viewer.addPostSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged( SelectionChangedEvent event ) {
                IStructuredSelection s = (IStructuredSelection) event.getSelection();
                if (s.isEmpty())
                    return;

                String title = null;
                try {
                    IGeoResource resource = (IGeoResource) s.getFirstElement();
                    title = resource.getTitle();
                    if( title == null ){
                        title = resource.getInfo(null).getTitle();
                    }
                } catch (IOException e) {
                    MapGraphicPlugin.log("", e); //$NON-NLS-1$
                }
                if (s.size() == 1 && title != null) {
                    setMessage(Messages.OtherAction_p1 + title + Messages.OtherAction_p2);
                } else {
                    setMessage(Messages.OtherAction_addAll);
                }
            }

        });
        viewer.addDoubleClickListener(new IDoubleClickListener(){

            public void doubleClick( DoubleClickEvent event ) {
                buttonPressed(IDialogConstants.OK_ID);
            }

        });
        return composite;
    }
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        createButton(parent, IDialogConstants.OK_ID, Messages.OtherAction_addButton, true);
    }
    @Override
    protected void buttonPressed( int buttonId ) {
        if (buttonId == IDialogConstants.OK_ID) {
            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            Iterator< ? > iter = selection.iterator();
            resourceList.clear();
            while( iter.hasNext() )
                resourceList.add((MapGraphicResource) iter.next());
        }
        super.buttonPressed(buttonId);
    }

    public List<MapGraphicResource> getSelectedResources() {
        return resourceList;
    }

    /**
     * Returns the IGeoResource for the provided graphic class
     * 
     * @param graphic the MapGraphic to use as a search parameter
     * @return the IGeoResource for the provided graphic class
     */
    public static MapGraphicResource findResource( Class< ? extends MapGraphic> graphic ) {
        MapGraphicService service = getMapGraphicService();
        try {
            List<MapGraphicResource> resources = service.resources(new NullProgressMonitor());
            for( MapGraphicResource mapGraphicResource : resources ) {
                if (graphic.isAssignableFrom(mapGraphicResource.getGraphic().getClass())) {
                    return mapGraphicResource;
                }
            }
            throw new NoSuchElementException(graphic
                    + " does is not registered.  Check that it is in the plugin.xml"); //$NON-NLS-1$
        } catch (IOException e) {
            // Won't happen but hey lets log anyhow
            MapGraphicPlugin.log("Unexpected exception", e); //$NON-NLS-1$
            throw new RuntimeException(e);
        }
    }
    /**
     * Returns the MapGraphic IService
     * 
     * @return the MapGraphic IService
     */
    public static MapGraphicService getMapGraphicService() {
        return CatalogPlugin.getDefault().getLocalCatalog().getById(MapGraphicService.class,
                MapGraphicService.SERVICE_ID, ProgressManager.instance().get());
    }

}