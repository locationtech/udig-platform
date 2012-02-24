package net.refractions.udig.project.ui.internal.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.project.internal.Folder;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.LegendView;
import net.refractions.udig.ui.IDropAction;
import net.refractions.udig.ui.ViewerDropLocation;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Display;

/**
 * Action that moves legend items within the legend items list. This moves layers in and out of
 * folders. And moves layers and folders up and down the order in the view.
 * 
 * @author nchan
 * @since 1.2.0
 */
public class LegendDropAction extends IDropAction {

    /**
     * Flags if source data is a layer
     */
    private boolean isLayerSource;
    
    /**
     * Flags if source data is a folder
     */
    private boolean isFolderSource;
    
    /**
     * Flags if source data is a mix of layers and/or folders
     */
    private boolean isMixedSource;
    
    /**
     * Flags if target data is a layer
     */
    private boolean isLayerTarget;
    
    /**
     * Flags if target data is a folder
     */
    private boolean isFolderTarget;
    
    /**
     * List contains the source data
     */
    private List sourceList;
    
    /**
     * The drop action destination
     */
    private Object destination;
    
    /**
     * Creates a legend item drop action
     */
    public LegendDropAction() {
        // Nothing
    }
    
    @Override
    public void init( IConfigurationElement element2, DropTargetEvent event2,
            ViewerDropLocation location2, Object destination2, Object data2 ) {
        super.init(element2, event2, location2, destination2, data2);
        initDropConditions();
    }
    
    @Override
    public boolean accept() {
        
        //CHeck if either source or destination is null
        if (sourceList == null || getDestination() == null) {
            return false;
        }
        //Check if selection is more than 1
        if (sourceList.size() > 1) {
            return false;
        }
        //Check if source and destination is the same
        if (sourceList.size() == 1 && sourceList.get(0) == getDestination()) {
            return false;
        }
        //Check relative to drop locations
        final ViewerDropLocation location = getViewerLocation();
        //Check if there is a drop location
        if (location == ViewerDropLocation.NONE) {
            return false;
        } else if (location == ViewerDropLocation.ON) {
            //Check if data being put inside is a folder or is mixed
            if (isFolderSource || isMixedSource) {
                return false;
            }
            //Check if layer data is being put inside another layer
            if (isLayerSource && isLayerTarget) {
                return false;
            }
            //Check if data is being put inside its own parent (folder)
            if (getParent(getData()) == getDestination()) {
                return false;
            }
        } else {
            //Check if folder is being put inside a folder
            if (isFolderSource && getParent(getDestination()) instanceof Folder) {
                return false;
            }
        }
        
        return true;
        
    }

    @Override
    public void perform( IProgressMonitor monitor ) {
        
        for( Object source : sourceList ) {
            
            final ViewerDropLocation location = getViewerLocation();

            if (location == ViewerDropLocation.NONE) {
                //Do nothing
            } else if (location == ViewerDropLocation.ON) {
                moveIn(source);
            } else {
                move(source, location);
            }
            
        }
        
        refresh();
        
    }

    /**
     * Refreshes the LegendView viewer
     */
    private void refresh() {
        
        final Runnable runnable = new Runnable(){
            public void run() {
                if (LegendView.getViewer() != null) {
                    LegendView.getViewer().refresh();    
                }
            }
        };

        if (Display.getCurrent() == null) {
            Display.getDefault().asyncExec(runnable);
        } else {
            runnable.run();
        }
        
    }
    
    /**
     * Expands the node of the element in the LegendView viewer
     * @param element
     */
    private void expandElement(final Object element) {
        
        final Runnable runnable = new Runnable(){
            public void run() {
                final CheckboxTreeViewer viewer = (CheckboxTreeViewer) LegendView.getViewer();
                if (viewer != null) {
                    viewer.setExpandedState(element, true);    
                }
            }
        };

        if (Display.getCurrent() == null) {
            Display.getDefault().asyncExec(runnable);
        } else {
            runnable.run();
        }
        
    }
    
    /**
     * Performs moving in the source item into a folder
     * @param source
     */
    private void moveIn( Object source ) {
        final Folder folder = (Folder) getDestination(); 
        folder.getItems().add(0, ((Layer) source));
        expandElement(folder);
    }
    
    /**
     * Performs moving the source item below or above another item, either inside or outside a
     * folder.
     * 
     * @param source
     * @param location
     */
    private void move(Object source, ViewerDropLocation location) {
        
        final List sourceParent = getParentList(source);
        final List targetParent = getParentList(getDestination());
        
        if (sourceParent == targetParent) {
            
            sourceParent.remove(source);
            int targetIndexNew = targetParent.indexOf(getDestination());
            
            if (location == ViewerDropLocation.BEFORE) {
                targetParent.add(targetIndexNew, source);
            } else if (location == ViewerDropLocation.AFTER) {
                targetParent.add(targetIndexNew + 1, source);
            }
            
        } else {
            int targetIndex = targetParent.indexOf(getDestination());
            if (location == ViewerDropLocation.AFTER) {
                targetIndex++;
            }
            targetParent.add(targetIndex, source);
        }
        
    }
    
    /**
     * Gets the actual list of the parent of the object
     * @param object
     * @return map.getLegend() or folder.getItems()
     */
    private List getParentList(Object object) {
        
        final Object parent = getParent(object);
        
        if (parent instanceof Map) {
            return ((Map) parent).getLegend();
        } else if (parent instanceof Folder) {
            return ((Folder) parent).getItems();
        }
        
        return null;
    }
    
    /**
     * Gets the parent of the object, relative to the LegendItems list. For example a folder or the
     * map if the object is contained in the LegendItem list itself.
     * 
     * @param object
     * @return map or folder
     */
    private Object getParent(Object object) {
        
        if (object instanceof Folder) {
            return ((Map) ApplicationGIS.getActiveMap());
        } else if (object instanceof Layer) {
            final List legendItems = ((Map) ApplicationGIS.getActiveMap()).getLegend();
            for( Object legendItem : legendItems ) {
                if (legendItem instanceof Folder) {
                    final List folderItems = ((Folder) legendItem).getItems();
                    for( Object folderItem : folderItems ) {
                        if (folderItem == object) {
                            return legendItem;
                        }
                    }
                } else {
                    if (legendItem == object) {
                        return ((Map) ApplicationGIS.getActiveMap());
                    }
                }
            }
        }
        
        return null;
        
    }
    
    /**
     * Initialises the drop conditions and flags used in this drop action
     */
    private void initDropConditions() {
        getSource(getData());
        getTarget(getDestination());
    }
    
    /**
     * Checks and analyses the source data initialises flags related to it
     * @param data
     */
    private void getSource( Object data ) {

        if (data == null) {
            this.sourceList = null;
            this.isFolderSource = false;
            this.isLayerSource = false;
            this.isMixedSource = false;
        } else {
            if (data instanceof Layer) {
                sourceList = Collections.singletonList(data);
                this.isFolderSource = false;
                this.isLayerSource = true;
                this.isMixedSource = false;
            } else if (data instanceof Folder) {
                sourceList = Collections.singletonList(data);
                this.isFolderSource = true;
                this.isLayerSource = false;
                this.isMixedSource = false;
            } else {
                sourceList = new ArrayList<Object>();
                Object[] array = (Object[]) data;
                for( Object object : array ) {
                    sourceList.add(object);
                }
                this.isFolderSource = false;
                this.isLayerSource = false;
                this.isMixedSource = true;
            }    
        }
        
    }
    
    /**
     * Checks and analyses the target data initialises flags related to it
     * @param data
     */
    private void getTarget( Object data ) {

        if (data == null) {
            this.isFolderTarget = false;
            this.isLayerTarget = false;
        } else {
            if (data instanceof Layer) {
                this.isFolderTarget = false;
                this.isLayerTarget = true;
            } else if (data instanceof Folder) {
                this.isFolderTarget = true;
                this.isLayerTarget = false;
            }            
        }
        
    }
    
}
