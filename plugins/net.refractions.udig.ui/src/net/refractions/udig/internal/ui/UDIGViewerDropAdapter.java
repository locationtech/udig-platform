package net.refractions.udig.internal.ui;

import net.refractions.udig.ui.UDIGDropTargetListener;
import net.refractions.udig.ui.ViewerDropLocation;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;

public class UDIGViewerDropAdapter extends ViewerDropAdapter implements UDIGDropTargetListener {

	UDIGDropHandler handler;
    private IDropTargetProvider defaultTarget;

	
	public UDIGViewerDropAdapter(Viewer viewer, IDropTargetProvider defaultTarget) {
		super(viewer);
        setFeedbackEnabled(true);
		handler = new UDIGDropHandler();
        this.defaultTarget=defaultTarget;
	}

    private void initHandler(DropTargetEvent event) {
        Object target = super.determineTarget(event);
        if( target==null )
            target=defaultTarget.getTarget(event);
        handler.setTarget(target);
        handler.setViewerLocation(ViewerDropLocation.valueOf(getCurrentLocation()));
    }
    
	public UDIGDropHandler getDropHandler() {
        return handler;
	}
	
	@Override
	public void dragEnter(DropTargetEvent event) {
        initHandler(event);
		handler.dragEnter(event);
	}
	
	@Override
	public void dragOperationChanged(DropTargetEvent event) {
        initHandler(event);
		handler.dragOperationChanged(event);
	}
    
    @Override
    public void dragOver( DropTargetEvent event ) {
        initHandler(event);
        super.dragOver(event);
        handler.dragOver(event);
    }

    @Override
    public void dropAccept( DropTargetEvent event ) {
        initHandler(event);
        handler.dropAccept(event);
    }

    @Override
	public void drop(DropTargetEvent event) {
        initHandler(event);
		handler.drop(event);
	}
	
	@Override
	public boolean performDrop(Object data) {
		handler.performDrop(data, null);
		return false;
	}
	
	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		return true;
	}

    public IDropTargetProvider getDropTargetProvider() {
        return defaultTarget;
    }

    public void setDropTargetProvider( IDropTargetProvider newProvider ) {
        defaultTarget=newProvider;
    }
    
    @Override
    protected int determineLocation( DropTargetEvent event ) {
        if (!(event.item instanceof Item)) {
            return LOCATION_NONE;
        }
        Item item = (Item) event.item;
        Point coordinates = new Point(event.x, event.y);
        coordinates = getViewer().getControl().toControl(coordinates);
        if (item != null) {
            Rectangle bounds = getBounds(item);
            if (bounds == null) {
                return LOCATION_NONE;
            }
            int thirdOfItem = bounds.height/3;
            if ((coordinates.y - bounds.y) < thirdOfItem) {
                return LOCATION_BEFORE;
            }
            if ((bounds.y + bounds.height - coordinates.y) < thirdOfItem) {
                return LOCATION_AFTER;
            }
        }
        return LOCATION_ON;
    }

}