/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.internal.editor;

import java.awt.Dimension;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.printing.model.AbstractBoxPrinter;
import org.locationtech.udig.printing.model.Page;
import org.locationtech.udig.printing.model.impl.MapBoxPrinter;
import org.locationtech.udig.printing.ui.IBoxEditAction;
import org.locationtech.udig.printing.ui.internal.editor.parts.BoxPart;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.render.IViewportModel;

/**
 * Sets the scale on a map in the MapBox
 *
 * <p>
 * The scale is calculated and set taking into account the real dimension of the currently used
 * paper type.
 * </p>
 *
 * @author jesse
 * @author Andrea Antonello (www.hydrologis.com)
 * @since 1.1.0
 */
public class SetScaleAction implements IBoxEditAction {

    private BoxPart owner;

    Double scale;

    @Override
    public Command getCommand() {
        final double newScale = scale;
        scale = null;

        final double oldScale = calculateScale();
        return new Command() {
            private void setScale(double scale) {
                applyNewScale(scale);
            }

            @Override
            public void execute() {
                setScale(newScale);
            }

            @Override
            public void undo() {
                setScale(oldScale);
            }
        };
    }

    @Override
    public void init(BoxPart owner) {
        this.owner = owner;
    }

    private MapBoxPrinter getMapBoxPrinter() {
        return (MapBoxPrinter) owner.getBoxPrinter();
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void perform() {
        scale = null;

        double scaleDenominator = calculateScale();

        Shell parentShell = Display.getCurrent().getActiveShell();
        if (scaleDenominator < .01) {
            MessageDialog.openInformation(parentShell, "Set Scale",
                    "The map scale cannot be accurately calculated because of the map's projection.\n\nTry Changing the projection of the map");
            return;
        }

        ScaleDialog dialog = new ScaleDialog(parentShell, scaleDenominator);
        dialog.setBlockOnOpen(true);

        int result = dialog.open();

        if (result == Window.OK) {
            this.scale = dialog.getScale();
        } else {
            this.scale = scaleDenominator;
        }

    }

    /**
     * Calculates the scale of the map taking into account the paper size.
     *
     * @return the scale of the map on the current paper type.
     */
    private double calculateScale() {
        IViewportModel viewportModel = getMapBoxPrinter().getMap().getViewportModel();
        // get page size in pixels and in mm
        Page page = getMapBoxPrinter().getBox().getPage();
        org.eclipse.draw2d.geometry.Dimension pageSize = page.getSize();
        org.eclipse.draw2d.geometry.Dimension paperSize = page.getPaperSize();
        // map box size in pixels
        org.eclipse.draw2d.geometry.Dimension mapSize = owner.getBoxPrinter().getBox().getSize();
        // map box size in points and then in meters on paper
        float mapPaperWidthPoints = (float) paperSize.width * (float) mapSize.width
                / pageSize.width;
        float mapPaperWidthMeters = AbstractBoxPrinter.point2cm(mapPaperWidthPoints) / 100f;
        // map bounds in meters
        ReferencedEnvelope mapEnvelope = viewportModel.getBounds();
        double mapWorldWidthMeters = mapEnvelope.getWidth();

        double thescale = mapWorldWidthMeters / mapPaperWidthMeters;
        return thescale;
    }

    /**
     * Applies the supplied scale on the map on the current type of paper.
     *
     * @param scale the scale to apply to the map.
     */
    private void applyNewScale(double scale) {
        ViewportModel viewportModel = getMapBoxPrinter().getMap().getViewportModelInternal();
        // get page size in pixels and in mm
        Page page = getMapBoxPrinter().getBox().getPage();
        org.eclipse.draw2d.geometry.Dimension pageSize = page.getSize();
        org.eclipse.draw2d.geometry.Dimension paperSize = page.getPaperSize();
        // map box size in pixels
        org.eclipse.draw2d.geometry.Dimension mapSize = owner.getBoxPrinter().getBox().getSize();
        // map box size in points and then in meters on paper
        float mapPaperWidthPoints = (float) paperSize.width * (float) mapSize.width
                / pageSize.width;
        float mapPaperWidthMeters = AbstractBoxPrinter.point2cm(mapPaperWidthPoints) / 100f;
        float mapPaperHeightPoints = (float) paperSize.height * (float) mapSize.height
                / pageSize.height;
        float mapPaperHeightMeters = AbstractBoxPrinter.point2cm(mapPaperHeightPoints) / 100f;

        double mapWorldWidthMeters = scale * mapPaperWidthMeters;
        double mapWorldHeightMeters = scale * mapPaperHeightMeters;

        // current map bounds in meters
        ReferencedEnvelope mapEnvelope = viewportModel.getBounds();
        double centerX = (mapEnvelope.getMaxX() + mapEnvelope.getMinX()) / 2.0;
        double centerY = (mapEnvelope.getMaxY() + mapEnvelope.getMinY()) / 2.0;

        double minX = centerX - mapWorldWidthMeters / 2.0;
        double maxX = centerX + mapWorldWidthMeters / 2.0;
        double minY = centerY - mapWorldHeightMeters / 2.0;
        double maxY = centerY + mapWorldHeightMeters / 2.0;

        viewportModel.setBounds(minX, maxX, minY, maxY);
    }

    private Dimension getDisplaySize() {
        org.eclipse.draw2d.geometry.Dimension gefDim = owner.getBoxPrinter().getBox().getSize();
        Dimension size = new Dimension(gefDim.width, gefDim.height);
        return size;
    }

    /**
     * A dialog with a Spinner for setting the scale of a map.
     *
     * @author jesse
     * @since 1.1.0
     */
    private static class ScaleDialog extends Dialog implements Listener {

        private Spinner spinner;

        private double scale;

        protected ScaleDialog(Shell parentShell, double scale) {
            super(parentShell);
            this.scale = scale;
            setShellStyle(SWT.CLOSE | SWT.TITLE);
        }

        @Override
        protected Point getInitialSize() {
            Point tmp = super.getInitialSize();
            return new Point(200, tmp.y);
        }

        @Override
        protected Control createContents(Composite parent) {
            getShell().setText("Set Scale");

            Composite container = new Composite(parent, SWT.NONE);
            container.setLayoutData(new GridData(GridData.FILL_BOTH));
            container.setLayout(new FillLayout());

            spinner = new Spinner(container, SWT.BORDER);
            spinner.setMinimum(1);
            spinner.setMaximum(Integer.MAX_VALUE);
            spinner.setIncrement(1000);
            spinner.setPageIncrement(1000000);
            spinner.setSelection((int) scale);

            spinner.addListener(SWT.Modify, this);
            spinner.addListener(SWT.KeyUp, this);
            return container;
        }

        @Override
        public void handleEvent(Event event) {
            if (event.type == SWT.Modify) {
                this.scale = spinner.getSelection();
            } else if (event.type == SWT.KeyUp) {
                if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR
                        || event.keyCode == SWT.LF) {
                    okPressed();
                }
            }
        }

        public double getScale() {
            return scale;
        }

    }

}
