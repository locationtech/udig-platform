package net.refractions.udig.project.geoselection;

/**
 * @author Vitalus
 * @param <T>
 */
public class GeoSelectionChangedEvent<T> {

    private String context;

    private T target;

    private IGeoSelection oldSelection;

    private IGeoSelection newSelection;

    /**
     * @param context
     * @param target
     * @param oldSelection
     * @param newSelection
     */
    public GeoSelectionChangedEvent( String context, T target, IGeoSelection oldSelection,
            IGeoSelection newSelection ) {
        super();
        this.context = context;
        this.target = target;
        this.oldSelection = oldSelection;
        this.newSelection = newSelection;
    }

    /**
     * @return
     */
    public String getContext() {
        return context;
    }

    /**
     * @return
     */
    public IGeoSelection getNewSelection() {
        return newSelection;
    }

    /**
     * @return
     */
    public IGeoSelection getOldSelection() {
        return oldSelection;
    }

    /**
     * @return
     */
    public T getTarget() {
        return target;
    }

}
