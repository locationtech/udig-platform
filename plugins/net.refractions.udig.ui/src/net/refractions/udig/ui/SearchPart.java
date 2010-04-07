/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.refractions.udig.ui.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;

/**
 * A ViewPart with support for a background "search" process.
 * <p>
 * The part will automatically show the "progress" when the monitored
 * Job(s) are running.
 * </p>
 * @author jgarnett
 * @since 1.0.0
 */
public class SearchPart extends ViewPart implements ISetSelectionTarget {
    
    /** Viewer for search results List/Tree/Table ... */
    protected StructuredViewer viewer; // often a tree or table viewer
        
    /**
     * Used to cancel current searchMonitor
     * <p>
     * Usually available in the toolbar.
     * </p>
     */
    protected IAction cancel;
    
    /**
     * Used to control search jobs.
     * <p>
     * Only one "batch" of search jobs is outstanding at anyone
     * time.
     * </p>
     */
    protected IProgressMonitor searchMonitor;

    protected SashForm splitter;

    /** Save state here */
    protected IMemento save;
    protected Composite parent;
    
    IDialogSettings settings;
    
    /** We need dialog settings for persistence */
    protected SearchPart( IDialogSettings dialogSettings ) {
        settings = dialogSettings;
    }
    @Override
    public void init( IViewSite site, IMemento memento ) throws PartInitException {
        super.init(site, memento);
        save = memento;
    }        
    
    @Override
    public void saveState( IMemento memento ) {
        if ( splitter == null) { // part has not been created
            if (save != null) { //Keep the old state;
                memento.putMemento( save );
            }
            return;
        }        
    }
    private void addResizeListener(Composite parent) {
        parent.addControlListener(new ControlListener() {
            public void controlMoved(ControlEvent e) {
            }
            public void controlResized(ControlEvent e) {
                computeOrientation();
            }
        });
    }
     
    protected enum Orientation { VERTICAL, HORIZONTAL, SINGLE, AUTOMATIC };
    protected Orientation orientation = Orientation.VERTICAL;
    
    void computeOrientation() {
        saveSplitterRatio();
        if( save != null ) save.putInteger("orientation", orientation.ordinal() ); //$NON-NLS-1$
        if ( orientation != Orientation.AUTOMATIC ) {
            setOrientation( orientation );
        }
        else {
            if (orientation == Orientation.SINGLE ) return;
            
            Point size= parent.getSize();
            if (size.x != 0 && size.y != 0) {
                if (size.x > size.y) 
                    setOrientation( Orientation.HORIZONTAL );
                else 
                    setOrientation( Orientation.VERTICAL );
            }
        }
    }
    private void saveSplitterRatio() {
        if (splitter != null && ! splitter.isDisposed()) {
            int[] weigths = splitter.getWeights();
            int ratio = (weigths[0] * 1000) / (weigths[0] + weigths[1]);
            settings.put( "ratio"+orientation, ratio );             //$NON-NLS-1$
        }
    }
    private void restoreSplitterRatio() {
        try {
            Integer ratio= settings.getInt("ratio"+orientation); //$NON-NLS-1$
            if (ratio == null)
                return;
            splitter.setWeights(new int[] {ratio, 1000 - ratio});
        }
        catch( NumberFormatException nan ) {
            // ignore bad setting
        }
    }
    
    Orientation currentOrientation;
    private boolean showDetails;
    /**
     * called from ToggleOrientationAction (or compute).
     * 
     * @param orientation Orientation.HORIZONTAL or Orientation.VERTICAL
     */
    protected void setOrientation(Orientation orientation) {
        if (currentOrientation == orientation) {
            return; // no change needed
        }
        if (viewer != null && !viewer.getControl().isDisposed() &&
            splitter != null && !splitter.isDisposed() ) {
            
            if (orientation == Orientation.SINGLE) {
                setShowDetails(false);
            } else {
                if (currentOrientation == Orientation.SINGLE) {
                    setShowDetails(true);
                }
                boolean horizontal = orientation == Orientation.HORIZONTAL;
                splitter.setOrientation( horizontal ? SWT.HORIZONTAL : SWT.VERTICAL);
            }
            splitter.layout();
        }
        updateCheckedState();

        currentOrientation = orientation;
        
        restoreSplitterRatio();
    }
    private ToggleOrientationAction[] toggleOrientationActions;
    private Composite details;
    private IPartListener2 partListener;
    private void updateCheckedState() {
        for (ToggleOrientationAction toggle : toggleOrientationActions) {
            toggle.setChecked( orientation == toggle.getOrientation());
        }
    }
    public void setShowDetails(boolean show) {
        showDetails = show;
        showOrHideDetails();
    }
    private void showOrHideDetails() {
        if (showDetails) {
            splitter.setMaximizedControl(null);
        } else {
            splitter.setMaximizedControl( viewer.getControl() );
        }
    }
    
    /**
     * Creates the SWT controls for this workbench part.
     * <p>
     * The details (from IWorkbenchPart.createPartControl( Composite ))
     * </p>
     * <p>
     * Multi-step process:
     * <ol>
     * <li>Create one or more controls within the parent.</li>
     * <li>Set the parent layout as needed.</li>
     * <li>Register any global actions with the <code>IActionService</code>.</li>
     * <li>Register any popup menus with the <code>IActionService</code>.</li>
     * <li>Register a selection provider with the <code>ISelectionService</code> (optional).
     * </li>
     * </ol>
     * </p>
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @param parent
     */
    public void createPartControl( Composite aParent ) {
        parent= aParent;
        addResizeListener(parent);
        
        // split
        splitter = new SashForm( parent, SWT.HORIZONTAL);
        // TODO: key listener
        
        viewer = createViewer( splitter );
        viewer.setContentProvider( createContentProvider() );
        viewer.setLabelProvider( createLabelProvider() );        
        viewer.addSelectionChangedListener( createSelectionListener() );
        getSite().setSelectionProvider(viewer);
        details = createDetails( splitter );
        
        initDragAndDrop();
        
        makeActions();
        fillViewMenu();
        fillActionBars();
        
        initOrientation();
        
        if (save != null) {
            restoreState( save );
        }
        restoreSplitterRatio();
        addPartListener();
    }
    
    /**
     * You can now restore whatever state you need.
     */
    private void restoreState(IMemento memento) {
        //
    }
    protected void saveViewSettings() {        
        saveSplitterRatio();
        settings.put( "orientation", orientation.ordinal() );         //$NON-NLS-1$
    }
    private void addPartListener() {
        final String ID = getViewSite().getId();
        partListener= new IPartListener2() {
                    public void partActivated(IWorkbenchPartReference partRef) { }
                    public void partBroughtToTop(IWorkbenchPartReference partRef) { }
                    public void partClosed(IWorkbenchPartReference partRef) {
                        if (ID.equals(partRef.getId()))
                            saveViewSettings();
                    }
                    public void partDeactivated(IWorkbenchPartReference partRef) {
                        if (ID.equals(partRef.getId()))
                            saveViewSettings();
                    }
                    public void partOpened(IWorkbenchPartReference partRef) { }
                    public void partHidden(IWorkbenchPartReference partRef) { }
                    public void partVisible(IWorkbenchPartReference partRef) { }
                    public void partInputChanged(IWorkbenchPartReference partRef) { }
                };
        getViewSite().getPage().addPartListener(partListener);
    }
    /**
     * Should do the dialog settings thing here ...
     */
    private void initOrientation() {
        orientation = Orientation.AUTOMATIC;
        if( save != null ) {
            Integer integer = save.getInteger( "orientation" ); //$NON-NLS-1$
            if( integer != null ) {
                orientation = Orientation.values()[ integer ];    
            }            
        }

        // force the update
        currentOrientation = null;
        setOrientation( orientation );
    }
    /**
     * Subclass should override to provide custom details display.
     * <p>
     * Many subclasses use PageBook with page selection based on
     * the selection in the viewer.
     * </p>
     * @param splitter
     * @return Composite to use for details display
     */
    protected Composite createDetails( SashForm splitter ) {
        return new Composite( splitter, SWT.NONE );        
    }
    /**
     * Subclass can override to return its kind of details,
     * example a PageBook.
     *
     * @return
     */
    protected Composite getDetails() {
        return details;
    }    
    
    /**
     * Create viewer (default is a ListViewer please override if you want a Tree or Table Viewer.
     * <p>
     * Note the following will be called after creation:
     * <ul>
     * <li>setContentProvider( createContentProvider() );
     * <li>viewer.setLabelProvider( createLableProvider() );
     * </ul>
     * This allows people who subclass you to do their own thing.
     * </p>    
     * @param page
     * @return
     */
    protected StructuredViewer createViewer( Composite parent ) {
        ListViewer viewer = new ListViewer( parent );
        return viewer;
    }
    /**
     * Default implementation calls showDetail( IStructuredSelection ).
     * <p>
     * Override if you want to do something else.
     */
    protected ISelectionChangedListener createSelectionListener() {
        return new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                ISelection sel= event.getSelection();
                if( sel instanceof IStructuredSelection ) {
                    IStructuredSelection selection = (IStructuredSelection) sel;
                    showDetail( selection.getFirstElement() );
                }
            }            
        };
    }
    /** Allows subclass to focus the detail on this selection  */ 
    protected void showDetail( Object selection ) {
        //
    }

    /**
     * Default implementation will work for lists, please overide if
     * you are into the whole tree thing.
     * 
     * @return
     */
    protected IStructuredContentProvider createContentProvider() {
        return new IStructuredContentProvider() {
            @SuppressWarnings("unchecked")
            public Object[] getElements( Object inputElement ) {
                if( inputElement instanceof List ) {
                    return ((List)inputElement).toArray();
                }
                return null;
            }
            public void dispose() {
            }
            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
                assert( newInput instanceof List );
                // lists don't have events for us to watch
            }            
        };
    }

    /**
     *
     * @return
     */
    protected IBaseLabelProvider createLabelProvider() {
        return new LabelProvider();
    }

    /** Will cancel any outstanding search */
    synchronized void stopSearch(){        
        if( searchMonitor != null ){
            IProgressMonitor cancelMonitor = searchMonitor;
            
            searchMonitor = null;
            cancelMonitor.setCanceled( true );            
        }        
    }
        
    /**
     * Subclass should override to support DnD.
     */
    protected void initDragAndDrop() {
        //
    }    

    protected void makeActions() {
        cancel = new Action() {
            public void run() {
                stopSearch();
                setEnabled(false);
                //book.showPage( promptPage );                
            }
        };
        Messages.initAction( cancel, "cancel" ); //$NON-NLS-1$
        cancel.setEnabled( false );
        
        toggleOrientationActions = new ToggleOrientationAction[] {
                new ToggleOrientationAction(this, Orientation.VERTICAL),
                new ToggleOrientationAction(this, Orientation.HORIZONTAL),
                new ToggleOrientationAction(this, Orientation.AUTOMATIC),
                new ToggleOrientationAction(this, Orientation.SINGLE)
            };
        
    }
    /**
    *
    */
   protected void fillViewMenu() {
       IActionBars actionBars = getViewSite().getActionBars();
       IMenuManager viewMenu = actionBars.getMenuManager();
       
       viewMenu.add( cancel );
       viewMenu.add(new Separator());
       for (ToggleOrientationAction toggle : toggleOrientationActions) {
           viewMenu.add( toggle );
       }
   }    
    
    /**
     * Create toolbar with cancel.
     */
    protected void fillActionBars() {
        IActionBars actionBars = getViewSite().getActionBars();
        IToolBarManager toolBar = actionBars.getToolBarManager();

        toolBar.add( cancel );
    }
    
    public void quick( String pattern ) {
        // search through existing contents based on label?
    }

    Object filter = null;
    Job looking = new Job( getPartName()+"..."){ //$NON-NLS-1$
        protected IStatus run( IProgressMonitor monitor ) {
            try {                    
                final ResultSet set=new ResultSet(SearchPart.this);
                Display.getDefault().asyncExec(new Runnable(){
                    public void run() {
                        viewer.setInput( set.results );
                    }
                }); 
                PlatformGIS.runBlockingOperation(new IRunnableWithProgress(){

                    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                        searchImplementation( filter, monitor, set);
                    }
                    
                }, monitor);
                Display.getDefault().asyncExec(new Runnable(){
                    public void run() {
                        viewer.setSelection(getSelection(set.results), showSelection());
                    }
                }); 
            }                
            catch (Throwable t ) {
                //book.showPage( promptPage );                    
            }finally{
                cancel.setEnabled( false );                    
            }
            return Status.OK_STATUS;                
        }
    }; 
    
    /**
     * Called each time data is added to the result set by the search.  Note: Many items or a single item could have been added.
     *
     * @param set the results of the search at the current point. 
     * @param newObjects the objects that have been added this time.
     */
    protected void notifyChange( final ResultSet set, final Collection< ? extends Object> newObjects ) {
        Display.getDefault().asyncExec(new Runnable(){
            public void run() {
                if( !newObjects.isEmpty() )
                    viewer.refresh(true);
            }
        }); 
    }

    /**
     * Called to determine what object(s) in the input should be the selection in the viewer.  This method is called when the search is complete.
     * The default selection is the first object in the list.
     * 
     * @param the complete list of objects in the viewer.
     *
     * @return the selection that will be set in the viewer.  may be null.
     */
    protected ISelection getSelection(List<Object> input) {
        if( input.size()>0 )
            return new StructuredSelection(input.get(0));
        else
            return new StructuredSelection();
    }
    /**
     * Returns true if the selection returned by {@link #getNewSelection(List)} should be shown in the viewer.
     *
     * @return true if the selection returned by {@link #getNewSelection(List)} should be shown in the viewer.
     */
    protected boolean showSelection() {
        return true;
    }

    
    /**
     * Search the catalog for text and update view contents
     * 
     * @param pattern
     */
    public void search( final Object newFilter ) {        
        if ( newFilter== null) {
            stopSearch();            
            //book.showPage( promptPage );
            return;
        }        
        stopSearch();
        
        viewer.setInput( null );
        filter = newFilter;        
        searchMonitor=Platform.getJobManager().createProgressGroup();
        searchMonitor.setTaskName( getPartName());           
        looking.setPriority( Job.BUILD );
        looking.setProgressGroup( searchMonitor, IProgressMonitor.UNKNOWN );
        cancel.setEnabled( true );
        looking.schedule(); // do it                
    }
  
    protected void searchImplementation( Object filter, IProgressMonitor monitor, ResultSet results ) {
        // No default action.
     }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.ISetSelectionTarget#selectReveal(org.eclipse.jface.viewers.ISelection)
     */
    public void selectReveal( ISelection selection ) {
        //viewer.setSelection(selection);
    }

    @Override
    public void setFocus() {
        if( viewer != null && viewer.getControl().isVisible() ) {
            viewer.getControl().setFocus();
        }
    }
    
    /**
     * Toggles the orientationof the layout
     */
    static class ToggleOrientationAction extends Action {
        private SearchPart view;    
        private Orientation orientation;        
        public ToggleOrientationAction(SearchPart searchPart, Orientation orientation) {
            super("", AS_RADIO_BUTTON); //$NON-NLS-1$            
            Messages.initAction( this, "orientation_"+orientation.toString().toLowerCase() );             //$NON-NLS-1$
            view = searchPart;
            this.orientation = orientation;
            //PlatformUI.getWorkbench().getHelpSystem().setHelp(this, lookup context id? );                    
        }        
        public Orientation getOrientation() {
            return orientation;
        }           
        /*
         * @see Action#actionPerformed
         */     
        public void run() {
            if (isChecked()) {
                view.orientation = orientation; 
                view.computeOrientation();
            }
        }
        
    }
    
    public static class ResultSet{
        SearchPart owner; 
        List<Object> results=new CopyOnWriteArrayList<Object>();
        private int changes=0;
        
        private ResultSet(SearchPart owner){
            this.owner=owner;
        }
        public boolean firstNotification() {
            return changes==1;
        }
        private void notifyChange(Collection< ? extends Object> changed){
            changes++;
            owner.notifyChange(this, changed);
        }
        public void add( int arg0, Object arg1 ) {
            results.add(arg0, arg1);
            notifyChange(Collections.singleton(arg1));
        }
        public boolean add( Object arg0 ) {
            boolean add = results.add(arg0);
            notifyChange(Collections.singleton(arg0));
            return add;
        }
        public boolean addAll( Collection< ? extends Object> arg0 ) {
            boolean addAll = results.addAll(arg0);
            notifyChange(arg0);
            return addAll;
        }
        public boolean addAll( int arg0, Collection< ? extends Object> arg1 ) {
            boolean addAll = results.addAll(arg0, arg1);
            notifyChange(arg1);
            return addAll;
        }
        public boolean contains( Object arg0 ) {
            return results.contains(arg0);
        }
        public boolean containsAll( Collection< ? > arg0 ) {
            return results.containsAll(arg0);
        }
        public Object get( int arg0 ) {
            return results.get(arg0);
        }
        public int indexOf( Object arg0 ) {
            return results.indexOf(arg0);
        }
        
    }

}
