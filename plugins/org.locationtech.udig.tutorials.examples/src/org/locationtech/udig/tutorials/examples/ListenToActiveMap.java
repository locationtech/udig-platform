/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.examples;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.locationtech.udig.project.ui.internal.MapEditorWithPalette;

/**
 * This class demonstrates how to listen for events that signal that the current map is being
 * changed (to another map).
 *
 * <p>
 * In order to be the active map the MapEditor for the map must be the last map editor that was
 * activated. Being visible or opened is not sufficient. However if the active map is closed or
 * hidden then the active map is the new Map Editor that is visible.
 * </p>
 *
 * @author Jesse
 */
public class ListenToActiveMap {
    private IPartListener2 activeMapListener = new IPartListener2() {

        @Override
        public void partActivated(IWorkbenchPartReference partRef) {
            if (partRef.getId().equals(MapEditorWithPalette.ID)) {
                // ok this is a map and the map is actually activated (focus has been given to the
                // editor.
                // could also use the following check instead of comparing IDs:
                // partRef.getPart(false) instanceof MapEditor
            }

        }

        @Override
        public void partBroughtToTop(IWorkbenchPartReference partRef) {
            if (partRef.getId().equals(MapEditorWithPalette.ID)) {
                // The "active" map editor has changed. The "top" editor is the active one.
                // coud also use the following check instead of comparing IDs:
                // partRef.getPart(false) instanceof MapEditor
            }

        }

        @Override
        public void partClosed(IWorkbenchPartReference partRef) {
            if (partRef.getId().equals(MapEditorWithPalette.ID)) {
                // a map editor has closed it is not necessarilly the active one. You
                // need to do your own checks for that.
            }

        }

        @Override
        public void partDeactivated(IWorkbenchPartReference partRef) {
            if (partRef.getId().equals(MapEditorWithPalette.ID)) {
                // This doesn't necessarily mean that the active map has changed
                // Just that the editor no longer has focus.
            }

        }

        @Override
        public void partHidden(IWorkbenchPartReference partRef) {
            if (partRef.getId().equals(MapEditorWithPalette.ID)) {
                // The "active" map has been hidden there is now a new active map
                // the method partBroughtToTop will be called so wait for that method before
                // actually
                // changing current map.
            }

        }

        @Override
        public void partInputChanged(IWorkbenchPartReference partRef) {
            if (partRef.getId().equals(MapEditorWithPalette.ID)) {
                // This should never be called
            }

        }

        @Override
        public void partOpened(IWorkbenchPartReference partRef) {
            if (partRef.getId().equals(MapEditorWithPalette.ID)) {
                // A map has been openned and will probably be the active map.
            }

        }

        @Override
        public void partVisible(IWorkbenchPartReference partRef) {
            if (partRef.getId().equals(MapEditorWithPalette.ID)) {
                // a map is visible but not necessarily the active map.
            }

        }

    };

    /**
     * This assumes that the caller has acces to a site. Usually can be obtained by getSite() from a
     * view or editor.
     *
     * @param site
     */
    public void addActiveMapListener(IWorkbenchPartSite site) {
        IWorkbenchPage page = site.getPage();
        page.addPartListener(activeMapListener);
    }
}
