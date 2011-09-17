package net.refractions.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.tool.display.ModalItem;
import net.refractions.udig.project.ui.internal.tool.display.ModalToolCategory;
import net.refractions.udig.project.ui.tool.IToolManager;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.jface.preference.IPreferenceStore;

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
    private static final String DEFAULT_ID = "net.refractions.udig.tools.Zoom"; //$NON-NLS-1$

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

        List<PaletteContainer> categories = new ArrayList<PaletteContainer>();

        // Normal GEF Tools (SelectionTool etc...)
        // PaletteContainer controlGroup = createControlGroup(root);
        // categories.add(controlGroup);
        int open = 0;
        for( ModalToolCategory category : toolManager.getModalToolCategories() ) {
            open++;                

            // Simple PaletteDrawer (no icon for the tool category at this time)
            String name = category.getName();
            name = fixLabel(name);
            
            PaletteContainer container;
            
            PaletteDrawer drawer = new PaletteDrawer(name);
            drawer.setId( category.getId() );

            if( category == toolManager.getActiveCategory()){
                drawer.setInitialState(PaletteDrawer.INITIAL_STATE_OPEN);
            }
            else {
                drawer.setInitialState(PaletteDrawer.INITIAL_STATE_CLOSED);
            }
            drawer.setDrawerType(ToolEntry.PALETTE_TYPE_TOOL);
            drawer.setUserModificationPermission(PaletteContainer.PERMISSION_NO_MODIFICATION);
            
            drawer.setShowDefaultIcon(false);
            
            container = drawer;
        
            category.container( container ); // hook up so container can cycle tools on keypress
            for( ModalItem modalItem : category ) {
                String label = fixLabel(modalItem.getName());
                ToolEntry tool = new MapToolEntry(label, modalItem, category.getId());
                
                //set the default tool
                if(modalItem.getId().equals(DEFAULT_ID)){
                    root.setDefaultEntry(tool);
                }                
                container.add(tool);
            }
            categories.add(container);
        }
        
        Comparator<PaletteContainer> sorter = new Comparator<PaletteContainer>(){
            List<String> preferredOrder = Arrays.asList(new String[]{
                    "net.refractions.udig.tool.category.zoom",
                    "net.refractions.udig.tool.category.pan",
                    "net.refractions.udig.tool.category.info",
                    "net.refractions.udig.tool.category.selection"
            });
            int order( String id ){
                int index = preferredOrder.indexOf(id);
                if ("Other".equals(id)) {
                    // Other will be -2 after everything else
                    return -2;
                }
                else if (index == -1 ){
                    return -1;
                }
                else {
                    // make this a positive experience with "zoom" showing up first
                    return 100-index;
                }
            }
            
            public int compare( PaletteContainer o1, PaletteContainer o2 ) {
                String s1 = o1.getId();
                String s2 = o2.getId();
                int order1 = order(s1);
                int order2 = order(s2);
                
                if ( order1 == order2){
                    return 0;
                }
                else if (order1 < order2){
                    return 1;
                }
                else {
                    return -1;
                }
                // return order1-order2; // is this the fast way? I am not good a C
            }
        };
        Collections.sort(categories,sorter );
        
        // try and prevent tool category order from changing
        root.setUserModificationPermission( PaletteContainer.PERMISSION_NO_MODIFICATION );
        root.setChildren(categories);
        return root;
    }

    /** Trim funny symbols as the world tool from the label displayed */
    static String fixLabel( String label ) {
        label = label.replace("&", ""); // remove reference to keyboard short cut
        //label = label.replace("Tools", "");
        //label = label.replace("Tool", "");
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
            public int getDockLocation() {
                return getPreferenceStore().getInt(PALETTE_DOCK_LOCATION);
            }

            public int getPaletteState() {
                return getPreferenceStore().getInt(PALETTE_STATE);
            }

            public int getPaletteWidth() {
                return getPreferenceStore().getInt(PALETTE_SIZE);
            }

            public void setDockLocation( int location ) {
                getPreferenceStore().setValue(PALETTE_DOCK_LOCATION, location);
            }

            public void setPaletteState( int state ) {
                getPreferenceStore().setValue(PALETTE_STATE, state);
            }

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