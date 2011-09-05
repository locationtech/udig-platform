package net.refractions.udig.project.ui.viewers;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.MapToolEntry;
import net.refractions.udig.project.ui.internal.tool.display.ToolManager;
import net.refractions.udig.project.ui.internal.tool.display.ToolProxy;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.tool.ModalTool;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteListener;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.ui.IEditorPart;

/**
 * Domain responsible for managing the active tool; and advertising the set of
 * available tools to the palette
 * <p>
 * The palette is actually going to handle the user interface for this; we are
 * simply listening for changes and providing a palette root.
 * 
 * @author Jody Garnett
 * @since 1.2.3
 * @version 1.3.0
 */
public class MapEditDomain extends DefaultEditDomain {

	private PaletteListener paletteListener = new PaletteListener() {
		public void activeToolChanged(PaletteViewer viewer, ToolEntry tool) {
			ToolManager tools = (ToolManager) ApplicationGIS.getToolManager();
			if (viewer != null) {
			    ToolEntry entry = viewer.getActiveTool();
				if (entry instanceof MapToolEntry) {
					MapToolEntry mapEntry = (MapToolEntry) entry;
					ToolProxy toolProxy = mapEntry.getMapToolProxy();
					tools.setActiveModalToolProxy( toolProxy );
				}
			}
		}
	};

	/**
	 * Create an edit domain for the provided IEditorPart / MapPart.
	 * 
	 * An {@link IEditorPart} isrequired in the constructor, but it can be
	 * <code>null</code>.
	 * 
	 * @param editorPart
	 */
	public MapEditDomain(IEditorPart editorPart) {
		super(editorPart);
	}
	@Override
	public void setPaletteViewer(PaletteViewer palette) {
		PaletteViewer current = getPaletteViewer();
		if (current != null) {
			current.removePaletteListener(paletteListener);
		}
		super.setPaletteViewer(palette);
		if (palette != null) {
			palette.addPaletteListener(paletteListener);
		}
	}
	/**
	 * Helper class used to activate a tool by id.
	 * <p>
	 * Used to allow MapTools top easily update the palette.
	 * @param id
	 */
    public void setActiveTool( String id ) {
        PaletteViewer paletteViewer = getPaletteViewer();
        if( id != null && id.equals( paletteViewer.getActiveTool().getId() ) ){
            return; // no change
        }
        ToolEntry entry = findToolEntry( paletteViewer.getPaletteRoot(), id );
        if( entry != null ){
            paletteViewer.setActiveTool( entry );
        }
    }
    private ToolEntry findToolEntry( PaletteContainer container, String id ) {
        if( id == null ) return null;
        for( Object item : container.getChildren() ) {
            PaletteEntry entry = (PaletteEntry) item;
            if( entry instanceof ToolEntry ){
                if( id.equals( entry.getId() )){
                    return (ToolEntry) entry;
                }
            }
            else if( entry instanceof PaletteContainer){
                ToolEntry find = findToolEntry((PaletteContainer) entry, id);
                if( find != null){
                    return find;
                }
            }
        }
        return null; // not found!
    }
    
}