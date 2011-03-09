/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.ui;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveFolder;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IResolve.Status;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ResolveLabelProviderSimple;
import net.refractions.udig.catalog.ui.ResolveTitlesDecorator;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.ResourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * A page that allows the user to select the resources he/she wish to include in the map.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public class ResourceSelectionPage extends WorkflowWizardPage implements IPageChangedListener {

    private Map<IResolve, List<IResolve>> resolveMap = new HashMap<IResolve, List<IResolve>>();
    private CheckboxTreeViewer viewer;

    /** url from workbench selection * */
    private ResolveTitlesDecorator titleDecorator;
    /**
     * Indicates whether selected services should be collapse when input is changed
     */
    private boolean collapseCheckedInput=false;
    private List<Object> grayedElements=new ArrayList<Object>();
    private Label label;
    private Composite blank;
    private String schemaSelected;

    public ResourceSelectionPage( String pageName ) {
        super(pageName);
        setTitle(Messages.ResourceSelectionPage_title); 
        setMessage(Messages.ResourceSelectionPage_message);
        setDescription(Messages.ResourceSelectionPage_description);
        setImageDescriptor(CatalogUIPlugin.getImageDescriptor(ImageConstants.CHOOSE_LAYER_WIZARD));
        schemaSelected = null;
    }

    /**
     * @deprecated
     */
    public void setResources( List<IResolve> serviceList, IProgressMonitor monitor ) {
    }

    @Override
    public void dispose() {
        super.dispose();
        if( viewer==null ){
        	return;
        }
        if( viewer.getContentProvider()!=null )
        	viewer.getContentProvider().dispose();
        if( viewer.getLabelProvider()!=null )
        	viewer.getLabelProvider().dispose();
    }
    
    private List<IResolve> getGeoResources( final IResolve resolve, boolean fork ) {
        if (resolveMap.get(resolve) == null || resolveMap.isEmpty()) {
            final List<IResolve> list = new ArrayList<IResolve>();

            try {
                IRunnableWithProgress runnable = new IRunnableWithProgress(){

                    @SuppressWarnings("unchecked")
                    public void run( IProgressMonitor monitor ) {
                        monitor.beginTask(Messages.ResourceSelectionPage_searching,
                                IProgressMonitor.UNKNOWN);
                        try {
                            List<IResolve> members = resolve.members(monitor);
                            list.addAll(members);
                            if (schemaSelected != null){
                                for( IResolve resolve2 : members ) {
                                    IResolveFolder folder = (IResolveFolder) resolve2;
                                    if (folder.getTitle() != schemaSelected){
                                        list.remove(resolve2);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // do nothing
                            CatalogUIPlugin.log("Error finding resources", e); //$NON-NLS-1$
                        }
                        monitor.done();
                    }

                };
                if (fork) {
                    getContainer().run(false, true, runnable);
                } else {
                    runnable.run(new NullProgressMonitor());
                }
            } catch (Exception e) {
                CatalogUIPlugin.log("", e); //$NON-NLS-1$
            }
            resolveMap.put(resolve, list);
        }
        return resolveMap.get(resolve);
    }

    /**
     * @deprecated
     */
    public List<Object> getCheckedElements() {
        List<Object> list = new ArrayList<Object>();
        for( Object object : viewer.getCheckedElements() ) {
            if (object instanceof IGeoResource) {
                IGeoResource resource = (IGeoResource) object;
                list.add(resource);
            }
        }
        return list;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        
        viewer = new CheckboxTreeViewer(composite);
        
        viewer.setSorter( null );
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.addPostSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged( SelectionChangedEvent event ) {
                getWizard().getContainer().updateButtons();
            }

        });
        viewer.addCheckStateListener(new ResourceSelectionPageCheckStateListener());
        viewer.setContentProvider(new ServiceTreeProvider());

        titleDecorator = new ResolveTitlesDecorator(new ResolveLabelProviderSimple(), true);
        LabelProvider labelProvider = new DecoratingLabelProvider(titleDecorator.getSource(),
                titleDecorator);

        viewer.setLabelProvider(labelProvider);
        viewer.setAutoExpandLevel(3);

        // use the state to initialize ui
        ResourceSelectionState state = (ResourceSelectionState) getState();

        label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        label.setText(MessageFormat.format(Messages.ResourceSelectionPage_NumLayersSelected,0));        
        
        setInput(state);

        setControl(composite);
    }

    /**
     * Public only for testing purposes.  Treat as if it is private.
     */
    public CheckboxTreeViewer getViewer() {
        return viewer;
    }

    /**
     * This method is mainly for testing purposes and should not be called by client code.
     */
    public void syncWithUI() {
        try {
            // LinkedHashMap to keep the order
            Map<IGeoResource, IService> map = new LinkedHashMap<IGeoResource, IService>();
            Object[] elements = getViewer().getCheckedElements();

            List<IGeoResource> resources = new ArrayList<IGeoResource>();
            for( int i = 0; i < elements.length; i++ ) {
                resources.addAll(collectResources((IResolve)elements[i]));
            }
            for( IGeoResource geoResource : resources ) {
                map.put(geoResource, geoResource.service(new NullProgressMonitor()));
            }
            label.setText(MessageFormat.format(Messages.ResourceSelectionPage_NumLayersSelected,map.size()));
            ((ResourceSelectionState) getState()).setResources(map);
        } catch (IOException e) {
            CatalogUIPlugin.log(e.getLocalizedMessage(), e);
        }
    }

    private List<IGeoResource> collectResources( IResolve resolve ) throws IOException {

        if( viewer.getExpandedState(resolve) && !(resolve instanceof IGeoResource) ){
            // it is expanded so the selected children will be checked
            return Collections.emptyList();
        }
        
        // not expanded so all children are considered selected.
        if( resolve instanceof IGeoResource){
            return Collections.singletonList((IGeoResource)resolve);
        } else {
            List<IGeoResource> resources = new ArrayList<IGeoResource>();
            List<IResolve> members = resolve.members(new NullProgressMonitor());
            for( IResolve resolve2 : members ) {
                resources.addAll(collectResources(resolve2));
            }
            return resources;
        }
    }

    @Override
    public void shown() {
        setInput((ResourceSelectionState) getState());
    }

    private void setInput( ResourceSelectionState state ) {
        grayedElements.clear();
        int checked=0;
        if (state.getWorkflow().getContext() instanceof IResolveFolder){
            IResolveFolder resolveFolder = (IResolveFolder) state.getWorkflow().getContext();
            IService service = resolveFolder.getService(new NullProgressMonitor());
            schemaSelected = resolveFolder.getTitle();
            viewer.setInput(service);
        }else {
            viewer.setInput(state.getServices()); // initialize viewer input to nothing
            Map<IGeoResource, IService> resources = state.getResources();
            
            if (resources != null) {
                Set<IService> expanded=new HashSet<IService>();
                for( Map.Entry<IGeoResource, IService> entry:resources.entrySet() ) {
                    checked++;
                    IGeoResource resource = entry.getKey();
                    IService service = entry.getValue();
                    
                    viewer.setChecked(resource, true);
                    viewer.setChecked(service, true);
                    expanded.add(service);
                }
                if( collapseCheckedInput ){
                    for( IService service : expanded ) {
                        viewer.setExpandedState(service, false);
                    }
                }
            }
        }
        viewer.setGrayedElements(grayedElements.toArray());
    
        label.setText(MessageFormat.format(Messages.ResourceSelectionPage_NumLayersSelected,checked));
    }

    Button findButton( Control[] children, int id ) {
        if (((Integer) getShell().getDefaultButton().getData()).intValue() == id) 
            return getShell().getDefaultButton();

        for( Control child : children ) {
            if (child instanceof Button) {
                Button button = (Button) child;
                if (((Integer) button.getData()).intValue() == id)
                    return button;
            }
            if (child instanceof Composite) {
                Composite composite = (Composite) child;
                Button button = findButton(composite.getChildren(), id);
                if (button != null)
                    return button;
            }
        }
        return null;
    }

    private final class ResourceSelectionPageCheckStateListener
            implements
                ICheckStateListener {
        public void checkStateChanged( CheckStateChangedEvent event ) {
            // set all children to same check state
            Object o = event.getElement();
            boolean checked = event.getChecked();
            ServiceTreeProvider p = updateChildren(o, checked);

            // if checked set parent checked (provided parent is not a IGeoResource)
            if (checked) {
                Object parent = p.getParent(o);
                while (parent != null && !(parent instanceof IGeoResource) ){
                    viewer.setChecked(parent, true);
                    parent = p.getParent(parent);
                }
            }

            syncWithUI();
        }
        private ServiceTreeProvider updateChildren(Object o, boolean checked) {
            ServiceTreeProvider p = (ServiceTreeProvider) viewer.getContentProvider();
            if( o instanceof IGeoResource ){
                return p;  
            }
            Object[] children = p.getChildren(o);
            if (children != null && children.length > 0) {
                for( int i = 0; i < children.length; i++ ){
                    viewer.setChecked(children[i], checked);
                    updateChildren(children[i], checked);
                }
            }
            return p;
        }
    }

    public class ServiceTreeProvider extends LabelProvider implements ITreeContentProvider {

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object inputElement ) {
            return getChildren(inputElement);
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
         *      java.lang.Object, java.lang.Object)
         */
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        }

        @SuppressWarnings("unchecked")
        public Object[] getChildren( Object parentElement ) {
            if (parentElement instanceof Collection) {
                Collection<IResolve> list = (Collection<IResolve>) parentElement;
                if( list.isEmpty() ){
                    return reasons();
                }
                return list.toArray();
            }
            if (parentElement instanceof IResolve) {
                if (parentElement instanceof IResolveFolder) {
                    IResolveFolder folder = (IResolveFolder) parentElement;
                    if (schemaSelected != null){
                        try {
                            if(schemaSelected == folder.getTitle()) {
                                List<IResolve> children2 = folder.members(new NullProgressMonitor());
                                return children2.toArray();
                            }else{
                                return null;
                            }
                        } catch (IOException e) {
                            CatalogUIPlugin.log(null, e);
                        }
                    }
                }
                IResolve service = (IResolve) parentElement;
                List<IResolve> children = getGeoResources(service, true);
                if( children.isEmpty() ){
                    if( parentElement instanceof IService ){
                        grayedElements.add(service);
                        if( service.getStatus()==Status.BROKEN ){
                            if( service.getMessage()!=null ) {
                                String string = Messages.ResourceSelectionPage_brokenReportError+service.getMessage().getLocalizedMessage();
                                grayedElements.add(string);
                                return new String[]{string};
                            } else{
                                String string = Messages.ResourceSelectionPage_brokenUnknown;
                                grayedElements.add(string);
                                return new String[]{string};
                            }
                        }
                        if( service.getStatus()==Status.RESTRICTED_ACCESS ){
                            String string = Messages.ResourceSelectionPage_noPermission;
                            grayedElements.add(string);
                            return new String[]{string};
                        }
                        if( service.getStatus()==Status.CONNECTED ){
                            String string = Messages.ResourceSelectionPage_connectedButNoResources;
                            grayedElements.add(string);
                            return new String[]{string};
                        }
                    }else{
                        return null;
                    }
                }
                return children.toArray();
            }
            return null;
        }

        private Object[] reasons() {
            return new String[]{Messages.ResourceSelectionPage_noServices};
        }

        public Object getParent( Object element ) {
            if (element instanceof IResolve) {
                IResolve resource = (IResolve) element;
                try {
                    return resource.parent(new  NullProgressMonitor());
                } catch (IOException e) {
                    CatalogUIPlugin.log(null, e);
                }
            }
            return null;
        }

        public boolean hasChildren( Object element ) {
            return element instanceof IResolve;
        }

        @Override
        public String getText( Object element ) {
            if (element instanceof IResolve) {
                IResolve resolver = (IResolve) element;
                return resolver.getIdentifier().toString();
            }
            return null;
        }

    }

    public void pageChanged( PageChangedEvent event ) {
    }

    public boolean isCollapseCheckedInput() {
        return collapseCheckedInput;
    }

    public void setCollapseCheckedInput( boolean collapseCheckedInput ) {
        this.collapseCheckedInput = collapseCheckedInput;
    }
}
