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
package net.refractions.udig.ui.boundary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.boundary.BoundaryProxy;
import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;

/**
 * Allows a user to select the BoundaryStrategy to define the boundary
 * <p>
 * This view processes the "boundary" extension point in order to obtain the list of options to
 * display to the user. Each boundary may optionally provide a "page" used to further refine the
 * limit used for the boundary.
 * 
 * @author pfeiffp
 * @sinve 1.3.0
 */
public class BoundaryView extends ViewPart {

    /**
     * Listens to the global IBoundaryService and updates our view if anything changes!
     */
    private Listener serviceWatcher = new Listener(){
        // private IBoundaryStrategy selectedStrategy = null;
        public void handleEvent( Event event ) {
            String name;
            BoundaryProxy currentStrategy;
            if (event.data instanceof BoundaryProxy) {
                currentStrategy = (BoundaryProxy) event.data;
            } else {
                currentStrategy = PlatformGIS.getBoundaryService().getProxy();
            }
            setSelected(currentStrategy);            
        }
    };

    // private Combo combo;
    private ComboViewer comboViewer;

    /*
     * The initial strategy - stored as a BoundaryProxy so that the setting the inital selection on
     * the combo works
     */
    private BoundaryProxy initialStrategy = null;

    /**
     * Listens to the user and changes the global IBoundaryService to the indicated strategy.
     */
    private ISelectionChangedListener comboListener = new ISelectionChangedListener(){
        @Override
        public void selectionChanged( SelectionChangedEvent event ) {
            IStructuredSelection selectedStrategy = (IStructuredSelection) event.getSelection();
            BoundaryProxy selected = (BoundaryProxy) selectedStrategy.getFirstElement();
            
            publishBoundaryStrategy(selected);
        }
    };

    private PageBook pagebook;
    private List<IPageBookViewPage> pages = new ArrayList<IPageBookViewPage>();
    private Map<BoundaryProxy,Control> controls = new HashMap<BoundaryProxy, Control>();

    /*
     * The component to add the page to (from the strategy if supplied)
     */
    private IPageSite getPageSite() {
        return null;
    }

    /**
     * Boundary View constructor adds the known strategies
     */
    public BoundaryView() {
    }
    

    private void publishBoundaryStrategy( BoundaryProxy selected ) {
        IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
        boundaryService.setProxy(selected);
    }

    /**
     * This will update the combo viewer and pagebook (carefully unhooking events while the viewer is updated).
     * 
     * @param selected
     */
    public void setSelected( BoundaryProxy selected ) {
        if (selected == null) {
            selected = PlatformGIS.getBoundaryService().getDefault();
        }
        
        if (comboViewer == null || comboViewer.getControl().isDisposed()) {
            listenService(false);
            return; // the view has shutdown!
        }
        
        BoundaryProxy current = getSelected();
        // check combo
        if (current != selected) {
            try {
                listenCombo(false);
                comboViewer.setSelection(new StructuredSelection(selected));
            } finally {
                listenCombo(true);
            }
        }
        
        // this is the control displayed right now
        Control currentControl = null;
        for( Control page : pagebook.getChildren() ){
            if( page.isVisible() ){
                currentControl = page;
                break;
            }
        }
        // Check if we already created the control for selected
        Control selectedControl = controls.get(selected);
        
        if( selectedControl == null ){
            if (false){
                IPageBookViewPage page = selected.createPage();
                if( page != null ){
                    pages.add( page );                    
                    try {
                        page.init( getPageSite() );
                    } catch (PartInitException e) {
                        page.dispose();
                        pages.remove(page);
                        return; // die die die
                    }
                    page.createControl(pagebook);
                    Control control = page.getControl();
                    
                    controls.put( selected, control );

                    selectedControl = control;
                }
            }
            if( true ){
                // placeholder just so we see something!
                String name = current.getName();
                
                Composite content  = new Composite(pagebook, SWT.NULL);
                content.setLayout(new FillLayout());
    
                Label label = new Label( content, SWT.LEFT | SWT.TOP | SWT.WRAP );
                label.setText(name);
                
                controls.put( selected, content );
                selectedControl = content;
            }
        }
        if( selectedControl != null ){
            pagebook.showPage(selectedControl); // done!
        }
    }
    /**
     * Access the IBoundaryStrategy selected by the user
     * 
     * @return IBoundaryStrategy selected by the user
     */
    public BoundaryProxy getSelected() {
        if (comboViewer.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
            return (BoundaryProxy) selection.getFirstElement();
        }
        return null;
    }

    protected void listenCombo( boolean listen ) {
        if (comboViewer == null || comboViewer.getControl().isDisposed()) {
            return; // run away!
        }
        if (listen) {
            comboViewer.addSelectionChangedListener(comboListener);
        } else {
            comboViewer.removeSelectionChangedListener(comboListener);
        }
    }

    protected void listenService( boolean listen ) {
        IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
        if (listen) {
            boundaryService.addListener(serviceWatcher);
        } else {
            boundaryService.removeListener(serviceWatcher);
        }
    }

    @Override
    public void init( IViewSite site, IMemento memento ) throws PartInitException {
        super.init(site, memento);
        
        // this is where you read your memento to remember
        // anything the user told you from last time
        // this.addBoundaryStrategy();
        if( memento != null ){
//            String id = memento.getString("boundary");
//            IBoundaryService service = PlatformGIS.getBoundaryService();
//            this.initialStrategy = service.findProxy(id);
        }
    }
    
    @Override
    public void saveState( IMemento memento ) {
        super.saveState(memento);
        
        IBoundaryService service = PlatformGIS.getBoundaryService();
        String id = service.getProxy().getId();
        
        memento.putString("boundary", id );
    }

    @Override
    public void createPartControl( Composite parent ) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        parent.setLayout(layout);
        Label label = new Label(parent, SWT.LEFT);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        label.setText("Boundary: ");

        // get the current strategy
        IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
        IBoundaryStrategy currentStrategy = boundaryService.getProxy();
        listenService(true);

        // eclipse combo viewer
        comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider(){
            @Override
            public String getText( Object element ) {
                if (element instanceof IBoundaryStrategy) {
                    IBoundaryStrategy comboStrategy = (IBoundaryStrategy) element;
                    return comboStrategy.getName();
                }
                return super.getText(element);
            }
        });
        comboViewer.setInput(boundaryService.getProxyList());
        // set the current strategy
        comboViewer.setSelection(new StructuredSelection(boundaryService.getDefault()));

        //pageContainer = new PageSite(this.getViewSite());
        
/*        Label testLabel = new Label(pageContainer, SWT.LEFT);
        testLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        testLabel.setText("Testing: ");
        testLabel.pack();
*/        
        // now that we are configured we can start to listen!
        listenCombo(true);
        
        pagebook = new PageBook(parent, SWT.NONE );
        GridData layoutData = new GridData(SWT.LEFT, SWT.TOP, true, true);
        layoutData.widthHint=400;
        layoutData.horizontalSpan=2;
        layoutData.heightHint=400;
        pagebook.setLayoutData(layoutData);        
    }

    @Override
    public void setFocus() {
        comboViewer.getControl().setFocus();
    }

    @Override
    public void dispose() {
        super.dispose();
        // clean up any page stuffs
        if (comboViewer != null) {
            comboViewer.removeSelectionChangedListener(comboListener);
        }
        if (serviceWatcher != null) {
            IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
            if (boundaryService != null) {
                boundaryService.removeListener(serviceWatcher);
            }
            serviceWatcher = null;
        }
    }
}
