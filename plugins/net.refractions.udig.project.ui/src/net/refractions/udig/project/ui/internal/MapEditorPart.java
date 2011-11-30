package net.refractions.udig.project.ui.internal;

import net.refractions.udig.internal.ui.UDIGDropHandler;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;

/**
 * interface for map editor related map parts
 * 
 * @author GDavis
 * @since 1.1.0
 */
public interface MapEditorPart extends MapPart, IEditorPart {

    public abstract MapEditorSite getMapEditorSite();
    
    // helper methods for tools
    public boolean isTesting();
    public void setTesting( boolean isTesting );

    public UDIGDropHandler getDropHandler();
    public boolean isDragging();
    public void setDragging( boolean isDragging);

    public Composite getComposite();

    public void setDirty( boolean b );
}
