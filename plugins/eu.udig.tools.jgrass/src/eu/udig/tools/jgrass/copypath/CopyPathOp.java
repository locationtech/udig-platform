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
package eu.udig.tools.jgrass.copypath;

import java.io.File;

import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import eu.udig.tools.jgrass.utils.OperationUtils;

/**
 * Operation to copy the path of a file. 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CopyPathOp extends OperationUtils implements IOp {
    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
        if (target instanceof File) {
            final File file = (File) target;
            Display.getDefault().syncExec(new Runnable(){
                public void run() {
                    final Clipboard cb = new Clipboard(display);
                    TextTransfer textTransfer = TextTransfer.getInstance();
                    cb.setContents(new Object[]{file.getAbsolutePath()}, new Transfer[]{textTransfer});
                }
            });
        }
    }

}
