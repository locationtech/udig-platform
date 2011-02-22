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
package net.refractions.udig.project.ui.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Tracks which map is the active map. Essentially just listens to the workbench for part changes
 * and when a part is an MapEditorPart it marks that as the active map.
 *
 * @author jesse
 * @since 1.1.0
 */
public class ActiveMapTracker implements IPartListener2, IWindowListener, IPageListener{

	/**
	 * All the parts that are have been active in the order of activation.
	 *
	 */
    private List<MapPart> activeParts = new CopyOnWriteArrayList<MapPart>();
    /**
     * All the maps that are currently visible
     */
    private Set<MapPart> visibleMaps = new CopyOnWriteArraySet<MapPart>();
    /**
     * All the maps that are currently open
     */
    private Set<MapPart> openMaps = new CopyOnWriteArraySet<MapPart>();

    /**
     * Returns the set of the visible maps.
     *
     * @return the set of the visible maps.
     */
    public Collection< ? extends IMap> getVisibleMaps(){
        HashSet<IMap> maps = new HashSet<IMap>();
        for( MapPart part : visibleMaps ) {
            maps.add(part.getMap());
        }
        return Collections.unmodifiableCollection(maps);
    }

    /**
     * Returns the most recently active map or {@link ApplicationGIS#NO_MAP} if all maps have been closed
     *
     * @return
     */
    public Map getActiveMap(){

    	if( Display.getCurrent()!=null ){
    		MapPart part = getActivePart();
    		if( part!=null && part!=activeParts.get(0)){
    			addActivePart(part);
    			return part.getMap();
    		}
    	}


    	if( activeParts.isEmpty() ){
    		return ApplicationGIS.NO_MAP;
    	}
        MapPart mapPart = activeParts.get(0);
        if( mapPart== null ){
            return ApplicationGIS.NO_MAP;
        }
        return mapPart.getMap();
    }


    private MapPart getActivePart() {
    	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    	if( window==null ){
    		return null;
    	}

    	IWorkbenchPage page = window.getActivePage();
    	if( page!=null ){
    		IWorkbenchPart part = page.getActivePart();
    		if( part instanceof MapPart ){
    			return (MapPart) part;
    		}
    	}

		return null;
	}

	/**
     * Returns all currently open maps;
     *
     * @return all currently open maps and empty set if no maps are open.
     */
    public Collection< ? extends IMap> getOpenMaps() {
        HashSet<IMap> maps = new HashSet<IMap>();
        for( MapPart part : openMaps ) {
            maps.add(part.getMap());
        }
        return Collections.unmodifiableCollection(maps);
    }

    /**
     * Registers the object with the platform
     */
    public void startup() {
        ApplicationGIS.setActiveMapTracker(this);
        IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.addWindowListener(this);
        IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
        for( IWorkbenchWindow workbenchWindow : windows ) {
            windowOpened(workbenchWindow);
        }
    }

	private void addActivePart(MapPart part) {
		while (activeParts.remove(part));
		activeParts.add(0,part);
	}

    private void removePart(IWorkbenchPart part) {
        while(activeParts.remove(part));
        visibleMaps.remove(part);
        openMaps.remove(part);
	}

	private boolean addOpenMap(IWorkbenchPart part) {
		return openMaps.add((MapPart) part);
	}

    public void windowClosed( IWorkbenchWindow window ) {
        // stop listening to pages and parts
        IWorkbenchPage[] pages = window.getPages();
        for( IWorkbenchPage workbenchPage : pages ) {
            workbenchPage.removePartListener(this);
        }
        window.removePageListener(this);
    }

    public void windowOpened( IWorkbenchWindow window ) {
        // start listening to pages and parts
        // stop listening to pages and parts
        IWorkbenchPage[] pages = window.getPages();
        for( IWorkbenchPage workbenchPage : pages ) {
            pageOpened(workbenchPage);
        }
        window.addPageListener(this);
    }

    public void pageClosed( IWorkbenchPage page ) {
        page.removePartListener(this);
    }

    public void pageOpened( IWorkbenchPage page ) {
        page.addPartListener(this);
        IEditorReference[] editors = page.getEditorReferences();
        for( IEditorReference reference : editors ) {
            if( reference.getPart(false) instanceof MapPart ){
                MapPart part = (MapPart) reference.getPart(false);
                openMaps.add(part);
            }
        }
        IViewReference[] views = page.getViewReferences();
        for( IViewReference reference : views ) {
            if( reference.getPart(false) instanceof MapPart ){
                MapPart part = (MapPart) reference.getPart(false);
                openMaps.add(part);
            }
        }
    }

    public void partActivated( IWorkbenchPartReference partRef ) {
        // make this the active map(if it is a MapPart)
        IWorkbenchPart part = partRef.getPart(false);
        if( part instanceof MapPart){
            addActivePart((MapPart) part);
        }
    }

    public void partClosed( IWorkbenchPartReference partRef ) {
        // if active map then make previous map be the active map
        IWorkbenchPart part = partRef.getPart(false);
        removePart(part);
    }

	public void partVisible( IWorkbenchPartReference partRef ) {
        // if no active map then make this the active map (if it is a MapPart)
        IWorkbenchPart part = partRef.getPart(false);
        if( part instanceof MapPart){
            visibleMaps.add((MapPart) part);
            if( activeParts.isEmpty() ){
                activeParts.add((MapPart) part);
            }
        }
    }

    public void partHidden( IWorkbenchPartReference partRef ) {
        IWorkbenchPart part = partRef.getPart(false);
        if( part instanceof MapPart){
            visibleMaps.remove(part);
        }
    }

    public void partOpened( IWorkbenchPartReference partRef ) {
        IWorkbenchPart part = partRef.getPart(false);
        if( part instanceof MapPart){
            addOpenMap(part);
        }
    }

    public void partBroughtToTop( IWorkbenchPartReference partRef ) {
        // do nothing
    }

    public void partDeactivated( IWorkbenchPartReference partRef ) {
        // do nothing
    }

    public void partInputChanged( IWorkbenchPartReference partRef ) {
        // do nothing
    }

    public void windowActivated( IWorkbenchWindow window ) {
        // do nothing
    }

    public void windowDeactivated( IWorkbenchWindow window ) {
        // do nothing
    }

    public void pageActivated( IWorkbenchPage page ) {
        // do nothing
    }
}
