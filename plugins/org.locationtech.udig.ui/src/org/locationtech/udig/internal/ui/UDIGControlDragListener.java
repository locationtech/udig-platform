/**
 * 
 */
package net.refractions.udig.internal.ui;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

/**
 * @author jones
 *
 */
public class UDIGControlDragListener extends DragSourceAdapter implements
		DragSourceListener {

    private ISelectionProvider provider;

	  /**
	   * This creates an instance for the given viewer.
	 * @param destination 
	   */
	  public UDIGControlDragListener(ISelectionProvider provider)
	  {
	    this.provider=provider;

	  }

	  /**
	   * This is called when dragging is initiated; it records the {@link #selection} of {@link #viewer}.
	   */
	  public void dragStart(DragSourceEvent event)
	  {
        UDigByteAndLocalTransfer.getInstance().object = provider.getSelection();
	  }

	  /**
	   * This is called when dragging is completed; it forgets the {@link #selection}.
	   */
	  public void dragFinished(DragSourceEvent event)
	  {
        UDigByteAndLocalTransfer.getInstance().object = null;
        
	  }
	  
	  /**
	   * This is called to transfer the data.
	   */
	  public void dragSetData(DragSourceEvent event)
	  {
	    if (!UDigByteAndLocalTransfer.getInstance().isSupportedType(event.dataType)){
            if (UDigByteAndLocalTransfer.getInstance().object instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection) UDigByteAndLocalTransfer.getInstance().object;
                event.data=selection.getFirstElement();   
            }
        }else{
            event.data=UDigByteAndLocalTransfer.getInstance().object;
        }
	  }
}
