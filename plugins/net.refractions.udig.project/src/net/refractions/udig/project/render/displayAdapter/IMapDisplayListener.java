package net.refractions.udig.project.render.displayAdapter;

/**
 * An interface for objects to listen to the MapEditor
 * <p>
 * All events are executed in a back ground thread so updating the UI must be done using the
 * {@linkplain org.eclipse.swt.widgets.Display#asyncExec(java.lang.Runnable)}method or the
 * {@linkplain org.eclipse.swt.widgets.Display#syncExec(java.lang.Runnable)}method
 * </p>
 * 
 * @author jeichar
 * @since 0.2
 */
public interface IMapDisplayListener {

    /**
     * Called with the size of the MapEditor has changed.
     * <p>
     * All events are executed in a back ground thread so updating the UI must be done using the
     * {@linkplain org.eclipse.swt.widgets.Display#asyncExec(java.lang.Runnable)}method or the
     * {@linkplain org.eclipse.swt.widgets.Display#syncExec(java.lang.Runnable)}method
     * </p>
     * 
     * @param event An event with a reference to the MapEditor and the new size.
     */
    public void sizeChanged( MapDisplayEvent event );
}
