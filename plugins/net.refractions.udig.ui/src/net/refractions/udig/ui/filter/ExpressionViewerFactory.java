package net.refractions.udig.ui.filter;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.expression.Expression;

/**
 * Factory class that takes an Expression and returns the appropriate ExpressionViewer.
 * 
 * @author Scott
 * @since 1.3.0
 */
@SuppressWarnings("restriction")
public abstract class ExpressionViewerFactory {

    /**
     * Value used to indicate ExpressionViewer cannot be used in this context.
     * <p>
     * An example would be a ExpressionViewer for editing colours that could not be used when
     * defining line width.
     * 
     * @see ExpressionViewerFactory#appropriate
     */
    public final int NOT_APPROPRIATE = 0;

    /**
     * Unable to display or edit all the provided information.
     * <p>
     * This FilterViewer should be listed as an option in case the user wants to replace their
     * current expression - however they unable to use this viewer to display the provided
     * expression.
     * <p>
     * This is a tricky thing to communicate; suggest a small warning label decorator to indicate
     * that the two are out of sync; until such time as the user uses the viewer to repalce the
     * current expression.
     * 
     * @see ExpressionViewerFactory#appropriate
     */
    public final int INCOMPLETE = 10;

    /**
     * Viewer allows the user to view and edit the provided expression (although perhaps not in the
     * most easy to use manner). Used for DefaultExpressionViewer offering direct CQL access
     * 
     * @see ExpressionViewerFactory#appropriate
     */
    public final int COMPLETE = 30;

    /**
     * Viewer both complete and appropriate for the content provided.
     * <p>
     * Example would be a general purpose color viewer that is able to show the provided literal
     * color.
     * <p>
     * This is often the best "general purpose" viewer available.
     * 
     * @see ExpressionViewerFactory#appropriate
     */
    public final int APPROPRIATE = 50;
    /**
     * Moving beyond general purpose we have a viewer that is able to supply some sensible defaults
     * based on what the user is up to.
     * <p>
     * Most content specific viewers fall into this category - offering a nice "fill in the blank"
     * style viewer expressed in domain model terms.
     * <p>
     * An example would be a road specific viewer that is able to recommend several standard road
     * widths.
     * 
     * @see ExpressionViewerFactory#appropriate
     */
    public final int FRIENDLY = 70;

    /**
     * An exact match - used for custom viewers that make some of the more complicated functions
     * easier to use. Examples would be Interpolate, Categorize or a LabelViewer based around the
     * concatenate function.
     * 
     * @see ExpressionViewerFactory#appropriate
     */
    public final int PERFECT = 100;

    private IConfigurationElement config;

    /**
     * Configuration from extension point of things like display name and binding
     * 
     * @return
     */
    void init( IConfigurationElement config ) {
        this.config = config;
    }

    String getDisplayName() {
        String name = config.getAttribute("name");
        return name;
    }

    /**
     * Expected Expression value example using either Literal (Color, Integer) or Expression class
     * such as PropertyName.
     */
    Class< ? > getBinding() {
        return Object.class;
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
     * @param schema FeatureType being considered (may be ignored by general purpose
     *        ExpressionViewer capable of working with any content)
     * @param expr Existing expression provided by user, may be null
     */
    public int appropriate( SimpleFeatureType schema, Expression expr ){
        return INCOMPLETE; // default to listing the viewer but not recomending it
    }
    /**
     * Create the requested {@link IExpressionViewer} using the supplied composite as a parent.
     * <p>
     * The currently supported styles are:
     * <ul>
     * <li>{@link SWT#DEFAULT}</li>
     * <li>{@link SWT#POP_UP} - hint used to tell the viewer it is being used in a pop up and can
     * assume both extra space and the ability of the user to resize.</li>
     * </ul>
     * <p>
     * This method simply creates the viewer; client code is expected to call
     * {@link Viewer#setInput(filter )} prior to use. For more information please see the
     * JFace {@link Viewer} class.
     * 
     * @param composite
     * @param style
     * @return requested viewer
     */
    public abstract IExpressionViewer createViewer( Composite composite, int style );
}
