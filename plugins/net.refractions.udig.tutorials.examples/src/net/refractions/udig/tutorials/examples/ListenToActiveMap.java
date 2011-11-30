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
package net.refractions.udig.tutorials.examples;

import net.refractions.udig.project.ui.internal.MapEditorWithPalette;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * This class demonstrates how to listen for events that signal that the current map is being changed (to another map).
 * 
 * <p>
 * In order to be the active map the MapEditor for the map must be the last map editor that was activated.  Being
 * visible or opened is not sufficient.  However if the active map is closed or hidden then the active map is the new 
 * Map Editor that is visible.  
 * </p>
 *   
 * @author Jesse
 */
public class ListenToActiveMap {
	private IPartListener2 activeMapListener=new IPartListener2(){

		public void partActivated(IWorkbenchPartReference partRef) {
		    if( partRef.getId().equals(MapEditorWithPalette.ID) ){
				// ok this is a map and the map is actually activated (focus has been given to the editor.
				// could also use the following check instead of comparing IDs:
				// partRef.getPart(false) instanceof MapEditor
			}
			
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			if( partRef.getId().equals(MapEditorWithPalette.ID) ){
				// The "active" map editor has changed.  The "top" editor is the active one.
				// coud also use the following check instead of comparing IDs:
				// partRef.getPart(false) instanceof MapEditor
			}
			
		}

		public void partClosed(IWorkbenchPartReference partRef) {
			if( partRef.getId().equals(MapEditorWithPalette.ID)  ){
				// a map editor has closed it is not necessarilly the active one.  You 
				// need to do your own checks for that.
			}
			
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
			if( partRef.getId().equals(MapEditorWithPalette.ID)  ){
				// This doesn't necessarily mean that the active map has changed
				// Just that the editor no longer has focus.
			}
			
			
		}

		public void partHidden(IWorkbenchPartReference partRef) {
			if( partRef.getId().equals(MapEditorWithPalette.ID)  ){
				// The "active" map has been hidden there is now a new active map
				// the method partBroughtToTop will be called so wait for that method before actually
				// changing current map.
			}
			
			
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
			if( partRef.getId().equals(MapEditorWithPalette.ID)  ){
				// This should never be called 
			}
			
		}

		public void partOpened(IWorkbenchPartReference partRef) {
			if( partRef.getId().equals(MapEditorWithPalette.ID)  ){
				// A map has been openned and will probably be the active map.
			}
			
			
			
		}

		public void partVisible(IWorkbenchPartReference partRef) {
			if( partRef.getId().equals(MapEditorWithPalette.ID)  ){
				// a map is visible but not necessarily the active map.
			}
			
			
			
		}
		
	};

	/**
	 * This assumes that the caller has acces to a site.  Usually can be obtained by getSite() from a view or editor.
	 * 
	 * @param site 
	 */
	public void addActiveMapListener(IWorkbenchPartSite site){
		IWorkbenchPage page = site.getPage();
		page.addPartListener(activeMapListener);
	}
}
