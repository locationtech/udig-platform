/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.tool.display.ModalToolCategory;

/**
 * Creates a sub menu that permits the applicability of a layer to be set.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public class LayerApplicabilityMenuCreator {
    MenuManager manager = new MenuManager(
    		Messages.LayerApplicabilityMenuCreator_ApplicabilityMenuName);
    /**
     * Construct <code>LayerApplicabilityMenuCreator</code>.
     */
    public LayerApplicabilityMenuCreator() {

        List<ModalToolCategory> list = ApplicationGIS.getToolManager().getModalToolCategories();
        for( ModalToolCategory element : list ) {
            manager.add(new ApplicabilityAction(element));

        }
    }

    /**
     * Gets the Applicability Menu
     * 
     * @return
     */
    public MenuManager getMenuManager() {
        return manager;
    }
    private static class ApplicabilityAction extends ContributionItem {

        private MenuItem menuItem;
        private ModalToolCategory category;
        /**
         * Construct <code>LayerApplicabilityMenuCreator.ApplicabilityAction</code>.
         */
        public ApplicabilityAction( ModalToolCategory category ) {
            this.category = category;
        }

        /**
         * @see org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.Event)
         */
        public void runWithEvent( Event event ) {
            IStructuredSelection selection = (IStructuredSelection) PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getSelectionService().getSelection();
            for( Iterator iter = selection.iterator(); iter.hasNext(); ) {
                Layer layer = (Layer) iter.next();
                layer.setInteraction(Interaction.getInteraction(category.getId()), !menuItem.getSelection()); 
            }
        }
        /**
         * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Menu, int)
         */
        public void fill( Menu menu, int index ) {
            if (menuItem != null && !menuItem.isDisposed())
                menuItem.dispose();
            menuItem = new MenuItem(menu, SWT.CHECK, index);
            IStructuredSelection selection = (IStructuredSelection) PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getSelectionService().getSelection();
            Layer layer = (Layer) selection.getFirstElement();
            ModalToolCategory modalToolCategory = category;
            if( category!=null ){
                Interaction key = Interaction.getInteraction(modalToolCategory.getId());
                boolean interaction = layer.getInteraction(key);
                menuItem.setSelection(interaction);
                menuItem.setText(modalToolCategory.getName());
            }
        }

        /**
         * @see org.eclipse.jface.action.ContributionItem#dispose()
         */
        public void dispose() {
            super.dispose();
            menuItem.dispose();
        }
    }

}
