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
package net.refractions.udig.ui.filter;

import java.awt.Color;
import java.util.Comparator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.opengis.filter.Filter;

/**
 * Factory used to create an appropriate Viewer implementation.
 * <p>
 * This factory is expected to take several things into account when figuring out what is
 * appropriate:
 * <ul>
 * <li>Input: Gathers the context (such as FeatureType) in which the viewer is expected to be used.
 * Custom viewers can be supplied that match a specific FeatureType; or a general purpose
 * FilterViewer capable of working with any kind of data.</li>
 * <li>Selection: The value (for example a Filter) the user is currently editing. While a general
 * purpose CQL based FilterViewer can allow the user to work with any provided Fitler, we also
 * expect viewers that are specifically used for checks such as isNull (data integrity) or Before
 * (temporal)</li>
 * </ul>
 * <p>
 * Note we are being pretty evil here and not supplying a proxy implementation; in part because we
 * need to call {@link #score(Object, Object)} in order to present these options to the user
 * in an appropriate order.
 * 
 * @author Jody Garnett
 * @since 1.3.2
 */
public abstract class ViewerFactory<V extends Viewer> {
    /**
     * TDOD fill me out
     */
    public static enum Appropriate {

        /**
         * Value used to indicate Viewer cannot be used in this context.
         * <p>
         * An example would be a custom FilterViewer for "Roads" that would not be appropriate when
         * working with rivers.
         * 
         * @see ViewerFactory#appropriate
         */
        NOT_APPROPRIATE(0),

        /**
         * Unable to display or edit all the provided information.
         * <p>
         * This FilterViewerFactory.java should be listed as an option in case the user wants to
         * replace their current value - however they unable to use this viewer to display the
         * provided value.
         * <p>
         * An example would be an Include/Exclude viewer that would not be able to PropertyEquals
         * test.
         * 
         * @see ViewerFactory#appropriate
         */
        INCOMPLETE(1),

        /**
         * General purpose Viewer that allows the user to view and edit the provided value (although
         * perhaps not in the most easy to use manner).
         * <p>
         * Used for CQLFilterViewer offering direct (and general purpose) CQL filter definition.
         * 
         * @see ViewerFactory#appropriate
         */
        COMPLETE(25),

        /**
         * Custom viewer able to work with the provided value.
         * <p>
         * Example would be a viewer used to present isNull or isNotNull checks in any easy to
         * understand fashion.
         * <p>
         * This is often the best general purpose viewer available for the provided value.
         * 
         * @see ViewerFactory#appropriate
         */
        APPROPRIATE(50),

        /**
         * Moving beyond general purpose we have a viewer that is able to supply some sensible
         * defaults based on what the user is up to.
         * <p>
         * Most content specific viewers fall into this category - offering a nice
         * "fill in the blank" style viewer expressed in domain model terms.
         * <p>
         * An example would be a road specific viewer that is able to ask the user question about
         * road condition or transport weight and able to translate this into a filter taking
         * several factors into account using prior knowledge of the available road attributes.
         * <p>
         * This is intended to be custom viewer that recognizes and is able to work with the
         * provided filter.
         * 
         * @see ViewerFactory#appropriate
         */
        FRIENDLY(75),

        /**
         * An exact match - used for custom viewers that make some of the more complicated functions
         * easier to use.
         * <p>
         * Example would be viewer able to walk the user through filling in a temporal filter check
         * using a calendar control widget supporting ranges.
         * 
         * @see ViewerFactory#appropriate
         */
        PERFECT(100);

        private final int score;

        private Appropriate(int score) {
            this.score = score;
        }

        /**
         * Threshold score (between 0 and 100) used for this category.
         * 
         * @see #valueOf(int)
         * @return Score used to define the threadshold for this cateogry.
         */
        public int getScore() {
            return score;
        }

        /**
         * Used to calcualte an adjusted score (between 0 and 100) relative to this cateogry marker.
         * <p>
         * Example:
         * 
         * <pre>
         * // offers complete control, slightly recommended over general purpose CQL entry
         * return COMPLETE.getScore(2);
         * </pre>
         * 
         * @see #valueOf(int)
         * @param adjust Used to tweak the resulting score relative to the {@link #getScore()}
         *        marker
         * @return Score used to define the threadshold for this cateogry.
         */
        public int getScore(int adjust) {
            int adjustedScore = score + adjust;
            if (adjustedScore < INCOMPLETE.getScore()) {
                return INCOMPLETE.getScore(); // boundary condition 0%
            } else if (adjustedScore > PERFECT.getScore()) {
                return PERFECT.getScore(); // boundary condition 100%
            } else {
                return adjustedScore;
            }
        }

        /**
         * Used to roughly lump appropriate values into the indicated category.
         * <p>
         * Example:
         * 
         * <pre>
         * // 82% should be considered FRIENDLY offering some sensible defaults
         * Appropriate category = Appropriate.valueOf(82);
         * </pre>
         * 
         * @param score Score being considered
         * @return Appropriate {@link Appropriate} category for the provided score
         */
        public static Appropriate valueOf(int score) {
            if (score <= NOT_APPROPRIATE.getScore()) {
                return NOT_APPROPRIATE;
            } else if (score < COMPLETE.getScore()) {
                return INCOMPLETE;
            } else if (score < APPROPRIATE.getScore()) {
                return COMPLETE;
            } else if (score < FRIENDLY.getScore()) {
                return APPROPRIATE;
            } else if (score < PERFECT.getScore()) {
                return FRIENDLY;
            } else {
                return PERFECT;
            }
        }
    }

    /**
     * Used to sort a list of {@link ViewerFactory} given the provided input and value.
     * <p>
     * The implementation uses {@link ViewerFactory#appropriate} to define the ordering.
     * 
     */
    public static class ViewerFactoryComparator implements Comparator<ViewerFactory<?>> {
        private Object input;

        private Object value;

        public ViewerFactoryComparator(Object input, Object value) {
            this.input = input;
            this.value = value;
        }

        @Override
        public int compare(ViewerFactory<?> factory1, ViewerFactory<?> factory2) {
            int factory1Score = factory1 != null ? factory1.score(input, value) : -1;
            int factory2Score = factory2 != null ? factory2.score(input, value) : -1;

            return factory2Score - factory1Score;
        }
    }

    /**
     * Configuration provided by extension point used to supply display name, id, and the factory
     * implementation.
     */
    private IConfigurationElement config;

    /**
     * Configuration from extension point of things like display name and binding
     * 
     * @return
     */
    void init(IConfigurationElement config) {
        this.config = config;
    }

    /**
     * Id provided as part of {@link #config} used to refer to this viewer factory.
     * <p>
     * At the current time this is used to uniquely identify popup menu items (used to choose a
     * filterViewer) and used by {@link FilterViewer#getViewerId()} allowing client code to
     * reference the current viewer when saving our DialogSettings.
     * 
     * @return id used to identify this viewer
     */
    public String getId() {
        String id = config.getAttribute("id");
        return id;
    }
    /**
     * Display name used to allow the user to select this implementation from a list.
     * @return display name (human readable translated)
     */
    public String getDisplayName() {
        String name = config.getAttribute("name");
        return name;
    }

    /**
     * Value is expected to be of this type (for example {@link Color} or {@link Filter}.
     */
    public Class<?> getBinding() {
        return Object.class;
    }

    /**
     * Percentage between 0-100 saying how well this viewer can process the provided object.
     * <p>
     * We have a predefined enum {@link Appropriate} representing common scores to make your code
     * more readable:
     * 
     * <pre>
     * int score = factory.score(filterInput, filter);
     * 
     * Appropriate category = Appropriate.valueOf(score);
     * 
     * boolean isEnabled = category == Appropriate.NOT_APPROPRIATE;
     * </pre>
     * 
     * @param input Used to provided context for editing (may be null if no context has been
     *        provided).
     * @param value Value being edited or displayed to the user
     * @return Score compatiable with {@link Appropriate#valueOf(int)}
     */
    public int score(Object input, Object value) {
        // default to listing the viewer but not recommending it
        return Appropriate.INCOMPLETE.getScore();
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
    public abstract V createViewer(Composite composite, int style);
    
    /** Safe cast that will return the value if it is of the provided type; or null otherwise.
     * 
     * @param obj
     * @param type
     * @return Cast the provided object to type, or null
     */
    public static <T> T safeCast( Object obj, Class<T> type ){
        if( obj == null ){
            return null;
        }
        else if( type != null && type.isInstance( obj )){
            return type.cast( obj );
        }
        return null;
    }
}
