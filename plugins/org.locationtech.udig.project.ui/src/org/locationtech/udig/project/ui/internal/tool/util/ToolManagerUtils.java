/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2022, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.project.ui.internal.tool.util;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbenchPart;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.internal.ui.UDIGDNDProcessor;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.commands.CreateMapCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.MapPart;

public class ToolManagerUtils {

    public static Map getTargetMap(final IWorkbenchPart part) {
        final Object selection = firstSelectedElement(part);

        Map finalMap = null;
        if (selection instanceof Map) {
            finalMap = (Map) selection;
            return finalMap;
        }

        final MapPart activeEditor = ApplicationGIS.getActiveMapPart();
        if (activeEditor == null || activeEditor.getMap() == null) {
            final CreateMapCommand command = new CreateMapCommand(null,
                    Collections.<IGeoResource> emptyList(), null);
            try {
                command.run(new NullProgressMonitor());
            } catch (final Exception e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
            finalMap = (Map) command.getCreatedMap();
        } else if (activeEditor.getMap() != null) {
            finalMap = activeEditor.getMap();
        }
        return finalMap;
    }

    public static Object getClipboardContent(final IWorkbenchPart part) {
        final Clipboard clipboard = new Clipboard(part.getSite().getShell().getDisplay());
        final Set<Transfer> transfers = UDIGDNDProcessor.getTransfers();
        Object contents = null;
        for (final Transfer transfer : transfers) {
            contents = clipboard.getContents(transfer);
            if (contents != null) {
                break;
            }
        }
        return contents;
    }

    public static Object firstSelectedElement(final IWorkbenchPart part) {
        final ISelection selection = part.getSite().getSelectionProvider().getSelection();
        if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
            return null;
        } else {
            return ((IStructuredSelection) selection).getFirstElement();
        }
    }

}
