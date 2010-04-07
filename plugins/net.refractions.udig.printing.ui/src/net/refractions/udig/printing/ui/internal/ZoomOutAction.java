/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.refractions.udig.printing.ui.internal;

import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.ui.internal.editor.PageEditorInput;
import net.refractions.udig.project.ui.UDIGEditorInput;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Action to zoom out the print page.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class ZoomOutAction extends Action implements IEditorActionDelegate {

    public void setActiveEditor( IAction action, IEditorPart targetEditor ) {
    }

    public void run( IAction action ) {
        run();
    }

    public void run() {
        Page page = null;
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        UDIGEditorInput editorInput = (UDIGEditorInput) workbenchWindow.getActivePage()
                .getActiveEditor().getEditorInput();
        if (editorInput instanceof PageEditorInput) {
            page = (Page) ((PageEditorInput) editorInput).getProjectElement();
        }
        if (page == null) {
            throw new RuntimeException(Messages.PrintAction_pageError);
        }

        Dimension pSize = page.getSize();

        float factor = (float) pSize.height / (float) pSize.width;
        float xPlus = 10f;
        float yPlus = xPlus * factor;
        int w = pSize.width - (int) xPlus;
        int h = pSize.height - (int) yPlus;
        page.setSize(new Dimension(w, h));

    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

}
