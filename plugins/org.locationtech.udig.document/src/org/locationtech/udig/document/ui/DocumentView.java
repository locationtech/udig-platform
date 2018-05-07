/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.ViewPart;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FidFilterImpl;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.document.IAbstractDocumentSource;
import org.locationtech.udig.catalog.document.IAttachment;
import org.locationtech.udig.catalog.document.IAttachmentSource;
import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocument.ContentType;
import org.locationtech.udig.catalog.document.IDocument.Type;
import org.locationtech.udig.catalog.document.IDocumentFolder;
import org.locationtech.udig.catalog.document.IDocumentSource;
import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;
import org.locationtech.udig.catalog.document.IHotlink;
import org.locationtech.udig.catalog.document.IHotlinkSource;
import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import org.locationtech.udig.core.AdapterUtil;
import org.locationtech.udig.document.source.ShpDocFactory;
import org.locationtech.udig.document.ui.DocumentDialog.Mode;
import org.locationtech.udig.document.ui.internal.Messages;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.commands.edit.SetAttributeCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.identity.FeatureId;

import net.miginfocom.swt.MigLayout;

/**
 * The Document View provides a user interface to view, edit, delete and open attached documents. In
 * this current release, the view uses shapefile specific implementations of {@link IDocumentSource}
 * and {@link IAttachmentSource} as its document sources.
 * 
 * @author paul.pfeiffer
 * @author Naz Chan
 */
public class DocumentView extends ViewPart {
    
    private TreeViewer viewer;
    private Button attachButton;
    private Button linkButton;
    private Button editButton;
    private Button openButton;
    private Button saveAsButton;
    private Action saveAsAction;
    private Button removeButton;

    private ResourceSelectionProvider resourceSelectionProvider = new ResourceSelectionProvider();
    private IGeoResource geoResource;
    private SimpleFeature feature;
    private IStructuredSelection viewerSelection;
    private IStructuredSelection workbenchSelection;
    private DocumentItemModel itemModel;
    
    private boolean isActive = false;
    private ISelectionListener workbenchSelectionListener;
    private IPartListener workbenchPartListener;
    
    private boolean isResourceEnabled = false;
    private boolean isFeatureEnabled = false;
    private boolean isHotlinkEnabled = false;
    
    private static final int DOCUMENT_INDEX = 0;
    private static final int DOCUMENT_WEIGHT = 40;
    
    private static final int TYPE_INDEX = 1;
    private static final int TYPE_WEIGHT = 10;
    
    private static final int DESCRIPTION_INDEX = 2;
    private static final int DESCRIPTION_WEIGHT = 50;
    
    public DocumentView() {
        this.itemModel = new DocumentItemModel();
    }

    /**
     * Adds workbench listeners. This will give us control over workbench events that drive the
     * view's contents. Eg. workbench selection, view activation, etc.
     */
    private void addWorkbenchListeners() {

        final IWorkbenchPartSite partSite = getSite();
        final ISelectionService selectionService = partSite.getWorkbenchWindow()
                .getSelectionService();
        
        // Add workbench selection listener
        workbenchSelectionListener = new ISelectionListener() {
            @Override
            public void selectionChanged(IWorkbenchPart part, ISelection selection) {                
                if (!(part instanceof DocumentView)) {
                    handleWorkbenchSelection(selection);    
                }
            }
        };
        selectionService.addPostSelectionListener(workbenchSelectionListener);
        
        // Add workbench part listener
        workbenchPartListener = new IPartListener() {
            @Override
            public void partOpened(IWorkbenchPart part) {
                if (part instanceof DocumentView) {
                    handleWorkbenchSelection(selectionService.getSelection());
                }
            }
            @Override
            public void partDeactivated(IWorkbenchPart part) {
                // Nothing
            }
            @Override
            public void partClosed(IWorkbenchPart part) {
                // Nothing
            }
            @Override
            public void partBroughtToTop(IWorkbenchPart part) {
                // Nothing
            }
            @Override
            public void partActivated(IWorkbenchPart part) {
                isActive = (part instanceof DocumentView);
            }
        };
        partSite.getPage().addPartListener(workbenchPartListener);
        
    }
    
    /**
     * Removes the workbench listeners. This cleans up the listeners we are adding during startup of
     * the view.
     */
    private void removeWorkbenchListeners() {
        if (workbenchSelectionListener != null) {
            getSite().getWorkbenchWindow().getSelectionService()
                    .removePostSelectionListener(workbenchSelectionListener);
            workbenchSelectionListener = null;
        }
        if (workbenchPartListener != null) {
            getSite().getPage().removePartListener(workbenchPartListener);
            workbenchPartListener = null;
        }
    }
    
    @Override
    public void createPartControl(final Composite viewParent) {
        
        setPartName(Messages.docView_name);
        createViewMenu();
        
        final Composite parent = new Composite(viewParent, SWT.NONE);
        final String treeLayoutConst = "insets 0, fill, wrap 2"; //$NON-NLS-1$
        final String treeColConst = "[85%]0[15%]"; //$NON-NLS-1$
        final String treeRowConst = "[100%]"; //$NON-NLS-1$
        parent.setLayout(new MigLayout(treeLayoutConst, treeColConst, treeRowConst));
        
        createTreeControlArea(parent);
        createButtonControlArea(parent);
        refreshBtns();

        addWorkbenchListeners();

    }

    /**
     * Creates the menu items for the view's menu.
     */
    private void createViewMenu() {

        saveAsAction = new Action(Messages.docView_saveAs) {
            @Override
            public void run() {
                saveAs();
            }
        };
        saveAsAction.setId(Messages.docView_saveAs);

        final IActionBars actionBars = getViewSite().getActionBars();
        final IMenuManager menuManager = actionBars.getMenuManager();
        menuManager.add(saveAsAction);
        menuManager.add( new Separator() );
        
        PropertyDialogAction resourcePropertyAction = new PropertyDialogAction( getSite(), resourceSelectionProvider );  
        menuManager.add( resourcePropertyAction );
    }
    
    /**
     * Creates the tree-table control for displaying the documents.
     * 
     * @param parent
     */
    private void createTreeControlArea(Composite parent) {
        
        final Tree viewerTree = new Tree(parent, SWT.FULL_SELECTION | SWT.BORDER
                | SWT.H_SCROLL | SWT.V_SCROLL);
        viewerTree.setLayoutData("grow, h 100%!, w 85%!"); //$NON-NLS-1$
        viewerTree.setHeaderVisible(true);
        viewerTree.setLinesVisible(true);
        
        final TableLayout viewerTreeLayout = new TableLayout();
        
        final TreeColumn nameColumn = new TreeColumn(viewerTree, SWT.LEFT, DOCUMENT_INDEX);
        nameColumn.setText(Messages.docView_documentColumn);
        viewerTreeLayout.addColumnData(new ColumnWeightData(DOCUMENT_WEIGHT));

        final TreeColumn detailColumn = new TreeColumn(viewerTree, SWT.CENTER, TYPE_INDEX);
        detailColumn.setText(Messages.docView_typeColumn);
        viewerTreeLayout.addColumnData(new ColumnWeightData(TYPE_WEIGHT));
        
        final TreeColumn descColumn = new TreeColumn(viewerTree, SWT.LEFT, DESCRIPTION_INDEX);
        descColumn.setText(Messages.docView_descriptionColumn);
        viewerTreeLayout.addColumnData(new ColumnWeightData(DESCRIPTION_WEIGHT));
        
        viewerTree.setLayout(viewerTreeLayout); 
        
        viewer = new TreeViewer(viewerTree);
        viewer.setContentProvider(new DocumentViewContentProvider());
        viewer.setLabelProvider(new DocumentViewTableLabelProvider());
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                handleListSelection(event.getSelection());
            }
        });
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                handleListDoubleClick(event.getSelection());
            }
        });
        
        getSite().setSelectionProvider(viewer);
        
    }
    
    /**
     * Creates the button controls panel for actions related to documents.
     * 
     * @param parent
     */
    private void createButtonControlArea(Composite parent) {
        
        final SelectionAdapter btnSelectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                handleBtnSelection(e.widget);
            }
        };
        
        final Composite btnSection = new Composite(parent, SWT.NONE);
        final String btnLayoutConst = "fillx, wrap 1"; //$NON-NLS-1$
        final String btnColConst = ""; //$NON-NLS-1$
        final String btnRowConst = "[][][][][]push[]"; //$NON-NLS-1$
        btnSection.setLayout(new MigLayout(btnLayoutConst, btnColConst, btnRowConst));
        btnSection.setLayoutData("grow"); //$NON-NLS-1$
        
        final String btnLayoutData = "growx"; //$NON-NLS-1$
        
        // attach button
        attachButton = new Button(btnSection, SWT.PUSH);
        attachButton.setText(Messages.docView_attach);
        attachButton.setLayoutData(btnLayoutData);
        attachButton.addSelectionListener(btnSelectionListener);
        
        // link button
        linkButton = new Button(btnSection, SWT.PUSH);
        linkButton.setText(Messages.docView_link);
        linkButton.setLayoutData(btnLayoutData);
        linkButton.addSelectionListener(btnSelectionListener);
        
        // edit button
        editButton = new Button(btnSection, SWT.PUSH);
        editButton.setText(Messages.docView_edit);
        editButton.setLayoutData(btnLayoutData);
        editButton.addSelectionListener(btnSelectionListener);

        // open button
        openButton = new Button(btnSection, SWT.PUSH);
        openButton.setText(Messages.docView_open);
        openButton.setLayoutData(btnLayoutData);
        openButton.addSelectionListener(btnSelectionListener);
        
        // save as button
        saveAsButton = new Button(btnSection, SWT.PUSH);
        saveAsButton.setText(Messages.docView_saveAs);
        saveAsButton.setLayoutData(btnLayoutData);
        saveAsButton.addSelectionListener(btnSelectionListener);
        saveAsButton.setVisible(false); // Re: Brett's comment
        
        // remove button
        removeButton = new Button(btnSection, SWT.PUSH);
        removeButton.setText(Messages.docView_clear);
        removeButton.setLayoutData(btnLayoutData);
        removeButton.addSelectionListener(btnSelectionListener);
        
    }
    
    @Override
    public void setFocus() {
        // Do something
    }

    @Override
    public void dispose() {
        removeWorkbenchListeners();
        super.dispose();
    }

    /**
     * Handles list selection changes. This should refresh the UI with respect to the current
     * selection.
     * 
     * @param selection
     */
    private void handleListSelection(ISelection selection) {
        viewerSelection = null;
        if (selection instanceof StructuredSelection) {
            viewerSelection = (StructuredSelection) selection;
        }
        refreshBtns();
    }
    
    /**
     * Handles list double clicks. This will open the document if the selection is a document and if
     * it is not empty.
     * 
     * @param selection
     */
    private void handleListDoubleClick(ISelection selection) {
        final StructuredSelection structSelection = (StructuredSelection) selection;
        final Object element = structSelection.getFirstElement(); 
        if (element instanceof IDocument) {
            final IDocument doc = (IDocument) element;
            if (!doc.isEmpty()) {
                open();    
            }
        }
    }
    
    /**
     * Refreshes the buttons with respect to the current list selection.
     */
    private void refreshBtns() {
        setBtns(false);
        refreshBtnsOnType();
    }
    
    /**
     * Sets the button's enablement.
     * 
     * @param isEnabled
     */
    private void setBtns(boolean isEnabled) {
        
        openButton.setEnabled(isEnabled);
        attachButton.setEnabled(isEnabled);
        linkButton.setEnabled(isEnabled);
        editButton.setEnabled(isEnabled);
        removeButton.setEnabled(isEnabled);
        
        saveAsButton.setEnabled(isEnabled);
        saveAsAction.setEnabled(isEnabled);
    }

    /**
     * Refreshes the buttons with respect to the selection's type (if {@link IDocument} or
     * {@link IDocumentFolder}), document type (refer to {@link Type}) and document content type
     * (refer to {@link ContentType}). And also with respect to the selection's document source. Refer to
     * {@link IAbstractDocumentSource}.
     */
    private void refreshBtnsOnType() {
        
        if (viewerSelection != null) {
            if (viewerSelection.size() == 1) {
                
                final Object element = viewerSelection.getFirstElement();
                final IAbstractDocumentSource folderSource = getDocumentSource();
                
                attachButton.setEnabled(DocSourceUtils.canAttach(folderSource));
                linkButton.setEnabled(DocSourceUtils.canLink(folderSource));
                
                if (element instanceof IDocument) {
                    
                    final IDocument doc = (IDocument) element;
                    final Type docType = doc.getType();
                    
                    editButton.setEnabled(DocSourceUtils.canUpdate(doc.getSource()));
                    
                    if (Type.ATTACHMENT == docType 
                            && ContentType.FILE == doc.getContentType()) {
                        saveAsButton.setEnabled(true);
                        saveAsAction.setEnabled(true);
                    }
                    
                    if (!doc.isEmpty()) {
                        openButton.setEnabled(true);
                    }
                    
                    final boolean isHotlink = (Type.HOTLINK == docType);
                    removeButton.setText(isHotlink ? Messages.docView_clear : Messages.docView_delete);
                    removeButton.setEnabled(isHotlink ? !doc.isEmpty() : true);
                    if (removeButton.isEnabled()) {
                        removeButton.setEnabled(DocSourceUtils.canRemove(doc.getSource()));    
                    }
                    
                }
                
            } else if (viewerSelection.size() > 1) {
                int count = 0;
                for (Object obj : viewerSelection.toList()) {
                    if (obj instanceof IDocumentFolder) {
                        count++;
                    }
                }
                final boolean isAllFolders = (count == viewerSelection.size());
                removeButton.setEnabled(!isAllFolders);    
            }
        }
        
    }
    
    /**
     * Handles workbench selection changes. This should refresh the current list with respect to the
     * selection. As the workbench selection change event happens ALOT of times, it is being checked
     * here is the selection is the same as the previous one as to reduce the number of list refreshes.
     * 
     * @param selection
     */
    private void handleWorkbenchSelection(ISelection selection) {

        if (selection != null) {
            if (selection instanceof IStructuredSelection) {
                final IStructuredSelection newSelection = (IStructuredSelection) selection;
                if (workbenchSelection == null) {
                    // Go Update!
                    updateList(UpdateType.UPDATE, newSelection);
                } else {
                    final boolean isSameCount = (workbenchSelection.size() == newSelection.size());
                    if (isSameCount) {
                        // Check and update!
                        updateList(UpdateType.CHECK_UPDATE, newSelection);
                    } else {
                        // Go Update!
                        updateList(UpdateType.UPDATE, newSelection);
                    }
                }
                return;
            }
        }
        // Clear list!
        updateList(UpdateType.CLEAR, null);

    }
    
    private enum UpdateType {
        CLEAR, UPDATE, CHECK_UPDATE
    }
    
    /**
     * Creates a job to get the related documents of the selection. This transitions the processing
     * to another thread.
     * 
     * @param option
     * @param selection
     */
    private void updateList(final UpdateType option, final IStructuredSelection selection) {

        final Job getDocsJob = new Job(Messages.DocumentView_retrieveDocsProgressMsg){
            @Override
            protected IStatus run(IProgressMonitor monitor) {

                switch (option) {
                case CLEAR:
                    workbenchSelection = null;
                    itemModel = null;
                    break;
                case UPDATE:
                    workbenchSelection = selection;
                    itemModel = new DocumentItemModel();
                    itemModel.setItems(getItems(monitor));

                    break;
                case CHECK_UPDATE:
                    if (!isSameSelection(selection)) {
                        workbenchSelection = selection;
                        itemModel = new DocumentItemModel();
                        itemModel.setItems(getItems(monitor));
                    }
                    break;
                default:
                    break;
                }

                updateListCallback();
                return Status.OK_STATUS;

            }
        };
        getDocsJob.schedule();

    }
    
    
    /**
     * Updates the document list with the related documents of the selection. This transitions the
     * processing back to the UI thread.
     */
    private void updateListCallback() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (viewer != null) {
                    final Tree viewerTree = viewer.getTree();
                    if (viewerTree != null && !viewerTree.isDisposed()) {
                        viewer.setInput(itemModel);
                        viewer.expandAll();
                        if (isActive) {
                            viewerTree.setFocus();
                        }
                    }
                }
            }
        });
    }
    
    
    /**
     * Checks if the selection is the same as the previous one.
     * 
     * @param newSelection
     * @return true if same, otherwise false
     */
    private boolean isSameSelection(IStructuredSelection newSelection) {
        
        final Iterator<?> wbIterator = workbenchSelection.iterator();
        final Iterator<?> nwIterator = newSelection.iterator();
        while (nwIterator.hasNext() && wbIterator.hasNext()) {
            final Object wbObj = (Object) wbIterator.next();
            final Object nwObj = (Object) nwIterator.next();
            if (!isSameSelectionObj(wbObj, nwObj)) {
                return false;
            }
        }
        
        return true;
    }
    
    
    /**
     * Checks if the selection object is the same as the previous selection's object in the same
     * index.
     * 
     * @param obj1
     * @param obj2
     * @return true if same, otherwise false
     */
    private boolean isSameSelectionObj(Object obj1, Object obj2) {
        
        final IProgressMonitor monitor = new NullProgressMonitor();
        
        final IGeoResource resource1 = toGeoResource(obj1, monitor);
        final IGeoResource resource2 = toGeoResource(obj2, monitor);
        if ((resource1 != null && resource2 != null)
                && (resource1.getID().equals(resource2.getID()))) {

            final FidFilterImpl filter1 = toFilter(obj1, monitor);
            final FidFilterImpl filter2 = toFilter(obj2, monitor);
            
            if (filter1 == null && filter2 == null) {
                return true;
            } else if ((filter1 != null && filter2 != null) && (filter1.equals(filter2))) {
                return true;
            }

        }

        return false;
    }

    void setGeoResourceInternal(IGeoResource geoResource) {
        this.geoResource = geoResource;
        this.resourceSelectionProvider.setSelection( geoResource );
    }

    /**
     * Gets the document items from the current selection.
     * 
     * @return document items
     */
    private List<Object> getItems(IProgressMonitor monitor) {
        
        final List<Object> items = new ArrayList<Object>();
        for (Iterator<?> iterator = workbenchSelection.iterator(); iterator.hasNext();) {
            
            final Object obj = iterator.next();
            setGeoResourceInternal( toGeoResource(obj, monitor) );
            if (geoResource != null) {
                
                feature = getFeature(geoResource, toFilter(obj, monitor));
                if (feature != null) {
                    
                    final IHotlinkSource hotlinkSource = toSource(geoResource, IHotlinkSource.class, monitor);
                    isHotlinkEnabled = hotlinkSource != null && hotlinkSource.isEnabled();
                    final IAttachmentSource attachmentSource = toSource(geoResource, IAttachmentSource.class, monitor);
                    isFeatureEnabled = attachmentSource != null && attachmentSource.isEnabled();
                    
                    if (isFeatureEnabled || isHotlinkEnabled) {
                        final String featureLabel = getFeatureLabel(geoResource, feature);
                        final IDocumentFolder folder = ShpDocFactory.createFolder(feature, featureLabel, attachmentSource);
                        if (isFeatureEnabled) {
                            // Set so that source's document list is same with folder's
                            folder.setDocuments(attachmentSource.getDocuments(feature, monitor));    
                        }
                        if (isHotlinkEnabled) {
                            folder.insertDocuments(hotlinkSource.getDocuments(feature, monitor), 0);
                        }
                        items.add(folder);
                    }
                    
                }
                
                final IDocumentSource docSource = toSource(geoResource, IDocumentSource.class, monitor);
                isResourceEnabled = docSource != null && docSource.isEnabled();
                
                if (isResourceEnabled) {
                    final IDocumentFolder folder = ShpDocFactory.createFolder(null, geoResource.getTitle(), docSource);
                    // Set so that source's document list is same with folder's
                    folder.setDocuments(docSource.getDocuments(monitor));
                    items.add(folder);
                }
                
            }
        }
        
        return items;
        
    }
    
    private static final String FEATURE_LABEL = "FEATURE_LABEL"; //$NON-NLS-1$
    
    /**
     * Gets the feature label by running the feature label expression set for the feature.
     * 
     * @param resource
     * @param feature
     * @return feature label
     */
    private String getFeatureLabel(IGeoResource resource, SimpleFeature feature) {
        
        final String labelExpression = (String) resource.getPersistentProperties().get(
                FEATURE_LABEL);
        
        if (labelExpression != null) {
            try {
                final Expression exp = ECQL.toExpression(labelExpression);
                final String featureLabel = (String) exp.evaluate(feature);
                if (featureLabel != null && featureLabel.trim().length() > 0) {
                    return featureLabel;    
                }
            } catch (CQLException e) {
                e.printStackTrace();
            }    
        }
        
        return feature.getID();
        
    }
    
    /**
     * Resolves the object to a geo resource. This returns null if it is not able to resolve.
     * 
     * @param obj
     * @param monitor
     * @return geo resource
     */
    private IGeoResource toGeoResource(Object obj, IProgressMonitor monitor) {
        if (obj != null) {
            final AdapterUtil adapterUtil = AdapterUtil.instance;
            if (adapterUtil.canAdaptTo(obj, IGeoResource.class)) {
                try {
                    return adapterUtil.adaptTo(IGeoResource.class, obj, monitor);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }       
        }
        return null;
    }

    /**
     * Resolves the object to a document source. This returns null if it is not able to resolve.
     * 
     * @param geoResource
     * @param type
     * @param monitor
     * @return document source
     */
    private <T> T toSource(IGeoResource geoResource, Class<T> type, IProgressMonitor monitor) {
        if (geoResource != null) {
            if (geoResource.canResolve(type)) {
                try {
                    return geoResource.resolve(type, monitor);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }    
        }
        return null;
    }

    /**
     * Resolves the object to a feature ID filter. This returns null if it is unable to resolve.
     * 
     * @param obj
     * @param monitor
     * @return feature ID filter
     */
    private FidFilterImpl toFilter(Object obj, IProgressMonitor monitor) {
        if (obj != null) {
            final AdapterUtil adapterUtil = AdapterUtil.instance;
            try {
                return adapterUtil.adaptTo(FidFilterImpl.class, obj, monitor);
            } catch (ClassCastException e) {
                // Selection does not include a feature
                // e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }    
        }
        return null;
    }
    
    /**
     * Gets the feature from the geo resource given the filter.
     * 
     * @param geoResource
     * @param filter
     * @return feature
     */
    private SimpleFeature getFeature(IGeoResource geoResource, FidFilterImpl filter) {
        if (filter != null) {
            try {
                if (geoResource.canResolve(SimpleFeatureStore.class)) {
                    final SimpleFeatureStore featureSource = geoResource.resolve(SimpleFeatureStore.class,
                            new NullProgressMonitor());
                    final SimpleFeatureCollection featureCollection = featureSource.getFeatures(filter);
                    final SimpleFeatureIterator featureIterator = featureCollection.features();
                    try {
                         if (featureIterator.hasNext()) {
                             return featureIterator.next();
                         }
                    } finally {
                        if (featureIterator != null) {
                            featureIterator.close();
                        }
                    }    
                }
            } catch (IOException e) {
                e.printStackTrace();
            }            
        }
        return null;
    }
    
    /**
     * Handles button selection (click) actions.
     * 
     * @param btn
     */
    private void handleBtnSelection(Widget btn) {
        if (openButton == btn) {
            open();
        } else if (attachButton == btn) {
            attach();
        } else if (linkButton == btn) {
            link();
        } else if (editButton == btn) {
            edit();
        } else if (removeButton == btn) {
            remove();
        } else if (saveAsButton == btn) {
            saveAs();
        }
    }
    
    /**
     * Opens the documents in the current selection.
     */
    private void open() {
        final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        final IDocument doc = (IDocument) selection.getFirstElement();
        if (ContentType.ACTION == doc.getContentType()) {
            openAction(doc);
        } else {
            doc.open();  
        }
    }

    /**
     * Opens the action document - action selection dialog that allows the user to select the action
     * to be opened.
     * 
     * @param doc
     */
    private void openAction(IDocument doc) { 
        
        final IHotlink hotlinkDoc = (IHotlink) doc;
        final List<HotlinkDescriptor> descriptors = hotlinkDoc.getDescriptors();
        
        if (descriptors.size() > 1) {
            final ListDialog dialog = new ListDialog(openButton.getShell());
            dialog.setTitle(Messages.DocumentView_openActionDialogTitle);
            dialog.setMessage(Messages.DocumentView_openActionDialogMessage);
            dialog.setLabelProvider(new LabelProvider() {
                @Override
                public String getText(Object element) {
                    final HotlinkDescriptor descriptor = (HotlinkDescriptor) element;
                    return DocUtils.getLabelAndDescDisplay(descriptor.getLabel(),
                            descriptor.getDescription());
                }
            });
            dialog.setContentProvider(new ArrayContentProvider());
            dialog.setInput(descriptors.toArray());
            dialog.setInitialElementSelections(Collections.singletonList(descriptors.get(0)));
            if (Dialog.OK == dialog.open()) {
                final Object[] results = dialog.getResult();
                if (results != null && results.length > 0) {
                    final HotlinkDescriptor descriptor = (HotlinkDescriptor) results[0];
                    openAction(hotlinkDoc, descriptor);
                }
            }            
        } else if (descriptors.size() == 1) {
            final HotlinkDescriptor descriptor = descriptors.get(0);
            openAction(hotlinkDoc, descriptor);
        }
        
    }
    
    /**
     * Opens the action document. This gets the value from the hotlink document and the action
     * definition from the desriptor.
     * 
     * @param hotlinkDoc
     * @param descriptor
     */
    private void openAction(IHotlink hotlinkDoc, HotlinkDescriptor descriptor) {
        final String action = descriptor.getConfig().replace(DocumentPropertyPage.ACTION_PARAM,
                hotlinkDoc.getContent().toString());
        Program.launch(action);
    }
    
    /**
     * Adds a new document. This opens the {@link DocumentDialog} to allow the user to input details
     * of the document to be added.
     */
    private void attach() {

        final IDocumentFolder folder = getDocumentFolder();
        if (folder != null) {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put(DocumentDialog.P_TYPE, Type.ATTACHMENT);
            params.put(DocumentDialog.P_MODE, Mode.ADD);
            params.put(DocumentDialog.P_TEMPLATES, itemModel.getTemplates(folder));

            final IAbstractDocumentSource source = folder.getSource();
            final boolean isFeatureDoc = (source instanceof IAttachmentSource);
            final DocumentDialog docDialog = openDocDialog(new HashMap<String, Object>(), params, isFeatureDoc);
            if (docDialog != null) {
                addDocument(folder, docDialog.getDocInfo());
            }            
        }
        
    }

    /**
     * Links a new document. This opens the {@link DocumentDialog} to allow the user to input details
     * of the document to be linked.
     */
    private void link() {

        final IDocumentFolder folder = getDocumentFolder();
        if (folder != null) {
            final IAbstractDocumentSource source = folder.getSource();
            
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put(DocumentDialog.P_TYPE, Type.LINKED);
            params.put(DocumentDialog.P_MODE, Mode.ADD);
            params.put(DocumentDialog.P_TEMPLATES, itemModel.getTemplates(folder));
            final List<ContentType> contentTypes = new ArrayList<ContentType>();
            if (DocSourceUtils.canLinkFile(source)) {
                contentTypes.add(ContentType.FILE);
            }
            if (DocSourceUtils.canLinkWeb(source)) {
                contentTypes.add(ContentType.WEB);
            }
            if (contentTypes != null && contentTypes.size() > 0) {
                params.put(DocumentDialog.P_CONTENT_TYPES, contentTypes);    
            }
            
            final boolean isFeatureDoc = (source instanceof IAttachmentSource);
            final DocumentDialog docDialog = openDocDialog(new HashMap<String, Object>(), params, isFeatureDoc);
            if (docDialog != null) { 
                addDocument(folder, docDialog.getDocInfo());
            }            
        }
        
    }
    
    /**
     * Adds a new document to the document folder with the given document info.
     */
    private void addDocument(final IDocumentFolder folder, final DocumentInfo info) {
                
        final Job addDocJob = new Job(Messages.DocumentView_addDocProgressMsg){
            @Override
            protected IStatus run(IProgressMonitor monitor) {

                IDocument doc = null;
                if (folder.getSource() instanceof IDocumentSource) {
                    final IDocumentSource resourceDocSource = (IDocumentSource) folder.getSource();
                    doc = resourceDocSource.add(info, monitor);
                } else if (folder.getSource() instanceof IAttachmentSource) {
                    final IAttachmentSource featureDocSource = (IAttachmentSource) folder.getSource();
                    doc = featureDocSource.add(feature, info, monitor);
                }                
                
                addDocumentCallback(doc);
                return Status.OK_STATUS;

            }
        };
        addDocJob.schedule();

    }
    
    /**
     * Refreshes the documents list after adding a document. This transitions the processing back to
     * the UI thread.
     */
    private void addDocumentCallback(final IDocument doc) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (doc == null) {
                    MessageDialog.openError(attachButton.getShell(),
                            Messages.DocumentView_addDocPopupTitle, Messages.DocumentView_addDocError);
                } else {
                    viewer.refresh();
                    viewer.expandAll();    
                }
            }
        });
    }
    
    /**
     * Edits an existing document. This opens the {@link DocumentDialog} to allow the user to input details
     * of the document to be edited.
     */
    private void edit() {
        
        final Object obj = viewerSelection.getFirstElement();
        if (obj instanceof IDocument) {
            final IDocument doc = (IDocument) obj;
            if (Type.HOTLINK == doc.getType()) {
                editHotlink(doc);
            } else {
                editDocument(doc);
            }
        }
        
    }
    
    /**
     * Edits an existing document.
     * 
     * @param doc
     */
    private void editDocument(final IDocument doc) {
        
        final Map<String, Object> values = new HashMap<String, Object>();
        if (!doc.isEmpty()) {
            values.put(DocumentDialog.V_INFO, doc.getContent().toString());    
        }
        values.put(DocumentDialog.V_CONTENT_TYPE, doc.getContentType());
        values.put(DocumentDialog.V_LABEL, doc.getLabel());
        values.put(DocumentDialog.V_DESCRIPTION, doc.getDescription());
        values.put(DocumentDialog.V_TEMPLATE, doc.isTemplate());
        
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(DocumentDialog.P_TYPE, doc.getType());
        params.put(DocumentDialog.P_MODE, Mode.EDIT);
        params.put(DocumentDialog.P_TEMPLATES, itemModel.getTemplates(doc));
        
        final IAbstractDocumentSource source = doc.getSource();
        final boolean isFeatureDoc = (source instanceof IAttachmentSource);
        final DocumentDialog docDialog = openDocDialog(values, params, isFeatureDoc);
        if (docDialog != null) {
            final Job editDocJob = new Job(Messages.DocumentView_updateDocProgressMsg){
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    boolean isUpdated = false;
                    final DocumentInfo info = docDialog.getDocInfo();
                    if (source instanceof IDocumentSource) {
                        final IDocumentSource resourceDocSource = (IDocumentSource) source;
                        isUpdated = resourceDocSource.update(doc, info, monitor);
                    } else if (source instanceof IAttachmentSource) {
                        final IAttachmentSource featureDocSource = (IAttachmentSource) source;
                        isUpdated = featureDocSource.update(feature, doc, info, monitor);
                    }              
                    editDocumentCallback(isUpdated);
                    return Status.OK_STATUS;
                }
            };
            editDocJob.schedule();
        }
        
    }
    
    /**
     * Refreshes the documents list after updating a document. This transitions the processing back
     * to the UI thread.
     */
    private void editDocumentCallback(final boolean isUpdated) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (!isUpdated) {
                    MessageDialog.openError(attachButton.getShell(),
                            Messages.DocumentView_updateDocPopupTitle, Messages.DocumentView_updateDocError);
                } else {
                    viewer.refresh();
                    viewer.expandAll();    
                }
            }
        });
    }
    
    /**
     * Edits an existing hotlink document.
     * 
     * @param doc
     */
    private void editHotlink(final IDocument doc) {
        
        final IHotlink hotlinkDoc = (IHotlink) doc;
        final String attributeName = hotlinkDoc.getAttributeName();
        
        final Map<String, Object> values = new HashMap<String, Object>();
        if (!doc.isEmpty()) {
            values.put(DocumentDialog.V_INFO, doc.getContent().toString());    
        }
        values.put(DocumentDialog.V_CONTENT_TYPE, doc.getContentType());
        values.put(DocumentDialog.V_ATTRIBUTE, attributeName);
        values.put(DocumentDialog.V_LABEL, doc.getLabel());
        values.put(DocumentDialog.V_DESCRIPTION, doc.getDescription());
        if (ContentType.ACTION == doc.getContentType()) {
            values.put(DocumentDialog.V_ACTIONS, hotlinkDoc.getDescriptors());
        }
        
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(DocumentDialog.P_TYPE, Type.HOTLINK);
        params.put(DocumentDialog.P_MODE, Mode.EDIT);
        if (ContentType.FILE == doc.getContentType()) {
            params.put(DocumentDialog.P_TEMPLATES, itemModel.getTemplates(doc));
        }
        
        final DocumentDialog docDialog = openDocDialog(values, params, true);
        if (docDialog != null) {
            final Job editHotlinkJob = new Job(Messages.DocumentView_updateHotlinkProgressMsg){
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    boolean isUpdated = false;
                    final IHotlinkSource featureDocSource = (IHotlinkSource) doc.getSource();
                    switch (doc.getContentType()) {
                    case FILE:
                        isUpdated = featureDocSource.setFile(feature, attributeName, docDialog.getFileInfo(), monitor);
                        break;
                    case WEB:
                        isUpdated = featureDocSource.setLink(feature, attributeName, docDialog.getUrlInfo(), monitor);
                        break;
                    case ACTION:
                        isUpdated = featureDocSource.setAction(feature, attributeName, docDialog.getInfo(), monitor);
                        break;
                    default:
                        break;
                    }
                    if (isUpdated) {
                        isUpdated = set(attributeName, feature.getAttribute(attributeName), monitor);
                    }
                    itemModel.getClass();
                    editHotlinkCallback(isUpdated);
                    return Status.OK_STATUS;
                }
            };
            editHotlinkJob.schedule();
        }
        
    }
    
    /**
     * Refreshes the documents list after updating a hotlink. This transitions the processing back
     * to the UI thread.
     */
    private void editHotlinkCallback(final boolean isUpdated) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (!isUpdated) {
                    MessageDialog.openError(attachButton.getShell(),
                            Messages.DocumentView_setHotlinkPopupTitle, Messages.DocumentView_setHotlinkError);
                } else {
                    viewer.refresh();
                    viewer.expandAll();    
                }
            }
        });
    }
    
    /**
     * Opens the {@link DocumentDialog} for inputting document metadata.
     * 
     * @param values
     * @param params
     * @return document info
     */
    private DocumentDialog openDocDialog(Map<String, Object> values, Map<String, Object> params,
            boolean isFeatureDoc) {
        if (isFeatureDoc && feature != null) {
            params.put(DocumentDialog.P_FEATURE_NAME, getFeatureLabel(geoResource, feature));
        }
        params.put(DocumentDialog.P_RESOURCE_NAME, geoResource.getTitle());
        final DocumentDialog docDialog = new DocumentDialog(editButton.getShell(), values, params);
        final int result = docDialog.open();
        if (Dialog.OK == result) {
            return docDialog;
        }
        return null;
    }
    
    
    /**
     * Removes the documents in the current selection.
     */
    private void remove() {

        final Map<IAbstractDocumentSource, ArrayList<IDocument>> docMap = new HashMap<IAbstractDocumentSource, ArrayList<IDocument>>();

        final Iterator<?> iterator = viewerSelection.iterator();
        while (iterator.hasNext()) {
            final Object obj = iterator.next();
            if (obj instanceof IDocument) {
                final IDocument doc = (IDocument) obj;
                if (docMap.containsKey(doc.getSource())) {
                    docMap.get(doc.getSource()).add(doc);
                } else {
                    final ArrayList<IDocument> docs = new ArrayList<IDocument>();
                    docs.add(doc);
                    docMap.put(doc.getSource(), docs);
                }
            }
        }

        for (final IAbstractDocumentSource source : docMap.keySet()) {
            final ArrayList<IDocument> docs = docMap.get(source);
            for (final IDocument doc : docs) {
                
                boolean doDelete = true;
                if (Type.ATTACHMENT == doc.getType() && ContentType.FILE == doc.getContentType()
                        && !doc.isEmpty()) {
                    doDelete = MessageDialog.openConfirm(removeButton.getShell(),
                            Messages.docView_deleteAttachConfirmTitle,
                            Messages.docView_deleteAttachConfirmMsg);
                }
                
                if (doDelete) {
                    if (source instanceof IDocumentSource) {
                        final Job removeLayerDocJob = new Job(Messages.DocumentView_removeDocProgressMsg){
                            @Override
                            protected IStatus run(IProgressMonitor monitor) {
                                final IDocumentSource docSource = (IDocumentSource) source;
                                final boolean isRemoved = docSource.remove(doc, monitor);
                                removeDocumentCallback(isRemoved);
                                return Status.OK_STATUS;
                            }
                        };
                        removeLayerDocJob.schedule();
                    } else if (source instanceof IAttachmentSource) {
                        final IAttachmentSource featureDocSource = (IAttachmentSource) source;
                        final Job removeFeatureDocJob = new Job(Messages.DocumentView_removeDocProgressMsg){
                            @Override
                            protected IStatus run(IProgressMonitor monitor) {
                                final boolean isRemoved = featureDocSource.remove(feature, doc, monitor);
                                removeDocumentCallback(isRemoved);
                                return Status.OK_STATUS;
                            }
                        };
                        removeFeatureDocJob.schedule();
                    } else if (source instanceof IHotlinkSource) {
                        final IHotlinkSource featureHotlinkSource = (IHotlinkSource) source;
                        final Job clearHotlinkJob = new Job(Messages.DocumentView_clearHotlinkProgressMsg) {
                            @Override
                            protected IStatus run(IProgressMonitor monitor) {
                                final IHotlink hotlinkDoc = (IHotlink) doc;
                                final String attributeName = hotlinkDoc.getAttributeName();
                                boolean isCleared = featureHotlinkSource.clear(feature,
                                        attributeName, monitor);
                                if (isCleared) {
                                    isCleared = set(attributeName, null, monitor);
                                }
                                clearHotlinkCallback(isCleared);
                                return Status.OK_STATUS;
                            }
                        };
                        clearHotlinkJob.schedule();
                    }                    
                }
                
            }
        }
        
    }
    
    /**
     * Refreshes the documents list after clearing a hotlink. This transitions the processing back
     * to the UI thread.
     */
    private void clearHotlinkCallback(final boolean isCleared) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (!isCleared) {
                    MessageDialog.openError(attachButton.getShell(),
                            Messages.DocumentView_clearHotlinkPopupTitle, Messages.DocumentView_clearHotlinkError);
                } else {
                    viewer.refresh();
                    viewer.expandAll();    
                }
            }
        });
    }
    
    /**
     * Refreshes the documents list after removing a document. This transitions the processing back
     * to the UI thread.
     */
    private void removeDocumentCallback(final boolean isRemoved) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (!isRemoved) {
                    MessageDialog.openError(attachButton.getShell(),
                            Messages.DocumentView_removeDocPopupTitle, Messages.DocumentView_removeDocError);
                } else {
                    viewer.refresh();
                    viewer.expandAll();    
                }
            }
        });
    }
    
    /**
     * Sets the attribute value of the feature.
     * 
     * @param fid
     * @param attributeName
     * @param obj
     */
    private boolean set(final String attributeName, final Object obj, IProgressMonitor monitor) {
        final FeatureId fid = feature.getIdentifier();
        final IMap map = ApplicationGIS.getActiveMap();
        if (map != null) {
            return setOnMap(map, fid, attributeName, obj);
        } else {
            return setOnGeoResource(fid, attributeName, obj, monitor);
        }
    }
    
    /**
     * Sets the attribute value of the feature given that the layer in on the map.
     * 
     * @param map
     * @param fid
     * @param attributeName
     * @param obj
     */
    private boolean setOnMap(final IMap map, final FeatureId fid, final String attributeName,
            final Object obj) {
        final SetAttributeCommand cmd = new SetAttributeCommand(attributeName, obj);
        map.sendCommandASync(cmd);
        return true;
    }
    
    /**
     * Sets the attribute value of the feature directly to the geoResource.
     * 
     * @param fid
     * @param attributeName
     * @param obj
     */
    private boolean setOnGeoResource(final FeatureId fid, final String attributeName,
            final Object obj, IProgressMonitor monitor) {

        try {
            if (geoResource.canResolve(SimpleFeatureStore.class)) {
                final Filter filter = CommonFactoryFinder.getFilterFactory2().id(fid);
                final SimpleFeatureStore featureStore = geoResource.resolve(
                        SimpleFeatureStore.class, monitor);
                featureStore.modifyFeatures(attributeName, obj, filter);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;

    }
    
    /**
     * Saves the current file document's file as another file.
     */
    private void saveAs() {

        final Object element = viewerSelection.getFirstElement();
        if (element instanceof IAttachment) {
            final IAttachment attachDoc = (IAttachment) element;
            final File file = (File) attachDoc.getContent();
            
            final FileDialog dialog = new FileDialog(saveAsButton.getShell(), SWT.SAVE);
            dialog.setText(Messages.DocumentView_saveAsDialogTitle);
            dialog.setOverwrite(true);
            dialog.setFileName(DocUtils.getSaveAsFilename(file));
            final String filePath = dialog.open();
            if (filePath != null) {
                final File newFile = new File(DocUtils.cleanFilename(filePath, file));
                final boolean isSaved = attachDoc.saveAs(newFile);
                if (isSaved) {
                    // Program.launch(file.getAbsolutePath());
                    MessageDialog.openInformation(saveAsButton.getShell(),
                            Messages.DocumentView_saveAsSuccessDialogTitle,
                            Messages.DocumentView_saveAsSuccessDialogMsg);
                } else {
                    MessageDialog.openError(saveAsButton.getShell(),
                            Messages.DocumentView_saveAsErrorDialogTitle,
                            Messages.DocumentView_saveAsErrorDialogMsg);
                }
            }
        }

    }
    
    // Utility inner classes
    
    /**
     * The label provider for the document item tree viewer in the {@link DocumentView}.
     */
    private class DocumentViewTableLabelProvider implements ITableLabelProvider {

        private DocumentImageProvider imageProvider;
        
        public DocumentViewTableLabelProvider() {
            imageProvider = new DocumentImageProvider();
        }
        
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            switch (columnIndex) {
            case DOCUMENT_INDEX:
                if (element instanceof IDocumentFolder) {
                    return imageProvider.createFolderImage();
                } else if (element instanceof IDocument) {
                    final IDocument doc = (IDocument) element;                
                    return imageProvider.createDocumentImage(doc);
                }
                return imageProvider.createDefaultImage();
            }
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof IDocumentFolder) {
                final IDocumentFolder folder = (IDocumentFolder) element;
                switch (columnIndex) {
                case DOCUMENT_INDEX:
                    return DocUtils.toCamelCase(folder.getName());
                }
            } else if (element instanceof IDocument) {
                final IDocument doc = (IDocument) element;
                switch (columnIndex) {
                case DOCUMENT_INDEX:
                    return DocUtils.getDocStr(doc);
                case TYPE_INDEX:
                    if (doc.isTemplate()) {
                        return Messages.DocumentView_templateLbl;
                    }
                    return DocUtils.toCamelCase(doc.getContentType().toString());
                case DESCRIPTION_INDEX:
                    final String description = doc.getDescription();
                    if (description != null) {
                        return description;    
                    }
                    return ""; //$NON-NLS-1$
                }                
            }
            return null;
        }

        @Override
        public void dispose() {
            // Nothing
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        @Override
        public void addListener(ILabelProviderListener listener) {
            // Nothing
        }
        
        @Override
        public void removeListener(ILabelProviderListener listener) {
            // Nothing
        }

    }

    /**
     * The content provider for the document item tree viewer in the {@link DocumentView}.
     */
    private class DocumentViewContentProvider implements ITreeContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof DocumentItemModel) {
                final DocumentItemModel itemModel = (DocumentItemModel) inputElement;
                return itemModel.getItems().toArray(new Object[0]);    
            }
            return null;
        }

        @Override
        public Object[] getChildren( Object element ) {
            if (element instanceof IDocumentFolder) {
                final IDocumentFolder folder = (IDocumentFolder) element;
                return folder.getItems().toArray(new Object[0]);
            }
            return null;
        }

        @Override
        public Object getParent( Object element ) {
            return null;
        }

        @Override
        public boolean hasChildren( Object element ) {
            if (element instanceof IDocumentFolder) {
                final IDocumentFolder folder = (IDocumentFolder) element;
                return folder.getItems().size() > 0;
            }
            return false;
        }

        @Override
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
            // Do input change stuff here
        }
        
        @Override
        public void dispose() {
            // Do dispose stuff here
        }
        
    }
 
    /**
     * The data model for the document item tree viewer in the {@link DocumentView}.
     */
    private class DocumentItemModel {
        
        private List<Object> items;
        
        public DocumentItemModel() {
            items = new ArrayList<Object>();
        }
        
        public List<Object> getItems() {
            return items;
        }
        
        public void setItems(List<Object> items) {
            this.items = items;
        }
        
        public IDocumentFolder getFolder(IDocument doc) {
            for (Object item : items) {
                if (item instanceof IDocumentFolder) {
                    final IDocumentFolder folder = (IDocumentFolder) item;
                    for (IDocument folderDoc : folder.getDocuments()) {
                        if (folderDoc.equals(doc)) {
                            return folder;
                        }
                    }
                    // if (folder.getSource().equals(doc.getSource())) {
                    // return folder;
                    // }
                }
            }
            return null;
        }
     
        public List<IDocument> getTemplates(IDocumentFolder folder) {
            return getTemplates(folder.getSource(), folder, null);
        }
        
        public List<IDocument> getTemplates(IDocument doc) {
            return getTemplates(doc.getSource(), getFolder(doc), doc);
        }
        
        private List<IDocument> getTemplates(IAbstractDocumentSource source,
                IDocumentFolder folder, IDocument doc) {
            if (source instanceof IDocumentSource) {
                return getTemplatesInternal(folder, doc);
            } else {
                return getTemplatesInternal(doc);
            }
        }
        
        private List<IDocument> getTemplatesInternal(IDocumentFolder folder, IDocument refDoc) {
            
            final List<IDocument> templates = new ArrayList<IDocument>();
            for (IDocument doc : folder.getDocuments()) {
                if (!doc.equals(refDoc) && doc.isTemplate()) {
                    templates.add(doc);
                }
            }
            return templates;
        }
        
        private List<IDocument> getTemplatesInternal(IDocument refDoc) {
            final List<IDocument> templates = new ArrayList<IDocument>();
            for (Object item : items) {
                if (item instanceof IDocumentFolder) {
                    final IDocumentFolder folder = (IDocumentFolder) item;
                    templates.addAll(getTemplatesInternal(folder, refDoc));
                }
            }
            return templates;
        }
        
    }
 
    /**
     * Gets the document folder containing the current selection. Or the current selection if it is
     * a folder.
     * 
     * @return document folder
     */
    private IDocumentFolder getDocumentFolder() {
        if (viewerSelection != null) {
            final Object obj = viewerSelection.getFirstElement();
            IDocumentFolder folder = null;
            if (obj instanceof IDocumentFolder) {
                folder = (IDocumentFolder) obj;
            } else if (obj instanceof IDocument) {
                folder = itemModel.getFolder((IDocument) obj);
            }
            return folder;    
        }
        return null;
    }
    
    /**
     * Gets the document source of the folder containing the current selection. This should also be
     * the same document source of the current selection if it is not a folder.
     * 
     * @return document source
     */
    private IAbstractDocumentSource getDocumentSource() {
        final IDocumentFolder folder = getDocumentFolder();
        if (folder != null) {
            return folder.getSource();    
        }
        return null;
    }
    
    /**
     * Selection provider that reports back the {@link #geoReosurce}.
     */
    public class ResourceSelectionProvider implements ISelectionProvider {
        protected ListenerList listeners;
        ISelection selection;
        
        public ResourceSelectionProvider() {
            listeners = new ListenerList( ListenerList.IDENTITY );
            selection = StructuredSelection.EMPTY;
        }

        @Override
        public void addSelectionChangedListener(ISelectionChangedListener listener) {
            listeners.add( listener );
        }
        @Override
        public void removeSelectionChangedListener(ISelectionChangedListener listener) {
            listeners.remove(listener);
        }

        @Override
        public ISelection getSelection() {
            return this.selection;
        }
        public void setSelection(IGeoResource resource){
            if ( resource != null ){
                this.selection = new StructuredSelection( DocumentView.this.geoResource );
            }
            else {
                this.selection = StructuredSelection.EMPTY; // no dice!
            }            
        }
        
        @Override
        public void setSelection(ISelection selection) {
            this.selection = selection;
            fire();
        }
        
        private void fire() {
            SelectionChangedEvent event = null;
            for( Object item : listeners.getListeners() ){
                ISelectionChangedListener listener = (ISelectionChangedListener) item;
                if( event == null ){
                    event = new SelectionChangedEvent( this, this.selection);
                }
                listener.selectionChanged( event );                
            }
        }
    }
}
