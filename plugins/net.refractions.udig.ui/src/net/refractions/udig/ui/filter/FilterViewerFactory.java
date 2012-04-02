package net.refractions.udig.ui.filter;

import org.eclipse.core.internal.registry.ConfigurationElement;
import org.eclipse.swt.widgets.Composite;
import org.opengis.filter.Filter;

/**
 * Factory class that takes an Filter and returns the appropriate FilterViewer.
 * 
 * @author Scott
 * @since 1.3.0
 */
@SuppressWarnings("restriction")
public class FilterViewerFactory {

    final int NOT_APPROPRIATE = 0; // value used to indicate FilterViewer cannot be used
    final int INCOMPLETE = 10; // unable to display/edit all information
    final int COMPLETE = 30; // Used for DefaultFilterViewer offering direct CQL access
    final int APPROPRIATE = 50; // Used for general purpose
    final int FRIENDLY = 70; // Sensible defaults such as fill in the blank predefined query
    final int PERFECT = 100; // Exact match often offering visual graphical chooser

    private Filter filter;

    /**
     * Configuration from extension point of things like display name and binding
     * 
     * @return
     */
    void init( ConfigurationElement config ) {
        System.out.println("init: " + config);
    }

    String getDisplayName() {
        return null;
    }

    /**
     * Expected Expression value example using either Literal (Color, Integer) or Filter class
     * PropertyName
     */
    Class< ? > getBinding() {
        return null;
    }

    /** Percentage between 0-100 saying how well this viewer can process the provided object */
    int appropriate( Filter filter ) {
        this.filter = filter;

        if (filter == null) {
            return NOT_APPROPRIATE;
        }
        return PERFECT;
    }

    public IFilterViewer createViewer( Filter filter, Composite composite, int style ) {

        if (filter == filter.INCLUDE) {
            return new IncludeFilterViewer(composite, style);
        }

        return new DefaultFilterViewer(composite, style);
    }
}
