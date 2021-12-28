/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2004 IBM Corporation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.locationtech.udig.style.sld.editor.internal;

import org.eclipse.core.runtime.Assert;

/**
 * A page history entry.
 */
final class PageHistoryEntry {
    private String id;

    private String label;

    private Object argument;

    /**
     * Creates a new entry.
     *
     * @param id the page id
     * @param label the label to display, usually the page label
     * @param argument an argument to pass to the page, may be <code>null</code>
     */
    public PageHistoryEntry(String id, String label, Object argument) {
        Assert.isLegal(id != null);
        Assert.isLegal(label != null);
        this.id = id;
        this.label = label;
        this.argument = argument;
    }

    /**
     * Returns the page id.
     *
     * @return the page id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the page argument.
     *
     * @return the page argument
     */
    public Object getArgument() {
        return argument;
    }

    /**
     * Returns the page label.
     *
     * @return the page label
     */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        if (argument == null)
            return id;
        return id + "(" + argument + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PageHistoryEntry) {
            PageHistoryEntry other = (PageHistoryEntry) obj;
            return id.equals(other.id) && (argument == null && other.argument == null
                    || argument.equals(other.argument));
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int argHash = argument == null ? 0 : argument.hashCode() & 0x0000ffff;
        return id.hashCode() << 16 | argHash;
    }
}
