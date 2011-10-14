package net.refractions.udig.ui.boundary;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.boundary.BoundaryProxy;
import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Allows a user to select the BoundaryStrategy to define the boundary
 * <p>
 * This view processes the "boundary" extension point in order to obtain the list
 * of options to display to the user. Each boundary may optionally provide a "page" used
 * to further refine the limit used for the boundary.
 * 
 * @author pfeiffp
 */
public class BoundaryView extends ViewPart {
    /** This is the boundary extension point processed to get BoundaryStrategy entries */
    private static final String EXT_ID = "net.refractions.udig.ui.boundary";

    /*
     * A list of all the strategies
     */
    private List<IBoundaryStrategy> strategyList = new ArrayList<IBoundaryStrategy>();

    /**
     * Listens to the global IBoundaryService and updates our view
     * if anything changes!
     */
    private Listener serviceWatcher = new Listener(){
        //private IBoundaryStrategy selectedStrategy = null;
        public void handleEvent( Event event ) {
            String name;
            IBoundaryStrategy currentStrategy;
            if (event.data instanceof IBoundaryStrategy) {
                currentStrategy = (IBoundaryStrategy) event.data;
            } else {
                currentStrategy = PlatformGIS.getBoundaryService().getCurrentStrategy();
            }
            setSelected( currentStrategy );
        }
    };
    
    // private Combo combo;
    private ComboViewer comboViewer;

    /*
     *  The initial strategy - stored as a BoundaryProxy so that the setting the 
     *  inital selection on the combo works
     */
    private BoundaryProxy initialStrategy = null;
    
    /**
     * Listens to the user and changes the global IBoundaryService to the
     * indicated strategy.
     */
    private ISelectionChangedListener comboListener = new ISelectionChangedListener(){
        @Override
        public void selectionChanged( SelectionChangedEvent event ) {
            IStructuredSelection selectedStrategy = (IStructuredSelection) event.getSelection();
            IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
            boundaryService.setStrategy((IBoundaryStrategy) selectedStrategy.getFirstElement());
        }
    };

    /**
     * Boundary View constructor adds the known strategies
     */
    public BoundaryView() {
    }
    /**
     * This will update the combo viewer (carefully unhooking events while
     * the viewer is updated).
     * 
     * @param selected
     */
    public void setSelected( IBoundaryStrategy selected ){
        if (selected == null) {
            selected = PlatformGIS.getBoundaryService().getDefault();
        }
        if( comboViewer == null || comboViewer.getControl().isDisposed()){
            listenService( false );
            return; // the view has shutdown!
        }
        IBoundaryStrategy current = getSelected();
        if( current == selected ){
            return; // already selected
        }
        try {
            listenCombo( false );
            comboViewer.setSelection(new StructuredSelection(selected));
        }
        finally {
            listenCombo( true );
        }
    }
    /**
     * Access the IBoundaryStrategy selected by the user
     * @return IBoundaryStrategy selected by the user
     */
    public IBoundaryStrategy getSelected() {
        if( comboViewer.getSelection() instanceof IStructuredSelection){
            IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
            return (IBoundaryStrategy) selection.getFirstElement();
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
    
    protected void listenService( boolean listen ){
        IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
        if( listen ){
            boundaryService.addListener(serviceWatcher);
        }
        else {
            boundaryService.removeListener(serviceWatcher);
        }
    }
    
    @Override
    public void init( IViewSite site, IMemento memento ) throws PartInitException {
        super.init(site, memento);
        // this is where you read your memento to remember
        // anything the user told you from last time
        // add the default strategy

        // Ensure we have at least the current strategy
        
        final IBoundaryStrategy current = PlatformGIS.getBoundaryService().getCurrentStrategy();
        final String currentClassName = current != null ? current.getClass().getName() : null;
        
        // extension point processing
        ExtensionPointProcessor processBoundaryItems = new ExtensionPointProcessor(){
            @Override
            public void process( IExtension extension, IConfigurationElement element ) throws Exception {
               BoundaryProxy strategy = new BoundaryProxy(element);
               strategyList.add(strategy);
               String className = element.getAttribute("class");
               if( currentClassName != null && currentClassName.equals( className )){
                   initialStrategy = strategy;
               }
            }
        };
        ExtensionPointUtil.process( UiPlugin.getDefault(), EXT_ID,  processBoundaryItems );
        
    }

    @Override
    public void createPartControl( Composite parent ) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        parent.setLayout(layout);
        Label label = new Label(parent, SWT.LEFT);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        label.setText("Boundary: ");

        // start listening
        listenService( true );

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

        comboViewer.setInput(strategyList);
        // set the current strategy
        if (initialStrategy != null)  {
            comboViewer.setSelection(new StructuredSelection(initialStrategy));
        }
        
        // now that we are configured we can start to listen!
        listenCombo( true );
    }
    
    @Override
    public void setFocus() {
        comboViewer.getControl().setFocus();
    }

    /**
     * Adds a Boundary Strategy to the view
     * 
     * @param strategy
     * @return boolean true if strategy was added
     */
    public boolean addBoundaryStrategy( IBoundaryStrategy strategy ) {
        if (!this.strategyList.contains(strategy)) {
            this.strategyList.add(strategy);
            return true;
        }
        return false;
    }
    @Override
    public void dispose() {
        super.dispose();
        if( comboViewer != null) {
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
