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
package org.locationtech.udig.ui.operations;

import org.eclipse.core.runtime.IConfigurationElement;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.internal.ui.UiPlugin;

public class OpFilterParser {

    FilterParser[] filterParsers;

    public OpFilterParser(FilterParser[] filterParser) {
        super();

        assert filterParser != null;

        int i = 0;
        if (filterParser != null)
            i = filterParser.length;
        this.filterParsers = new FilterParser[i];

        System.arraycopy(filterParser, 0, this.filterParsers, 0, i);

    }

    public OpFilter parseFilter(IConfigurationElement element) {

        if (element == null) {
            LoggingSupport.log(UiPlugin.getDefault(),
                    "OpFilterParser: Parsing OpFilter, Configuration element is null so returning OpFilter null"); //$NON-NLS-1$
            return OpFilter.TRUE;
        }

        String elementName = element.getName();
        if (elementName.equals("or")) { //$NON-NLS-1$
            return orFilter(element);
        }
        if (elementName.equals("not")) { //$NON-NLS-1$
            return notFilter(element);
        }
        if (elementName.equals("and")) { //$NON-NLS-1$
            return andFilter(element);
        }
        for (FilterParser parser : filterParsers) {
            if (elementName.equals(parser.getElementName())) {
                return parser.parse(element);
            }
        }
        LoggingSupport.log(UiPlugin.getDefault(),
                "OpFilterParser: Parsing OpFilter: no parser found for parsing " //$NON-NLS-1$
                        + element.getNamespaceIdentifier());

        return OpFilter.TRUE;
    }

    private OpFilter andFilter(IConfigurationElement element) {
        And andFilter = new And();

        IConfigurationElement[] children = element.getChildren();
        if (children.length == 0) {
            LoggingSupport.log(UiPlugin.getDefault(),
                    "OpFilterParser: Parsing OpFilter: No children of an AND OpFilter " //$NON-NLS-1$
                            + element.getNamespaceIdentifier());
            return OpFilter.TRUE;
        }
        for (IConfigurationElement element2 : children) {
            andFilter.getFilters().add(parseFilter(element2));
        }

        return andFilter;
    }

    private OpFilter notFilter(IConfigurationElement element) {
        IConfigurationElement[] children = element.getChildren();
        if (!validateChildren(children)) {
            return OpFilter.TRUE;
        }
        return new Not(parseFilter(children[0]));
    }

    private OpFilter orFilter(IConfigurationElement element) {
        Or orFilter = new Or();

        IConfigurationElement[] children = element.getChildren();
        if (children.length == 0) {
            LoggingSupport.log(UiPlugin.getDefault(),
                    "OpFilterParser: Parsing OpFilter: No children of an OR OpFilter " //$NON-NLS-1$
                            + element.getNamespaceIdentifier());

            return OpFilter.TRUE;
        }
        for (IConfigurationElement element2 : children) {
            orFilter.getFilters().add(parseFilter(element2));
        }

        return orFilter;

    }

    private boolean validateChildren(IConfigurationElement[] children) {
        if (children.length < 1) {
            return false;
        }
        if (children.length > 1) {
            LoggingSupport.log(UiPlugin.getDefault(),
                    new IllegalStateException(
                            "OpFilterParser: Error, more than one enablement element " + children[0] //$NON-NLS-1$
                                    .getDeclaringExtension().getExtensionPointUniqueIdentifier()));
            return false;
        }
        return true;
    }
}
