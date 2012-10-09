/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.ui.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.opengis.filter.expression.Expression;

/**
 * Factory class that takes an Expression and returns the appropriate ExpressionViewer.
 * 
 * @author Scott
 * @since 1.3.0
 */
public abstract class ExpressionViewerFactory extends ViewerFactory<IExpressionViewer> {

    @Override
    public Class<?> getBinding() {
        return Expression.class;
    }
    /**
     * Percentage between 0-100 saying how well this viewer can process the provided object.
     * <p>
     * The default implementation assumes {@link ExpressionInput} and {@link Express} in order to
     * call {@link #score(ExpressionInput, Expression)} below.
     * 
     * @param input Assumed to be {@link ExpressionInput}
     * @param value Assumed to be {@link Express}
     * @return score between 0-100 indicating suitability
     */
    public int score(Object input, Object value) {
        ExpressionInput expressionInput = safeCast(input, ExpressionInput.class);
        Expression expression = safeCast(value, Expression.class);

        return score(expressionInput, expression);
    }

    /**
     * Percentage between 0-100 saying how well this viewer can process the provided object.
     * <p>
     * The default implementation assumes {@link ExpressionInput} and {@link Express} in order to
     * call {@link #score(ExpressionInput, Expression)} below.
     * 
     * @param input Context used to assist the user in defining an expresison
     * @param expr {@link Expression} displayed to the user for editing
     * @return score between 0-100 indicating suitability
     */
    public abstract int score(ExpressionInput input, Expression expr);

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
     * {@link Viewer#setInput(filter )} prior to use. For more information please see the JFace
     * {@link Viewer} class.
     * 
     * @param composite
     * @param style
     * @return requested viewer
     */
    public abstract IExpressionViewer createViewer(Composite composite, int style);

    //
    // Factory and Extension Point Support
    //
    /** General purpose {@link IFilterViewer} suitable for use as a default */
    public static final String CQL_EXPRESSION_VIEWER = "net.refractions.udig.ui.filter.cqlExpressionViewer";

    /** Extension point ID each "expressionViewer" will be processed into our {@link #factoryList()} */
    public static final String FILTER_VIEWER_EXTENSION = FilterViewerFactory.FILTER_VIEWER_EXTENSION;

    /**
     * Internal factory list, read-only access provided by {@link #factoryList()}.
     */
    private static List<ExpressionViewerFactory> factoryList;

    /**
     * Short list {@link ExpressionViewerFactory} suitable for the editing a expression in the provided context.
     * 
     * @param input context information for editing
     * @param expression expression presented to the user for editing
     * @return
     */
    public static List<ExpressionViewerFactory> factoryList(
            final ExpressionInput input, final Expression expression) {
        List<ExpressionViewerFactory> list = new ArrayList<ExpressionViewerFactory>();
        for( ExpressionViewerFactory factory : factoryList()){
            int score = factory.score( input, expression );
            if( Appropriate.valueOf( score ) == Appropriate.NOT_APPROPRIATE ){
                continue; // skip this one 
            }
            list.add( factory );
        }
        Collections.sort(list, new ViewerFactoryComparator( input, expression ) );
        return list;
    }
    /**
     * List of {@link ExpressionViewerFactory} declared using {@link #FILTER_VIEWER_EXTENSION} extension.
     * <p>
     * Note because these factories are active objects (each with an implementation of {@link #score(ExpressionInput, Expression)}
     * which we need to call) they are not handled in the traditional eclipse "proxy" style. This is a known violation of the
     * Eclipse House rules (that will force each plugin implementing a {@link ExpressionViewerFactory} to be loaded - very bad).
     * 
     * @return Complete list of factories provided by {@link #FILTER_VIEWER_EXTENSION} extension
     */
    public synchronized static List<ExpressionViewerFactory> factoryList() {
        if (factoryList == null) {
            ArrayList<ExpressionViewerFactory> list = new ArrayList<ExpressionViewerFactory>();

            IExtensionRegistry registery = Platform.getExtensionRegistry();
            IExtensionPoint extensionPoint = registery.getExtensionPoint(FILTER_VIEWER_EXTENSION);

            IConfigurationElement[] configurationElements = extensionPoint
                    .getConfigurationElements();
            for (IConfigurationElement configuration : configurationElements) {
                if ("expressionViewer".equals(configuration.getName())) {
                    try {
                        ExpressionViewerFactory factory;
                        factory = (ExpressionViewerFactory) configuration
                                .createExecutableExtension("class");
                        factory.init(configuration);

                        list.add(factory);
                    } catch (CoreException e) {
                        String pluginId = configuration.getContributor().getName();
                        IStatus status = new Status(IStatus.WARNING, pluginId, e.getMessage(), e);
                        UiPlugin.log(status);
                    }
                } else {
                    // skip as it is probably a expressionViewer element
                }
            }
            factoryList = Collections.unmodifiableList(list);
        }
        return factoryList;
    }
}
