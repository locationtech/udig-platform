package net.refractions.udig.project.ui.viewers;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.MapToolEntry;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.tool.ModalTool;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.palette.PaletteListener;
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
 */
public class MapEditDomain extends DefaultEditDomain {
	MapViewer mapViewer;

	private PaletteListener paletteListener = new PaletteListener() {
		public void activeToolChanged(PaletteViewer viewer, ToolEntry tool) {
			IToolManager toolManager = ApplicationGIS.getToolManager();
			if (viewer != null && mapViewer != null) {
				ToolEntry entry = viewer.getActiveTool();
				if (entry instanceof MapToolEntry) {
					MapToolEntry mapEntry = (MapToolEntry) entry;
					ModalTool mapTool = mapEntry.getMapTool();
					mapViewer.setModalTool(mapTool);
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

	/**
	 * Provided a viewer that the MapEditDomain can report tool changes to.
	 * 
	 * @param mapViewer
	 */
	public void setMapViewer(MapViewer mapViewer) {
		this.mapViewer = mapViewer;
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
}