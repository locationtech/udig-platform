/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011-2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tool.info.documents;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.DocumentFolder;
import net.refractions.udig.catalog.FileDocument;
import net.refractions.udig.catalog.IAbstractDocumentSource;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IDocumentFolder;
import net.refractions.udig.catalog.IDocumentItem;
import net.refractions.udig.catalog.IDocumentSource;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IHotlinkSource;
import net.refractions.udig.catalog.URLDocument;
import net.refractions.udig.core.AdapterUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.geotools.filter.FidFilterImpl;
import org.geotools.filter.identity.FeatureIdImpl;

/**
 * The Document View provides a user interface to view, edit and access attached documents. In this
 * current release, the view uses shapefile implementations of {@link IDocumentSource} and
 * {@link IHotlinkSource} as its document sources.
 * 
 * @author paul.pfeiffer
 * @author Naz Chan
 */
public class DocumentView extends ViewPart {
    
    private TreeViewer viewer;
    private Button openButton;
    private Button attachButton;
    private Button linkButton;
    private Button removeButton;

    private FidFilterImpl filter;
    private IStructuredSelection viewerSelection;
    private IStructuredSelection workbenchSelection;
    private DocumentItemModel itemModel;
    
    private ISelectionChangedListener listSelectionListener;
    private SelectionAdapter btnSelectionListener;
    private ISelectionListener workbenchSelectionListener;

    public DocumentView() {
        this.itemModel = new DocumentItemModel();
    }

    @Override
    public void createPartControl(final Composite viewParent) {
        
        setPartName(Messages.docView_name);
        
        final FormToolkit toolkit = new FormToolkit(viewParent.getDisplay());
        final ScrolledForm scrolledForm = toolkit.createScrolledForm(viewParent);
        final Form form = scrolledForm.getForm();
        form.setText(Messages.docView_name);
        form.setSeparatorVisible(true);
        
        final Composite parent = scrolledForm.getBody();
        
        final String treeLayoutConst = "fillx, wrap 2"; //$NON-NLS-1$
        final String treeColConst = "[85%][15%]"; //$NON-NLS-1$
        final String treeRowConst = "[top][top]"; //$NON-NLS-1$
        parent.setLayout(new MigLayout(treeLayoutConst, treeColConst, treeRowConst));
        
        listSelectionListener = new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                handleListSelection(event.getSelection());
            }
        };
        
        // add table or tree viewer
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL
                | SWT.FULL_SELECTION | SWT.BORDER);
        viewer.setContentProvider(new DocumentViewContentProvider());
        viewer.setLabelProvider(new DocumentViewLabelProvider());
        viewer.setUseHashlookup(true);
        viewer.getTree().setLayoutData("grow, push"); //$NON-NLS-1$
        viewer.addSelectionChangedListener(listSelectionListener);
        
        btnSelectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                handleBtnSelection(e.widget);
            }
        };
        
        final String btnLayoutData = "growx"; //$NON-NLS-1$
        final String btnSubLayoutConst = "insets 0, fillx, wrap 1"; //$NON-NLS-1$
        final String btnLayoutConst = "insets 0, fillx, wrap 1, gapy 20px!"; //$NON-NLS-1$
        final String btnColConst = ""; //$NON-NLS-1$
        final String btnRowConst = ""; //$NON-NLS-1$
        
        final Composite btnSection = toolkit.createComposite(parent);
        btnSection.setLayout(new MigLayout(btnLayoutConst, btnColConst, btnRowConst));
        btnSection.setLayoutData(btnLayoutData);
        
        final Composite btnTopSection = toolkit.createComposite(btnSection);
        btnTopSection.setLayout(new MigLayout(btnSubLayoutConst, btnColConst, btnRowConst));
        btnTopSection.setLayoutData(btnLayoutData);
        // open button
        openButton = toolkit.createButton(btnTopSection, Messages.docView_open, SWT.PUSH);
        openButton.setLayoutData(btnLayoutData);
        openButton.addSelectionListener(btnSelectionListener);
        
        final Composite btnMidSection = toolkit.createComposite(btnSection);
        btnMidSection.setLayout(new MigLayout(btnSubLayoutConst, btnColConst, btnRowConst));
        btnMidSection.setLayoutData(btnLayoutData);
        // attach button
        attachButton = toolkit.createButton(btnMidSection, Messages.docView_attach, SWT.PUSH);
        attachButton.setLayoutData(btnLayoutData);
        attachButton.addSelectionListener(btnSelectionListener);
        // link button
        linkButton = toolkit.createButton(btnMidSection, Messages.docView_link, SWT.PUSH);
        linkButton.setLayoutData(btnLayoutData);
        linkButton.addSelectionListener(btnSelectionListener);
        
        final Composite btnBotSection = toolkit.createComposite(btnSection);
        btnBotSection.setLayout(new MigLayout(btnSubLayoutConst, btnColConst, btnRowConst));
        btnBotSection.setLayoutData(btnLayoutData);
        // remove button
        removeButton = toolkit.createButton(btnBotSection, Messages.docView_delete, SWT.PUSH);
        removeButton.setLayoutData(btnLayoutData);
        removeButton.addSelectionListener(btnSelectionListener);
        
        refreshBtns();
        
        // Add workbench selection lister
        workbenchSelectionListener = new ISelectionListener() {
            @Override
            public void selectionChanged(IWorkbenchPart part, ISelection selection) {
                handleWorkbenchSelection(selection);
            }
        };
        getSite().getWorkbenchWindow().getSelectionService()
                .addPostSelectionListener(workbenchSelectionListener);
        
    }

    @Override
    public void setFocus() {
        // Do something
    }

    @Override
    public void dispose() {
        if (workbenchSelectionListener != null) {
            getSite().getWorkbenchWindow().getSelectionService()
                    .removePostSelectionListener(workbenchSelectionListener);
            workbenchSelectionListener = null;
        }
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
     * Refreshes the buttons with respect to the current list selection.
     */
    private void refreshBtns() {

        openButton.setEnabled(false);
        removeButton.setEnabled(false);
        attachButton.setEnabled(false);
        linkButton.setEnabled(false);

        if (viewerSelection != null) {
            if (viewerSelection.size() == 1) {
                final Object firstObj = viewerSelection.getFirstElement();
                if (firstObj instanceof IDocumentFolder) {
                    openButton.setEnabled(false);
                    removeButton.setEnabled(false);
                    final DocumentFolder folder = (DocumentFolder) firstObj;
                    final boolean isDocSource = (folder.getSource() instanceof IDocumentSource);
                    attachButton.setEnabled(isDocSource);
                    linkButton.setEnabled(isDocSource);
                } else if (firstObj instanceof IDocument) {
                    final IDocument doc = (IDocument) firstObj;
                    switch (doc.getType()) {
                    case FILE:
                        attachButton.setEnabled(true);
                        break;
                    case WEB:
                        linkButton.setEnabled(true);
                        break;
                    default:
                        break;
                    }
                    if (!doc.isEmpty()) {
                        openButton.setEnabled(true);
                        removeButton.setEnabled(true);
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
                openButton.setEnabled(!isAllFolders);
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
                    updateList(UPD_OPTIONS.UPDATE, newSelection);
                } else {
                    final boolean isSameCount = (workbenchSelection.size() == newSelection.size());
                    if (isSameCount) {
                        // Check and update!
                        updateList(UPD_OPTIONS.CHECK_UPDATE, newSelection);
                    } else {
                        // Go Update!
                        updateList(UPD_OPTIONS.UPDATE, newSelection);
                    }
                }
                return;
            }
        }
        
        // Clear list!        
        updateList(UPD_OPTIONS.CLEAR, null);
        
    }
    
    private enum UPD_OPTIONS {
        CLEAR, UPDATE, CHECK_UPDATE
    }
    
    /**
     * Updates the list with respect to the current selection.
     * 
     * @param option
     * @param newSelection
     */
    private void updateList(UPD_OPTIONS option, IStructuredSelection newSelection) {

        switch (option) {
        case CLEAR:
            workbenchSelection = null;
            itemModel = null;
            viewer.setInput(itemModel);
            break;
        case UPDATE:
            workbenchSelection = newSelection;
            itemModel = new DocumentItemModel();
            itemModel.setItems(getItems());
            viewer.setInput(itemModel);
            break;
        case CHECK_UPDATE:
            if (!isSameSelection(newSelection)) {
                itemModel = new DocumentItemModel();
                workbenchSelection = newSelection;
                itemModel.setItems(getItems());
                viewer.setInput(itemModel);
            }
            break;
        default:
            break;
        }
        
        viewer.expandAll();
        
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
    
    /**
     * Gets the document items from the current selection.
     * 
     * @return document items
     */
    private List<IDocumentItem> getItems() {
        
        final List<IDocumentItem> items = new ArrayList<IDocumentItem>();
        final NullProgressMonitor monitor = new NullProgressMonitor();
        for (Iterator<?> iterator = workbenchSelection.iterator(); iterator.hasNext();) {
            
            final Object obj = iterator.next();
            final IGeoResource geoResource = toGeoResource(obj, monitor);
            if (geoResource != null) {
                
                final IHotlinkSource hotlinkSource = toHotlinkSource(obj, geoResource, monitor);
                if (hotlinkSource != null && filter != null) {
                    final String fid = (String) filter.getIDs().iterator().next();
                    items.add(hotlinkSource.getDocumentsInFolder(fid, new FeatureIdImpl(fid)));
                }
                
                final IDocumentSource docSource = toSource(geoResource, IDocumentSource.class, monitor);
                if (docSource != null) {
                    items.add(docSource.getDocumentsInFolder(geoResource.getTitle()));
                }                
            }
        }
        
        return items;
        
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
                    return adapterUtil.adaptTo(IGeoResource.class, obj, new NullProgressMonitor());
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
     * Resolves the object to a hotlink document source. This returns null if it is unable to
     * resolve.
     * 
     * @param obj
     * @param geoResource
     * @param monitor
     * @return hotlink document source
     */
    private IHotlinkSource toHotlinkSource(Object obj, IGeoResource geoResource, IProgressMonitor monitor) {
        final FidFilterImpl filter = toFilter(obj, monitor);
        if (filter != null) {
            this.filter = filter; 
            return toSource(geoResource, IHotlinkSource.class, monitor);
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
        } else if (removeButton == btn) {
            remove();
        }
    }
    
    /**
     * Opens the documents in the current selection.
     */
    private void open() {

        final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        final Iterator<?> iterator = selection.iterator();
        while (iterator.hasNext()) {
            final Object next = iterator.next(); 
            if (next instanceof IDocument) {
                final IDocument document = (IDocument) next;
                document.open();    
            }
        }
        
    }

    /**
     * Opens the file selection dialog to allow the user to choose file(s) to attach and then
     * attaches the selected file(s).
     */
    private void attach() {
        
        final Object obj = viewerSelection.getFirstElement();
        boolean isFolder = false;
        if (obj instanceof IDocumentFolder) {
            isFolder = true;
        }
        
        final List<File> fileList = openFileDialog(isFolder);
        if (fileList != null) {
            if (isFolder) { 
                
                final DocumentFolder folder = (DocumentFolder) obj;
                final IDocumentSource docSource = (IDocumentSource) folder.getSource();
                
                final List<IDocument> docs = docSource.addFiles(fileList);
                if (docs.size() != fileList.size()) {
                    MessageDialog.openInformation(attachButton.getShell(),
                            Messages.docView_attachFiles, Messages.docView_errFileExistMulti);
                }
                
            } else {
                
                final IDocument doc = (IDocument) obj;
                final IAbstractDocumentSource source = doc.getSource();
                final File file = fileList.get(0);
                
                if (source instanceof IDocumentSource) {
                    final FileDocument fileDoc = (FileDocument) obj;
                    final boolean isUpdateSuccess = ((IDocumentSource) source).updateFile(fileDoc, file);
                    if (!isUpdateSuccess) {
                        MessageDialog.openInformation(attachButton.getShell(),
                                Messages.docView_attachFile, Messages.docView_errFileExistSingle);
                    }
                } else if (source instanceof IHotlinkSource) {
                    
                    final String fid = (String) filter.getIDs().iterator().next();
                    final IHotlinkSource hotlinkSource = (IHotlinkSource) source;
                    hotlinkSource.setFile(new FeatureIdImpl(fid), doc.getAttributeName(), file);
                    
                    final FileDocument fileDoc = (FileDocument) obj;
                    fileDoc.setFile(file);
                    
                }
                
            }
            viewer.refresh();
            viewer.expandAll();
        }
        
    }

    /**
     * Opens the file selection dialog.
     * 
     * @param isMultiSelect
     * @return list of selected files
     */
    private List<File> openFileDialog(boolean isMultiSelect) {
        
        final int style = isMultiSelect ? (SWT.SAVE | SWT.MULTI) : SWT.SAVE; 
        final FileDialog fileDialog = new FileDialog(attachButton.getShell(), style);
        fileDialog.setText(Messages.docView_openDialogTitle);
        
        final String hasSelection = fileDialog.open();
        if (hasSelection != null) {
            final String[] filenames = fileDialog.getFileNames();
            if (filenames != null && filenames.length > 0) {
                final List<File> fileList = new ArrayList<File>();
                final String filePath = fileDialog.getFilterPath();
                for (int i = 0, n = filenames.length; i < n; i++) {
                    String filename = filePath;
                    if (filePath.charAt(filePath.length() - 1) != File.separatorChar) {
                        filename += File.separatorChar;
                    }
                    filename += filenames[i];
                    fileList.add(new File(filename));
                }
                return fileList;
            }
        }
        
        return null;
    }
    
    /**
     * Opens the link input dialog to allow the user to input a URL to link and then links the URL.
     */
    private void link() {
        
        final Object obj = viewerSelection.getFirstElement();
        boolean isFolder = true;
        String defaultValue = ""; //$NON-NLS-1$
        if (obj instanceof IDocument) {
            final IDocument doc = (IDocument) obj;
            isFolder = false;
            if (!doc.isEmpty()) {
                defaultValue = doc.getURI().toString();    
            }
        }
        
        final String urlSpec = openLinkDialog(defaultValue);
        if (urlSpec != null) {

            try {
                
                if (isFolder) {
                    
                    final DocumentFolder folder = (DocumentFolder) obj;
                    final IDocumentSource docSource = (IDocumentSource) folder.getSource();
                    if (docSource.addLink(new URL(urlSpec)) == null) {
                        MessageDialog.openInformation(attachButton.getShell(),
                                Messages.docView_linkURL, Messages.docView_errURLExist);
                    }
                    
                } else {

                    final IDocument doc = (IDocument) obj;
                    final IAbstractDocumentSource source = doc.getSource();
                    final URL url = new URL(urlSpec);
                    
                    if (source instanceof IDocumentSource) {
                        final URLDocument urlDoc = (URLDocument) doc;
                        if (!((IDocumentSource) source).updateLink(urlDoc, url)) {
                            MessageDialog.openInformation(attachButton.getShell(),
                                    Messages.docView_linkURL, Messages.docView_errURLExist);
                        }
                    } else if (source instanceof IHotlinkSource) {
                        
                        final String fid = (String) filter.getIDs().iterator().next();
                        final IHotlinkSource hotlinkSource = (IHotlinkSource) source;
                        hotlinkSource.setLink(new FeatureIdImpl(fid), doc.getAttributeName(), url);
                        
                        final URLDocument urlDoc = (URLDocument) doc;
                        urlDoc.setUrl(url);
                        
                    }
                    
                }

                viewer.refresh();
                viewer.expandAll();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        
    }

    /**
     * Opens the link input dialog.
     * 
     * @param defaultValue
     * @return url string
     */
    private String openLinkDialog(String defaultValue) {

        final InputDialog inputDialog = new InputDialog(linkButton.getShell(), Messages.docView_linkDialogTitle,
                Messages.docView_linkDialogHeader, defaultValue, new IInputValidator() {
                    @Override
                    public String isValid(String newText) {
                        if (newText == null || newText.length() == 0) {
                            return Messages.docView_errEmpty;
                        } else {
                            try {
                                @SuppressWarnings("unused")
                                final URL url = new URL(newText);
                            } catch (MalformedURLException e) {
                                return Messages.docView_errInvalidURL;
                            }
                        }
                        return null;
                    }
                });
        
        if (inputDialog.open() == Window.OK) {
            return inputDialog.getValue();
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

        for (IAbstractDocumentSource source : docMap.keySet()) {
            
            final ArrayList<IDocument> docs = docMap.get(source);
            
            if (source instanceof IDocumentSource) {
                ((IDocumentSource) source).remove(docs);
            } else if (source instanceof IHotlinkSource) {
                
                final IHotlinkSource hotlinkSource = (IHotlinkSource) source;
                final String fid = (String) filter.getIDs().iterator().next();
                for (IDocument doc : docs) {
                                        
                    hotlinkSource.remove(new FeatureIdImpl(fid), doc.getAttributeName());
                    
                    if (doc instanceof FileDocument) {
                        final FileDocument fileDoc = (FileDocument) doc;
                        fileDoc.setFile(null);
                    } else if (doc instanceof URLDocument) {
                        final URLDocument fileDoc = (URLDocument) doc;
                        fileDoc.setUrl(null);
                    }
                    
                }
                
            }

        }
        viewer.refresh();
        viewer.expandAll();
    }
    
    // Utility inner classes

    /**
     * The label provider for the document item tree viewer in the {@link DocumentView}.
     */
    private class DocumentViewLabelProvider extends LabelProvider {

        public DocumentViewLabelProvider() {
            // Do nothing
        }

        @Override
        public String getText(Object element) {
            if (element instanceof IDocumentItem) {
                final IDocumentItem item = (IDocumentItem) element;
                return item.getName();                
            }
            return super.getText(element);
        }

        @Override
        public Image getImage(Object obj) {
            
            if (obj instanceof IDocumentFolder) {
                return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(ISharedImages.IMG_OBJ_FOLDER);
            } else if (obj instanceof FileDocument) {
                return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(ISharedImages.IMG_OBJ_FILE);
            } else if (obj instanceof URLDocument) {
                return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(ISharedImages.IMG_OBJ_ELEMENT);
            }
            
            return PlatformUI.getWorkbench().getSharedImages()
                    .getImage(ISharedImages.IMG_OBJ_ELEMENT);
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
                return ((IDocumentFolder) element).getItems().size() > 0;
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
        
        private List<IDocumentItem> items;
        
        public DocumentItemModel() {
            items = new ArrayList<IDocumentItem>();
        }
        
        public List<IDocumentItem> getItems() {
            return items;
        }
        
        public void setItems(List<IDocumentItem> items) {
            this.items = items;
        }
        
    }
    
}
