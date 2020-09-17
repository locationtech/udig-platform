/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.tool.display.ModalItem;
import org.locationtech.udig.project.ui.internal.tool.display.ModalToolCategory;
import org.locationtech.udig.project.ui.tool.IToolManager;

/**
 * Factory for the Tool Palette
 * 
 * @author Jody Garnett
 * @since 1.3.0
 * @version 1.3.0
 */
public class MapToolPaletteFactory {

    private static final String PREFIX = "MapToolPaletteFactory."; //$NON-NLS-1$
    private static final String PALETTE_DOCK_LOCATION = PREFIX + "Location"; //$NON-NLS-1$
    private static final String PALETTE_STATE = PREFIX + "State"; //$NON-NLS-1$
    private static final String PALETTE_SIZE = PREFIX + "Size"; //$NON-NLS-1$
    private static final int DEFAULT_PALETTE_SIZE = 125;
    
    /**
     * The ID of the default tool: Zoom
     */
    private static final String DEFAULT_ID = "org.locationtech.udig.tools.Zoom"; //$NON-NLS-1$

    static final Comparator<PaletteContainer> PALETTE_COMPARATOR = new Comparator<PaletteContainer>() {
        final List<String> preferredOrder = Arrays.asList(
                "org.locationtech.udig.tool.category.zoom",
                "org.locationtech.udig.tool.category.pan",
                "org.locationtech.udig.tool.category.info",
                "org.locationtech.udig.tool.category.measure",
                "org.locationtech.udig.tool.category.selection");

        int order(String id) {
            int index = preferredOrder.indexOf(id);
            if ("Other".equals(id)) {
                // Other will be -2 after everything else
                return -2;
            } else if (index == -1) {
                return -1;
            } else {
                // make this a positive experience with "zoom" showing up first
                return 100 - index;
            }
        }

        @Override
        public int compare(PaletteContainer o1, PaletteContainer o2) {
            String s1 = o1.getId();
            String s2 = o2.getId();
            int order1 = order(s1);
            int order2 = order(s2);

            if (order1 == order2) {
                return 0;
            } else if (order1 < order2) {
                return 1;
            } else {
                return -1;
            }
            // return order1-order2; // is this the fast way? I am not good a C
        }
    };
    /**
     * Create a map tool palette bridging from from uDig ToolManager to the GEF ToolEntry model.
     * 
     * @param domain
     * @param domain The domain (ie MapEditor or MapPart) for which we are tacking tools
     * @return PaletteRoot suitable for use with a PaletteView
     */
    public static PaletteRoot createPalette() {
        PaletteRoot root = new PaletteRoot();
        IToolManager toolManager = ApplicationGIS.getToolManager();

        List<PaletteContainer> categories = new ArrayList<>();

        // Normal GEF Tools (SelectionTool etc...)
        // PaletteContainer controlGroup = createControlGroup(root);
        // categories.add(controlGroup);
        PaletteToolbar navigation = new PaletteToolbar("Navigation");
        navigation.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
        
        int othersCounter = 1;
        for( ModalToolCategory category : toolManager.getModalToolCategories() ) {
            PaletteContainer container;
            if( category.getId().equals("org.locationtech.udig.tool.category.zoom") ||
                    category.getId().equals("org.locationtech.udig.tool.category.pan")) {
                container = navigation;
            } else {
                String categoryName = category.getName();
                if (categoryName == null || categoryName.trim().isEmpty()) {
                    categoryName = MessageFormat.format(Messages.MapToolPaletteCategoryNameFallback,
                            othersCounter++);
                    ProjectUIPlugin.getDefault().getLog().log(new Status(IStatus.WARNING,
                            ProjectUIPlugin.ID,
                            MessageFormat.format(
                                    "'name' attribute for Tool Category extension with id {0} not set.", ////$NON-NLS-1$
                                    category.getId())));
                }

                int initialState = PaletteDrawer.INITIAL_STATE_CLOSED;
                if (category == toolManager.getActiveCategory()) {
                    initialState = PaletteDrawer.INITIAL_STATE_OPEN;
                }
                container = createPaletteContainer(category.getId(), fixLabel(categoryName),
                        shortcut(categoryName), initialState);
            }
            category.container( container ); // hook up so container can cycle tools on keypress
            for( ModalItem modalItem : category ) {
                String label = fixLabel(modalItem.getName());
                String keypress = shortcut(modalItem.getName());
                ToolEntry tool = new MapToolEntry(label, modalItem, keypress, category.getId());
                
                //set the default tool
                if(modalItem.getId().equals(DEFAULT_ID)){
                    root.setDefaultEntry(tool);
                }         
                container.add(tool);
            }
            if (container == navigation) {
                continue; // don't add navigation container multiple times
            }
            categories.add(container);
        }

        Collections.sort(categories, PALETTE_COMPARATOR);
        categories.add(0,navigation);
        // try and prevent tool category order from changing
        root.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
        root.setChildren(categories);
        return root;
    }
    
    private static PaletteContainer createPaletteContainer(String categoryId, String label,
            String shortcut, int initialState) {
        // Simple PaletteDrawer (no icon for the tool category at this time)
        PaletteDrawer drawer = new PaletteDrawer(label);
        drawer.setId(categoryId);
        drawer.setInitialState(initialState);
        drawer.setDrawerType(ToolEntry.PALETTE_TYPE_TOOL);
        drawer.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
        drawer.setShowDefaultIcon(false);
        if (shortcut != null) {
            drawer.setDescription("(" + shortcut + ")");
        }
        return drawer;
    }

    static String shortcut( String label ){
        int cut = label.indexOf("&");
        String shortcut = cut == -1 ? null : label.substring(cut+1,cut+2);
        return shortcut;
    }

    /** Trim funny symbols as the world tool from the label displayed */
    static String fixLabel( String label ) {
        label = label.replace("&", ""); // remove reference to keyboard short cut
        label = label.replace("Tools", "");
        label = label.replace("Tool", "");
        return label;
    }

    /**
     * This looks like a PaletteContainer that hosts the "normal" GEF tools (such as the
     * SelectionTool "arrow"). We won't be using this but I will keep it hear for a bit as a
     * reference point.
     * 
     * @param root
     * @return container of the usual GEF suspects
     */
    private static PaletteContainer createControlGroup( PaletteRoot root ) {
        PaletteGroup controlGroup = new PaletteGroup("Actions");

        List<ToolEntry> entries = new ArrayList<ToolEntry>();

        ToolEntry tool = new SelectionToolEntry();
        tool.setToolClass(SelectionToolWithDoubleClick.class);
        entries.add(tool);
        root.setDefaultEntry(tool);

        controlGroup.addAll(entries);

        return controlGroup;
    }

    /**
     * We make use of the ProjectUIPlugin preference store (if we need to offer the user any control
     * over palette presentation).
     */
    private static IPreferenceStore getPreferenceStore() {
        return ProjectUIPlugin.getDefault().getPreferenceStore();
    }

    static FlyoutPreferences createPalettePreferences() {

        getPreferenceStore().setDefault(PALETTE_DOCK_LOCATION, -1);
        getPreferenceStore().setDefault(PALETTE_STATE, 4);
        getPreferenceStore().setDefault(PALETTE_SIZE, DEFAULT_PALETTE_SIZE);

        return new FlyoutPreferences(){
            @Override
            public int getDockLocation() {
                return getPreferenceStore().getInt(PALETTE_DOCK_LOCATION);
            }

            @Override
            public int getPaletteState() {
                return getPreferenceStore().getInt(PALETTE_STATE);
            }

            @Override
            public int getPaletteWidth() {
                return getPreferenceStore().getInt(PALETTE_SIZE);
            }

            @Override
            public void setDockLocation( int location ) {
                getPreferenceStore().setValue(PALETTE_DOCK_LOCATION, location);
            }

            @Override
            public void setPaletteState( int state ) {
                getPreferenceStore().setValue(PALETTE_STATE, state);
            }

            @Override
            public void setPaletteWidth( int width ) {
                getPreferenceStore().setValue(PALETTE_SIZE, width);
            }
        };
    }

    /**
     * An extension of the normal GEF SelectionTool that can pass on a double click events.
     * 
     * @author jody
     */
    public static class SelectionToolWithDoubleClick extends SelectionTool {
        @Override
        protected boolean handleDoubleClick( int button ) {
            EditPart part = getTargetEditPart();

            // handle any "double click actions here
            return super.handleDoubleClick(button);
        }
    }

    /**
     * New Idea - have the factory know how to cleanup after itself (rather than distribute the
     * knowledge.
     * 
     * @param paletteRoot may be null
     */
    public static void dispose( PaletteRoot paletteRoot ) {
        if( paletteRoot == null ) return;
        
        IToolManager tools = ApplicationGIS.getToolManager();
        
        // We should unhook the ToolManager enablement notifications
        for( Object child : paletteRoot.getChildren() ){
            if( child instanceof PaletteContainer){
                PaletteContainer container = (PaletteContainer) child;
                for( Object entry : container.getChildren() ){
                    if ( entry instanceof MapToolEntry){
                        MapToolEntry mapEntry = (MapToolEntry) entry;
                        mapEntry.dispose();
                    }
                }
            }
        }
    }
    
}
