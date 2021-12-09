/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.locationtech.udig.internal.ui.operations;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ui.internal.dialogs.DialogUtil;
import org.locationtech.udig.ui.operations.OpAction;

/**
 * This is used to sort views in a RunOperationDialog.
 */
public class OperationSorter extends ViewerComparator {

    public OperationSorter() {
        super();
    }

    /**
     * Returns a negative, zero, or positive number depending on whether the first element is less
     * than, equal to, or greater than the second element.
     */
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {

        // Categories are always greater than actions.
        if (e1 instanceof OpAction && e2 instanceof OperationCategory) {
            return 1;
        }
        if (e1 instanceof OperationCategory && e2 instanceof OpAction) {
            return -1;
        }

        String str1 = null;
        String str2 = null;

        if (e1 instanceof OpAction) {
            str1 = DialogUtil.removeAccel(((OpAction) e1).getText());
        } else if (e1 instanceof OperationCategory) {
            str1 = DialogUtil.removeAccel(((OperationCategory) e1).getMenuText());
        }

        if (e2 instanceof OpAction) {
            str2 = DialogUtil.removeAccel(((OpAction) e2).getText());
        } else if (e2 instanceof OperationCategory) {
            str2 = DialogUtil.removeAccel(((OperationCategory) e2).getMenuText());
        }

        return Collator.getInstance().compare(str1, str2);
    }
}
