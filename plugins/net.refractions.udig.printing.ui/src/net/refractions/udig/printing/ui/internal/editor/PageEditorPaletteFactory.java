/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.printing.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.printing.ui.internal.BoxFactory;
import net.refractions.udig.printing.ui.internal.Messages;
import net.refractions.udig.printing.ui.internal.PrintingPlugin;
import net.refractions.udig.printing.ui.internal.editor.parts.BoxPart;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.jface.preference.IPreferenceStore;

public class PageEditorPaletteFactory {
    private static final String PREFIX = "PageEditorPaletteFactory."; //$NON-NLS-1$
    private static final String PALETTE_DOCK_LOCATION = PREFIX+"Location"; //$NON-NLS-1$
    private static final String PALETTE_STATE = PREFIX+"State"; //$NON-NLS-1$
    private static final String PALETTE_SIZE = PREFIX+"Size"; //$NON-NLS-1$
    private static final int DEFAULT_PALETTE_SIZE = 125;

    private static PaletteContainer createControlGroup(PaletteRoot root) {
        PaletteGroup controlGroup = new PaletteGroup(Messages.PageEditorPaletteFactory_controlGroup_title); 

        List<ToolEntry> entries = new ArrayList<ToolEntry>();
        ToolEntry tool = new SelectionToolEntry();
        tool.setToolClass(SelectionToolWithDoubleClick.class);
        entries.add(tool);
        root.setDefaultEntry(tool);
        

        controlGroup.addAll(entries);
        return controlGroup;
     }

     private static PaletteContainer createComponentsDrawer() {

        PaletteDrawer drawer = new PaletteDrawer(Messages.PageEditorPaletteFactory_components_title, null); 

        List<ToolEntry> entries = new ArrayList<ToolEntry>();
        
        List<BoxFactory> boxFactories = PrintingPlugin.getDefault().getVisibleBoxes();
        
        for (BoxFactory factory : boxFactories) {
        	ToolEntry tool = new CombinedTemplateCreationEntry(
        			factory.getName(),
        			factory.getDescription(),
        			null,
        			new BoxCreationFactory(factory),
        			factory.getSmallImage(),
        			factory.getLargeImage()
        	);
        	entries.add(tool);
        }

        drawer.addAll(entries);
        return drawer;
     }

     private static List<?> createCategories(PaletteRoot root) {
        List<PaletteContainer> categories = new ArrayList<PaletteContainer>();

        categories.add(createControlGroup(root));
        categories.add(createComponentsDrawer());

        return categories;
     }

     public static PaletteRoot createPalette() {
        PaletteRoot paletteRoot = new PaletteRoot();
        paletteRoot.addAll(createCategories(paletteRoot));
        return paletteRoot;
     }
     
     
     private static IPreferenceStore getPreferenceStore() {
         return PrintingPlugin.getDefault().getPreferenceStore();
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
    		public void setDockLocation(int location) {
    			getPreferenceStore().setValue(PALETTE_DOCK_LOCATION, location);
    		}
    		public void setPaletteState(int state) {
    			getPreferenceStore().setValue(PALETTE_STATE, state);
    		}
    		public void setPaletteWidth(int width) {
    			getPreferenceStore().setValue(PALETTE_SIZE, width);
    		}
        };
     }
     
     public static class SelectionToolWithDoubleClick extends SelectionTool{
         
         @Override
        protected boolean handleDoubleClick( int button ) {
             if (getTargetEditPart() instanceof BoxPart) {
                BoxPart part = (BoxPart) getTargetEditPart();
                BoxAction defaultAction = part.getDefaultAction();
                if( defaultAction!=null ){
                    defaultAction.run();
                    return true;
                }
            }
            return super.handleDoubleClick(button);
        }
     }
}
