/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.jconsole.core;

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

    private static final String title = "OmsBox Processing Output";
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
