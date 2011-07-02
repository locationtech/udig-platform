/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.udig.omsbox.core;

import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * The {@link MessageConsole} of the script editor.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class JConsoleOutputConsole extends MessageConsole {

    private final static String title = "OmsBox Processing Output";
    public final PrintStream internal;
    public final PrintStream err;
    public final PrintStream out;

    // private Color COLOR_GRAY;
    private Color COLOR_RED;
    private Color COLOR_BLACK;

    public JConsoleOutputConsole( String newTitle ) {
        super(newTitle == null ? title : newTitle, null);

        Display.getDefault().syncExec(new Runnable(){
            public void run() {
                // COLOR_GRAY = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
                COLOR_RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);
                COLOR_BLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
            }
        });

        this.setTabWidth(4);
        MessageConsoleStream internalStream = newMessageStream();
        internal = new PrintStream(internalStream, true);
        internalStream.setColor(COLOR_BLACK);
        MessageConsoleStream errorStream = newMessageStream();
        err = new PrintStream(errorStream, true);
        errorStream.setColor(COLOR_RED);
        MessageConsoleStream outputStream = newMessageStream();
        out = new PrintStream(outputStream, true);
        outputStream.setColor(COLOR_BLACK);
    }

    protected void dispose() {
        super.dispose();
    }

    public void setName( String arg0 ) {
        super.setName(arg0);
    }
}
