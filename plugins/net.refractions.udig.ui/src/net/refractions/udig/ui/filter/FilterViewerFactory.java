package net.refractions.udig.ui.filter;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Factory used to create an appropriate FilterViewer implementation.
 * <p>
 * This factory is expected to take several things into account when figuring out what is
 * appropriate:
 * <ul>
 * <li>FeatureType: Custom viewers can be supplied that match a specific FeatureType; or a general
 * purpose FilterViewer capable of working with any kind of data.</li>
 * <li>Filter: The filter the user is currently editing. While a general purpose CQL based
 * FilterViewer can allow the user to work with any provided Fitler, we also expect viewers that are
 * specifically used for checks such as isNull (data integrity) or Before (temporal)</li>
 * </ul>
 * 
 * @author Scott
 * @since 1.3.0
 */
public abstract class FilterViewerFactory {
    /**
     * Value used to indicate FilterViewer cannot be used in this context.
     * <p>
     * An example would be a custom FilterViewer for "Roads" that would not be appropriate when
     * working with rivers.
     * 
     * @see FilterViewerFactory#appropriate
     */
    public final int NOT_APPROPRIATE = 0;

    /**
     * Unable to display or edit all the provided information.
     * <p>
     * This FilterViewer should be listed as an option in case the user wants to replace their
     * current Filter - however they unable to use this viewer to display the provided filter.
     * <p>
     * An example would be an Include/Exclude viewer that would not be able to PropertyEquals test.
     * 
     * @see FilterViewerFactory#appropriate
     */
    public final int INCOMPLETE = 10;

    /**
     * General purpose Viewer tjat allows the user to view and edit the provided filter (although
     * perhaps not in the most easy to use manner).
     * <p>
     * Used for DefaultFilterViewer offering direct (and general purpose) CQL filter definition.
     * 
     * @see FilterViewerFactory#appropriate
     */
    public final int COMPLETE = 30;

    /**
     * Custom viewer able to work with the provided filter.
     * <p>
     * Example would be a viewer used to present isNull or isNotNull checks in any easy to
     * understand fashion.
     * <p>
     * This is often the best general purpose viewer available for the provided filter.
     * 
     * @see FilterViewerFactory#appropriate
     */
    public final int APPROPRIATE = 50;

    /**
     * Moving beyond general purpose we have a viewer that is able to supply some sensible defaults
     * based on what the user is up to.
     * <p>
     * Most content specific viewers fall into this category - offering a nice "fill in the blank"
     * style viewer expressed in domain model terms.
     * <p>
     * An example would be a road specific viewer that is able to ask the user question about road
     * condition or transport weight and able to translate this into a filter taking several factors
     * into account using prior knowledge of the available road attributes.
     * <p>
     * This is intended to be custom viewer that recognizes and is able to work with the provided
     * filter.
     * 
     * @see FilterViewerFactory#appropriate
     */
    public final int FRIENDLY = 70;

    /**
     * An exact match - used for custom viewers that make some of the more complicated functions
     * easier to use.
     * <p>
     * Example would be viewer able to walk the user through filling in a temporal filter check
     * using a calendar control widget supporting ranges.
     * 
     * @see FilterViewerFactory#appropriate
     */
    public final int PERFECT = 100;

    private IConfigurationElement config;

    /**
     * Configuration from extension point of things like display name and binding
     * 
     * @return
     */
    void init(IConfigurationElement config) {
        this.config = config;
    }

    public String getDisplayName() {
        String name = config.getAttribute("name");
        return name;
    }

    /**
     * Expected Filter or {@link Filter} for a general purpose viewer.
     */
    public Class<?> getBinding() {
        return Filter.class;
    }

    /**
     * Percentage between 0-100 saying how well this viewer can process the provided object.
     * <p>
     * We have some predefined constants if you would like to make your code more readable:
     * <ul>
     * <li>{@link #NOT_APPROPRIATE}</li>
     * <li>{@link #INCOMPLETE}</li>
     * <li>{@link #COMPLETE}</li>
     * <li>{@link #APPROPRIATE}</li>
     * <li>{@link #FRIENDLY}</li>
     * <li>{@link #PERFECT}</li>
     * </ul>
     * 
     * @param schema FeatureType being considered (may be ignored by general purpose FilterViewers
     *        capable of working with any content)
     * @param filter Existing filter provided by user, may be null
     */
    public int appropriate(SimpleFeatureType schema, Filter filter) {
        return INCOMPLETE; // default to listing the viewer but not recomending it
    }

    /**
     * Create the requested IFilterViewer using the supplied composite as a parent.
     * <p>
     * The currently supported styles are:
     * <ul>
     * <li>{@link SWT#DEFAULT}</li>
     * <li>{@link SWT#POP_UP} - hint used to tell the viewer it is being used in a pop up and can
     * assume both extra space and the ability of the user to resize.</li>
     * </ul>
     * <p>
     * This method simply creates the viewer; client code is expected to call
     * {@link Viewer#setInput(filter )} prior to use. For more information please see the JFace
     * {@link Viewer} class.
     * 
     * @param composite
     * @param style
     * @return requested viewer
     */
    public abstract IFilterViewer createViewer(Composite composite, int style);
}
