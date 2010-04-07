/**
 * 
 */
package net.refractions.udig.ui;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import net.refractions.udig.internal.ui.IDropTargetProvider;
import net.refractions.udig.internal.ui.UDIGControlDragListener;
import net.refractions.udig.internal.ui.UDIGControlDropListener;
import net.refractions.udig.internal.ui.UDIGDNDProcessor;
import net.refractions.udig.internal.ui.UDIGTransfer;
import net.refractions.udig.internal.ui.UDIGViewerDropAdapter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

/**
 * Useful methods for adding drag and drop support to controls or
 * viewers.  It hooks up the control with the udig drag and drop
 * framework.
 * 
 * @author jones
 *
 */
public class UDIGDragDropUtilities {
	   
	/**
     * This class listens for selection events and enables and disables the applicable transfer types.
     * 
     * @author jones
     * @since 1.0.0
     */
    private static class DragController implements ISelectionChangedListener {
        private static final Transfer[] EMPTY=new Transfer[0];
        private DragSource dragSource;
        private Set<Transfer> transfers; 
        
        public DragController( DragSource dragSourceA, Set<Transfer> transfersA ) {
            this.dragSource=dragSourceA;
            transfers=transfersA;
        }

        public void selectionChanged( SelectionChangedEvent event ) {
            if( event.getSelection()==null ){
                dragSource.setTransfer(EMPTY);
            }

            IStructuredSelection selection=(IStructuredSelection) event.getSelection();
            setTransfers(selection);
        }

        private void setTransfers( IStructuredSelection selection ) {
            CopyOnWriteArrayList<Transfer> toSet=new CopyOnWriteArrayList<Transfer>(transfers);
            
            for( Iterator iter = selection.iterator(); iter.hasNext(); ) {
                if( toSet.isEmpty() )
                    break;
                Object element = iter.next();
                
                for( Transfer transfer : toSet ) {
                    if( transfer instanceof UDIGTransfer){
                        UDIGTransfer t=(UDIGTransfer) transfer;
                        if( !t.validate(element) )
                            toSet.remove(t);
                        continue;
                    }
                    if (transfer instanceof TextTransfer) {
                        TextTransfer t = (TextTransfer) transfer;
                        try{
                            t.javaToNative(element, t.getSupportedTypes()[0]);
                        }catch (Exception e) {
                            toSet.remove(t);
                        }
                    }
                    if (transfer instanceof FileTransfer) {
                        FileTransfer t = (FileTransfer) transfer;
                        try{
                            t.javaToNative(element, t.getSupportedTypes()[0]);
                        }catch (Exception e) {
                            toSet.remove(t);
                        }
                    }
                    if (transfer instanceof RTFTransfer) {
                        RTFTransfer t = (RTFTransfer) transfer;
                        try{
                            t.javaToNative(element, t.getSupportedTypes()[0]);
                        }catch (Exception e) {
                            toSet.remove(t);
                        }
                    }
                    if (transfer instanceof HTMLTransfer) {
                        HTMLTransfer t = (HTMLTransfer) transfer;
                        try{
                            t.javaToNative(element, t.getSupportedTypes()[0]);
                        }catch (Exception e) {
                            toSet.remove(t);
                        }
                    }
                }
            }
            
            if( toSet.isEmpty() )
                dragSource.setTransfer(EMPTY);
            else
                dragSource.setTransfer(toSet.toArray(new Transfer[toSet.size()]));
        }

    }

    /**
     * Adds both drag and drop support to the StructuredViewer.
     * <p>For this to work the destination object must have a dropAction extension
     * defined for it.<p>
     * Feedback is enabled but scroll and expand is not.
     * 
     * @param viewer the viewer to have drag and drop support added to it.
     * @param defaultTarget The target to use if the mouse is not over an item in the viewer
     * 
     */
    public static void addDragDropSupport(StructuredViewer viewer, IDropTargetProvider defaultTarget) {
        addDragDropSupport(viewer, defaultTarget, true, false);
    }

    /**
     * Adds both drag and drop support to the StructuredViewer.
     * <p>For this to work the destination object must have a dropAction extension
     * defined for it.<p>
     * 
     * @param viewer the viewer to have drag and drop support added to it.
     * @param defaultTarget The target to use if the mouse is not over an item in the viewer
     */
    public static void addDragDropSupport(StructuredViewer viewer, IDropTargetProvider defaultTarget, boolean showDropFeedback, 
            boolean expandTree) {
        addDragSupport(viewer.getControl(), viewer);
        addDropSupport(viewer, defaultTarget, showDropFeedback, expandTree);
    }
	
    /**
     * Adds drop support to the StructuredViewer.
     * <p>For this to work the destination object must have a dropAction extension
     * defined for it.<p>
     * Feedback is enabled but scroll and expand is not.
     * 
     * @param viewer the viewer to have drop support added to it.
     * @param destination The target to use if the mouse is not over an item in the viewer
     */
    public static UDIGDropTargetListener addDropSupport(StructuredViewer viewer, IDropTargetProvider defaultTarget) {
        return addDropSupport(viewer, defaultTarget, true, false);
    }   
    
    /**
     * Adds drop support to the StructuredViewer.
     * <p>For this to work the destination object must have a dropAction extension
     * defined for it.<p>
     * 
     * @param viewer the viewer to have drop support added to it.
     * @param destination The target to use if the mouse is not over an item in the viewer
     * @param showDropFeedback if true the feedback bars will be shown in the viewer.
     * @param scrollExpandEnabled if true trees will be expanded and the viewer will be scrolled.
     */
    public static UDIGDropTargetListener addDropSupport(StructuredViewer viewer, IDropTargetProvider defaultTarget, boolean showDropFeedback,
             boolean scrollExpandEnabled ) {
        int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_DEFAULT;
        Set<Transfer> transfers = getTransfers();

        UDIGViewerDropAdapter viewerDropAdapter = new UDIGViewerDropAdapter(viewer, defaultTarget);
        viewerDropAdapter.setFeedbackEnabled(showDropFeedback);
        viewerDropAdapter.setScrollExpandEnabled(scrollExpandEnabled);
        viewer.addDropSupport(dndOperations, 
                transfers.toArray(new Transfer[transfers.size()]),
                viewerDropAdapter);
        return viewerDropAdapter;
    }   
	/**
	 * Adds both drag and drop support to the StructuredViewer.
	 * <p>For this to work the destination object must have a dropAction extension
	 * defined for it.<p>
	 * 
	 * @param control the viewer to have drag and drop support added to it.
	 * @param destination the destination that determines what actions will take
	 * place when a drag or drop event occurs.
	 */
	public static void addDragDropSupport(Control control, IDropTargetProvider destination, ISelectionProvider source) {
		addDragSupport(control, source);
		addDropSupport(control, destination);
	}
	/**
	 * Adds drag support to the StructuredViewer.
	 * <p>For this to work the destination object must have a dropAction extension
	 * defined for it.<p>
	 * 
	 * @param control the viewer to have drag support added to it.
	 * @param destination the destination that determines what actions will take
	 * place when a drag event occurs.
	 * @return 
	 */
	public static DragSourceDescriptor addDragSupport(Control control, ISelectionProvider provider) {
		int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_DEFAULT;
	    Set<Transfer> transfers = getTransfers();
        transfers.remove(FileTransfer.getInstance());

	    DragSource dragSource=new DragSource(control, dndOperations);
        DragController dragController = new DragController(dragSource, transfers);
        provider.addSelectionChangedListener(dragController);
        dragController.setTransfers((IStructuredSelection) provider.getSelection());
	    UDIGControlDragListener controlDragListener = new UDIGControlDragListener(provider);
        dragSource.addDragListener(controlDragListener);
        return new DragSourceDescriptor(dragSource, controlDragListener);
	}

	/**
	 * Adds drop support to the StructuredViewer.
	 * <p>For this to work the destination object must have a dropAction extension
	 * defined for it.<p>
	 * 
	 * @param control the viewer to have drop support added to it.
	 * @param destination the destination that determines what actions will take
	 * place when a drop event occurs.
	 * @return 
	 */
	public static DropTargetDescriptor addDropSupport(Control control, IDropTargetProvider destination) {
		int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_DEFAULT;
	    Set<Transfer> transfers = getTransfers();
	    DropTarget target=new DropTarget(control, dndOperations);
	    target.setTransfer(transfers.toArray(new Transfer[transfers.size()]));
	    UDIGControlDropListener controlDropListener = new UDIGControlDropListener(destination);
        target.addDropListener(controlDropListener);
        return new DropTargetDescriptor(target, controlDropListener);
	}

	/**
	 * Returns the transfers that are available with the current
	 * udig configuration.
	 * 
	 * @return the transfers that are available with the current
	 * udig configuration.
	 */
	public static Set<Transfer> getTransfers() {
		return UDIGDNDProcessor.getTransfers();
	}

    /**
     * Registers the DND Support needed by uDig.
     * Public because it might need to be called by another application using uDig as a plugin.
     * @param configurer The IWorkbenchWindowConfigurer for the workbench
     */
    public static void registerUDigDND( IWorkbenchWindowConfigurer configurer ) {
        Set<Transfer> transfers = getTransfers();
        for (Transfer transfer : transfers ) {
            configurer.addEditorAreaTransfer(transfer);
        }
        configurer.configureEditorAreaDropListener(new UDIGControlDropListener(new EditorPlaceholder()));
    }

    /**
     * Creates a drop listener that will send the drop event to the 
     * currently active editor.
     * 
     * @return  a drop listener that will send the drop event to the 
     * currently active editor.
     */
	public static UDIGControlDropListener getEditorDropListener() {
		return new UDIGControlDropListener(new EditorPlaceholder());
	}

    public static class DragSourceDescriptor{

        public final DragSource source;
        public final DragSourceListener listener;

        public DragSourceDescriptor( DragSource sourceA, DragSourceListener listenerA ) {
            source=sourceA;
            listener=listenerA;
        }
    }
    
    public static class DropTargetDescriptor{
        public final DropTarget target;
        public final UDIGDropTargetListener listener;
        public DropTargetDescriptor(DropTarget targetA, UDIGDropTargetListener listenerA) {
            target=targetA;
            listener=listenerA;
        }
    }
    
	static private class EditorPlaceholder implements IEditorPart, IDropTargetProvider {

		IEditorPart getEditorPart() {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();
		}
		
		public IEditorInput getEditorInput() {
			return getEditorPart() != null ? getEditorPart().getEditorInput()
				: null;
		}

		public IEditorSite getEditorSite() {
			return getEditorPart() != null ? getEditorPart().getEditorSite()
				: null;
		}

		public void init(IEditorSite site, IEditorInput input) 
			throws PartInitException {
			
			if (getEditorPart() != null) 
					getEditorPart().init(site, input);
		}

		public void addPropertyListener(IPropertyListener listener) {
			if (getEditorPart() != null) 
				getEditorPart().addPropertyListener(listener);
		}

		public void createPartControl(Composite parent) {
			if (getEditorPart() != null)
				getEditorPart().createPartControl(parent);
		}

		public void dispose() {
			if (getEditorPart() != null)
				getEditorPart().dispose();
		}

		public IWorkbenchPartSite getSite() {
			return getEditorPart() != null ? getEditorPart().getSite() 
					: null;
		}

		public String getTitle() {
			return getEditorPart() != null ? getEditorPart().getTitle() 
					: null;
		}

		public Image getTitleImage() {
			return getEditorPart() != null ? getEditorPart().getTitleImage() 
					: null;
		}

		public String getTitleToolTip() {
			return getEditorPart() != null ? getEditorPart().getTitleToolTip() 
					: null;
		}

		public void removePropertyListener(IPropertyListener listener) {
			if (getEditorPart() != null)
				getEditorPart().removePropertyListener(listener);
		}

		public void setFocus() {
			if (getEditorPart() != null)
				getEditorPart().setFocus();
		}

		public Object getAdapter(Class adapter) {
			return getEditorPart() != null 
				? getEditorPart().getAdapter(adapter)
				: null;
		}

		public void doSave(IProgressMonitor monitor) {
			if (getEditorPart() != null)
				getEditorPart().doSave(monitor);
		}

		public void doSaveAs() {
			if (getEditorPart() != null)
				getEditorPart().doSaveAs();
		}

		public boolean isDirty() {
			return getEditorPart() != null ? getEditorPart().isDirty()
					: false;
		}

		public boolean isSaveAsAllowed() {
			return getEditorPart() != null ? getEditorPart().isSaveAsAllowed()
					: false;
		}

		public boolean isSaveOnCloseNeeded() {
			return getEditorPart() != null 
				? getEditorPart().isSaveOnCloseNeeded()
				: false;
		}

        public Object getTarget(DropTargetEvent event) {
            return this;
        }
		
	}

}
