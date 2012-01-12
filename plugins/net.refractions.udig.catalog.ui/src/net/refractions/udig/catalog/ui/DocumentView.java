/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
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
package net.refractions.udig.catalog.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.DocumentFactory;
import net.refractions.udig.catalog.DocumentFolder;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IDocumentSource;
import net.refractions.udig.catalog.IFolder;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.core.AdapterUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.filter.identity.FeatureId;

/**
 * The Document View is the UI for viewing and editing the document list 
 * associated with a Document Source.
 * 
 * @author paul.pfeiffer
 * @version 1.3.1
 */
public class DocumentView extends ViewPart {

    /*
     * internal workbench selection listener class that allows us to hook into the 
     */
    private final class WorkbenchSelectionListener implements ISelectionListener {
        public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
            if( selection instanceof IStructuredSelection ){
                updateSelection( (IStructuredSelection) selection );
            }
            else {
                updateSelection( null );
            }
        }

    }

    /**
     * Provides string labels for every row attribute (called once for every row / feature)
     * 
     * @author paul.pfeiffer
     */
    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

        public ViewLabelProvider() {
        }
        /*
         * (non-Javadoc)
         * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            if (element instanceof IFolder) {
                IFolder folder = (IFolder) element;
                return folder.getName();
            }
            return super.getText(element);
        }
        @Override
        public String getColumnText( Object obj, int index ) {
            if (obj instanceof IFolder) {
                IFolder folder = (IFolder) obj;
                return folder.getName();
            }
            return obj == null ? "" : obj.toString();
        }

        @Override
        public Image getColumnImage( Object obj, int index ) {
            return null;
        }

        @Override
        public Image getImage( Object obj ) {
            if (obj instanceof IDocument) {
                return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(ISharedImages.IMG_OBJ_FILE);
            }
            if (obj instanceof IFolder) {
                return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(ISharedImages.IMG_OBJ_FOLDER);
            }
            // descriptor = TreeViewerPlugin.getImageDescriptor("book.gif");
            return PlatformUI.getWorkbench().getSharedImages()
                    .getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
    }

    private ISelectionListener selectionListener;
    private IDocumentSource documentSource;
    //private DocumentContentProvider contents;
    private DocumentFolder folder = null;

    private TreeViewer viewer;
    private ListViewer listViewer;
    
    private Button removeButton;

    private Button addButton;
    private Shell addDialog;
    private Label addLabel;
    private Text addText;
    private Button okButton;
    private Button cancelButton;
    
    private Button openButton;

    /*
     * handles any events relating to the action buttons on the document view
     */
    private Listener actionListener = new Listener() {
        public void handleEvent(Event event) {
            if (event.widget == addButton && documentSource != null) {
                addDialog.open();
            } 
            else if (event.widget == removeButton && documentSource != null) {
                IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
                IDocument document = (IDocument) selection.getFirstElement();
                documentSource.remove(document);
                listViewer.setInput(documentSource.findDocuments());
            }
            else if (event.widget == openButton && documentSource != null) {
                IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
                IDocument document = (IDocument) selection.getFirstElement();
                documentSource.open(document);
            }
        }
    };

    /*
     * handles any events relating to the add document dialog
     */
    private Listener addDocumentListener = new Listener() {
        public void handleEvent(Event event) {
            if (event.widget == okButton && documentSource != null) {
                String documentString = addText.getText();
                DocumentFactory factory = new DocumentFactory();
                IDocument document = factory.create(documentString);
                if (document != null) {
                    documentSource.add(document);
                }
                listViewer.setInput(documentSource.findDocuments());
            } 
            addDialog.close();
        }
    };
    
    public DocumentView() {
    }
    
    /**
     * 
     * @param selection
     */
    protected void updateSelection( IStructuredSelection selection ) {
        
        if( selection == null || selection.isEmpty() ){
            return;
        }
        
        listViewer.setInput(null);
        viewer.setInput(null);
        
        AdapterUtil adapterUtil = AdapterUtil.instance;
        
        for( Iterator<?> iterator=selection.iterator(); iterator.hasNext(); ){            
            Object object = iterator.next();
            
            NullProgressMonitor monitor = new NullProgressMonitor();
            List<IDocument> documents = new ArrayList<IDocument>();
            
            if (adapterUtil.canAdaptTo(object, IGeoResource.class)) {
                try {
                    IGeoResource resource = adapterUtil.adaptTo(IGeoResource.class, object, monitor);
                    if (resource.canResolve(IDocumentSource.class)) {
                        IProgressMonitor progressMonitor = new NullProgressMonitor();
                        documentSource = resource.resolve(IDocumentSource.class, progressMonitor);
                        if (documentSource != null) {
                            documents = documentSource.findDocuments();
                            
                            if (folder == null) {
                                folder = new DocumentFolder(documentSource);
                            }
                            
                            // SimpleFeatureCollection
                            if (adapterUtil.canAdaptTo(object, SimpleFeatureCollection.class)) {
                                SimpleFeatureCollection collection = adapterUtil.adaptTo(SimpleFeatureCollection.class, object, monitor);
                                if( collection != null){
                                    // TODO: this must be in a job as we are doing IO
                                    final List<FeatureId> fids = new ArrayList<FeatureId>();
                                    collection.accepts(new FeatureVisitor(){
                                        @Override
                                        public void visit( Feature feature ) {
                                            fids.add( feature.getIdentifier());
                                        }
                                    }, null);
                                    System.out.println( fids );
                                    if (fids.size() == 1) {
                                        String fid = fids.get(0).toString();
                                        folder.setSelectedFeature(fid);
                                    }
                                }
                            }
                            else {
                                folder.setSelectedFeature(null);
                            }
                            viewer.setInput(folder);
                        }
                    }
                    else {
                        documentSource = null;
                    }
                    System.out.println("geo resource");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }            
            
            listViewer.setInput(documents);
            
        }
    }


    @Override
    public void init( IViewSite site, IMemento memento ) throws PartInitException {
        super.init(site, memento);
        
        // this is where you read your memento to remember
        // anything the user told you from last time
        // this.addAOIStrategy();
        if( memento != null ){
//            String id = memento.getString("AOI");
//            IAOIService service = PlatformGIS.getAOIService();
//            this.initialStrategy = service.findProxy(id);
        }
    }
    
    @Override
    public void createPartControl( Composite parent ) {
        //add table or tree viewer
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL
                | SWT.FULL_SELECTION | SWT.BORDER);

        Tree viewerTree = viewer.getTree();
        viewerTree.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 2, 2));
        // config table
        viewerTree.setHeaderVisible(false);
        viewerTree.setLinesVisible(true);
        viewerTree.setSize(500, 100);

        viewer.setContentProvider(new DocumentContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());

//        viewer.setSorter(new ColumnSorter());
//        EmptyTable emptyTable = new EmptyTable();
//        emptyTable.init();

        viewer.setUseHashlookup(true);
        //viewer.setInput(documentSource);


        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        parent.setLayout(layout);
        
        Label label = new Label(parent, SWT.LEFT);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        label.setText("Documents: ");

        // add a list view for temporary display
        listViewer = new ListViewer(parent, SWT.READ_ONLY);
        listViewer.setContentProvider(new ArrayContentProvider());
        listViewer.setLabelProvider(new LabelProvider(){
            @Override
            public String getText( Object element ) {
                if (element instanceof IDocument) {
                    IDocument document = (IDocument) element;
                    return document.getName();
                }
                return super.getText(element);
            }
        });
        
        selectionListener = new WorkbenchSelectionListener();
        ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
        selectionService.addPostSelectionListener(selectionListener);

        // add button
        addButton = new Button(parent, SWT.PUSH);
        addButton.setSize(50, 20);
        addButton.setText("Add");
        addButton.addListener(SWT.Selection, actionListener);
        
        addDialog = new Shell(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
        addDialog.setText("Add Document");
        addDialog.setSize(150, 100);        
        
        addLabel = new Label(addDialog, SWT.NONE);
        addLabel.setText("Document:");
        addLabel.setBounds(35, 5, 100, 20);
        
        addText = new Text(addDialog, SWT.BORDER);
        addText.setSize(100, 20);
                
        okButton = new Button(addDialog, SWT.PUSH);
        okButton.setBounds(20, 35, 40, 25);
        okButton.setText("OK");
        okButton.addListener(SWT.Selection, addDocumentListener);

        cancelButton = new Button(addDialog, SWT.PUSH);
        cancelButton.setBounds(70, 35, 40, 25);
        cancelButton.setText("Cancel");

        // remove button
        removeButton = new Button(parent, SWT.PUSH);
        removeButton.setSize(50, 20);
        removeButton.setText("Delete");
        removeButton.addListener(SWT.Selection, actionListener);
        
        // open button
        openButton = new Button(parent, SWT.PUSH);
        openButton.setSize(50, 20);
        openButton.setText("Open");
        openButton.addListener(SWT.Selection, actionListener);
        
        
}

    @Override
    public void setFocus() {
    }

    @Override
    public void dispose() {
        // clean up any page stuffs
        if( selectionListener != null ){
            // if our init method failed selectionListener would be null!
            ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
            selectionService.removePostSelectionListener(selectionListener);
            selectionListener = null;
        }
        super.dispose();
       
    }
}
