/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.printing.ui.internal.editor;

import java.awt.Color;

import net.refractions.udig.printing.model.impl.RectangleEllipseBoxPrinter;
import net.refractions.udig.printing.ui.IBoxEditAction;
import net.refractions.udig.printing.ui.internal.editor.parts.BoxPart;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class SimpleStyleAction implements IBoxEditAction {

    private BoxPart owner;

    public Command getCommand() {
        return null;
    }

    public void init( BoxPart owner ) {
        this.owner = owner;
    }

    private RectangleEllipseBoxPrinter getPrinter() {
        return (RectangleEllipseBoxPrinter) owner.getBoxPrinter();
    }

    public boolean isDone() {
        return true;
    }

    public void perform() {

        Shell parentShell = Display.getCurrent().getActiveShell();
        RectangleEllipseBoxPrinter boxPrinter = getPrinter();
        SimpleStyleDialog dialog = new SimpleStyleDialog(parentShell, boxPrinter);
        dialog.setBlockOnOpen(true);

        int result = dialog.open();

        if (result == Window.OK) {

            boxPrinter.setLineWidth(dialog.getLineWidth());
            boxPrinter.setLineAlpha(dialog.getLineAlpha());
            boxPrinter.setLineColor(dialog.getLineColor());
            boxPrinter.setFillAlpha(dialog.getFillAlpha());
            boxPrinter.setFillColor(dialog.getFillColor());
            boxPrinter.setType(dialog.getType());
        } else {
            return;
        }

        boxPrinter.setDirty(true);

    }

    /**
     * A simple style dialog.
     * 
     * @author Andrea Antonello (www.hydrologis.com)
     */
    private static class SimpleStyleDialog extends Dialog implements Listener {

        private float lineWidth;
        private int lineAlpha;
        private Color lineColor;
        private int fillAlpha;
        private Color fillColor;
        private int type;
        private Spinner lineWidthSpinner;
        private Spinner lineAlphaSpinner;
        private Button lineColorButton;
        private Spinner fillAlphaSpinner;
        private Button fillColorButton;
        private Button rectangleRadio;
        private Button roundRectangleRadio;
        private Button ellipseRadio;

        protected SimpleStyleDialog( Shell parentShell, RectangleEllipseBoxPrinter printer ) {
            super(parentShell);
            setShellStyle(SWT.DIALOG_TRIM);

            lineWidth = printer.getLineWidth();
            lineAlpha = printer.getLineAlpha();
            lineColor = printer.getLineColor();
            fillAlpha = printer.getFillAlpha();
            fillColor = printer.getFillColor();
            type = printer.getType();
        }

        @Override
        protected Control createContents( Composite parent ) {
            getShell().setText("Set style");

            Composite container = new Composite(parent, SWT.NONE);
            container.setLayoutData(new GridData(GridData.FILL_BOTH));
            container.setLayout(new GridLayout(2, false));

            Label lineWidthLabel = new Label(container, SWT.NONE);
            lineWidthLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            lineWidthLabel.setText("Line width");
            lineWidthSpinner = new Spinner(container, SWT.BORDER);
            lineWidthSpinner.setMinimum(1);
            lineWidthSpinner.setMaximum(10);
            lineWidthSpinner.setIncrement(1);
            lineWidthSpinner.setSelection((int) lineWidth);
            lineWidthSpinner.addListener(SWT.Modify, this);
            lineWidthSpinner.addListener(SWT.KeyUp, this);

            Label lineAlphaLabel = new Label(container, SWT.NONE);
            lineAlphaLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            lineAlphaLabel.setText("Line alpha");
            lineAlphaSpinner = new Spinner(container, SWT.BORDER);
            lineAlphaSpinner.setMinimum(0);
            lineAlphaSpinner.setMaximum(255);
            lineAlphaSpinner.setIncrement(5);
            lineAlphaSpinner.setSelection(lineAlpha);
            lineAlphaSpinner.addListener(SWT.Modify, this);
            lineAlphaSpinner.addListener(SWT.KeyUp, this);

            Label lineColorLabel = new Label(container, SWT.NONE);
            lineColorLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            lineColorLabel.setText("Line color");
            lineColorButton = new Button(container, SWT.PUSH);
            lineColorButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            lineColorButton.setText("...");
            lineColorButton.addListener(SWT.Selection, this);

            Label fillAlphaLabel = new Label(container, SWT.NONE);
            fillAlphaLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            fillAlphaLabel.setText("Fill alpha");
            fillAlphaSpinner = new Spinner(container, SWT.BORDER);
            fillAlphaSpinner.setMinimum(0);
            fillAlphaSpinner.setMaximum(255);
            fillAlphaSpinner.setIncrement(5);
            fillAlphaSpinner.setSelection(fillAlpha);
            fillAlphaSpinner.addListener(SWT.Modify, this);
            fillAlphaSpinner.addListener(SWT.KeyUp, this);

            Label fillColorLabel = new Label(container, SWT.NONE);
            fillColorLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            fillColorLabel.setText("Fill color");
            fillColorButton = new Button(container, SWT.PUSH);
            fillColorButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            fillColorButton.setText("...");
            fillColorButton.addListener(SWT.Selection, this);

            rectangleRadio = new Button(container, SWT.RADIO);
            rectangleRadio.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            rectangleRadio.setText("Rectangle");
            if (type == RectangleEllipseBoxPrinter.RECTANGLE)
                rectangleRadio.setSelection(true);
            rectangleRadio.addListener(SWT.Selection, this);
            new Label(container, SWT.NONE);
            roundRectangleRadio = new Button(container, SWT.RADIO);
            roundRectangleRadio.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            roundRectangleRadio.setText("Rounded rectangle");
            if (type == RectangleEllipseBoxPrinter.ROUNDEDRECTANGLE)
                roundRectangleRadio.setSelection(true);
            roundRectangleRadio.addListener(SWT.Selection, this);
            new Label(container, SWT.NONE);
            ellipseRadio = new Button(container, SWT.RADIO);
            ellipseRadio.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            ellipseRadio.setText("Ellipse");
            if (type == RectangleEllipseBoxPrinter.ELLIPSE)
                ellipseRadio.setSelection(true);
            ellipseRadio.addListener(SWT.Selection, this);
            new Label(container, SWT.NONE);

            Composite buttonComposite = new Composite(container, SWT.NONE);
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            gd.horizontalSpan = 2;
            buttonComposite.setLayoutData(gd);
            buttonComposite.setLayout(new GridLayout(1, true));

            createButtonsForButtonBar(buttonComposite);

            return container;
        }

        public void handleEvent( Event event ) {
            Widget widget = event.widget;

            if (widget.equals(lineWidthSpinner)) {
                lineWidth = lineWidthSpinner.getSelection();
            } else if (widget.equals(lineAlphaSpinner)) {
                lineAlpha = lineAlphaSpinner.getSelection();
            } else if (widget.equals(lineColorButton)) {
                ColorDialog dialog = new ColorDialog(lineColorButton.getShell());
                dialog.open();
                RGB rgb = dialog.getRGB();
                if (rgb != null) {
                    lineColor = new Color(rgb.red, rgb.green, rgb.blue);
                }
            } else if (widget.equals(fillAlphaSpinner)) {
                fillAlpha = fillAlphaSpinner.getSelection();
            } else if (widget.equals(fillColorButton)) {
                ColorDialog dialog = new ColorDialog(fillColorButton.getShell());
                dialog.open();
                RGB rgb = dialog.getRGB();
                if (rgb != null) {
                    fillColor = new Color(rgb.red, rgb.green, rgb.blue);
                }
            } else if (widget.equals(rectangleRadio) || widget.equals(roundRectangleRadio)
                    || widget.equals(ellipseRadio)) {
                boolean selection = rectangleRadio.getSelection();
                if (selection) {
                    type = RectangleEllipseBoxPrinter.RECTANGLE;
                }
                selection = roundRectangleRadio.getSelection();
                if (selection) {
                    type = RectangleEllipseBoxPrinter.ROUNDEDRECTANGLE;
                }
                selection = ellipseRadio.getSelection();
                if (selection) {
                    type = RectangleEllipseBoxPrinter.ELLIPSE;
                }
            }
        }

        public float getLineWidth() {
            return lineWidth;
        }

        public int getLineAlpha() {
            return lineAlpha;
        }

        public Color getLineColor() {
            return lineColor;
        }

        public int getFillAlpha() {
            return fillAlpha;
        }

        public Color getFillColor() {
            return fillColor;
        }

        public int getType() {
            return type;
        }

    }

}
