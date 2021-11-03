/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.internal;

import java.awt.Point;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.geometry.jts.JTS;
import org.geotools.measure.AngleFormat;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.internal.command.navigation.SetViewportCenterCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.AbstractTool;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A CursorPosition tool displays the current Cursor position in map coordinates on the StatusBar
 *
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class CursorPosition extends AbstractTool {
    private static final String ID = "CURSOR_POSITION_LABEL"; //$NON-NLS-1$

    private static final AngleFormat LAT_ANGLE_FORMAT = new AngleFormat("DD°MM'SS.ss"); //$NON-NLS-1$

    private static final AngleFormat LON_ANGLE_FORMAT = new AngleFormat("DDD°MM'SS.ss"); //$NON-NLS-1$

    /**
     * Creates an new instance of CursorPosition
     */
    public CursorPosition() {
        super(MOTION);
    }

    @Override
    public void setContext(IToolContext tools) {
        super.setContext(tools);
        PlatformGIS.syncInDisplayThread(() -> getLabel());
    }

    @Override
    public void mouseMoved(final MapMouseEvent e) {
        final LineItem label = getLabel();
        if (label == null)
            return;
        Point screen = e.getPoint();
        Coordinate world = getContext().pixelToWorld(screen.x, screen.y);
        if (world == null)
            return;
        label.setPosition(world);
    }

    LineItem getLabel() {
        if (getContext().getActionBars() == null)
            return null;
        IStatusLineManager bar = getContext().getActionBars().getStatusLineManager();
        if (bar == null) {
            return null;
        }
        LineItem item = (LineItem) bar.find(ID);
        if (item == null) {
            item = new LineItem(ID);
            bar.appendToGroup(StatusLineManager.END_GROUP, item);
            bar.update(true);
        }

        return item;
    }

    @Override
    public void mouseDragged(MapMouseEvent e) {
        mouseMoved(e);
    }

    private class LineItem extends ContributionItem implements KeyListener, FocusListener {
        private static final double ACCURACY = 0.0000001;

        private Text textArea;

        Coordinate position;

        LineItem(String id) {
            super(id);
        }

        /**
         * @see org.eclipse.jface.action.IContributionItem#isDynamic()
         */
        @Override
        public boolean isDynamic() {
            return true;
        }

        public void setPosition(Coordinate coord) {
            if (position != null && Math.abs(position.x - coord.x) < ACCURACY
                    && Math.abs(position.y - coord.y) < ACCURACY) {
                return;
            }
            position = coord;

            if (textArea != null && !textArea.isDisposed()) {
                textArea.setText(getString(coord));
            }
        }

        @Override
        public void fill(Composite parent) {
            Label separator = new Label(parent, SWT.SEPARATOR);
            StatusLineLayoutData data = new StatusLineLayoutData();
            data.widthHint = 1;
            data.heightHint = 15;
            separator.setLayoutData(data);
            textArea = new Text(parent, SWT.BORDER | SWT.CENTER);
            textArea.setEditable(false);
            if (position != null) {
                textArea.setText(getString(position));
            }
            textArea.setToolTipText(Messages.CursorPosition_tooltip);
            setFont(textArea);
            data = new StatusLineLayoutData();

            data.widthHint = 200;
            data.heightHint = 15;
            textArea.setLayoutData(data);
        }

        void setFont(Control textArea2) {
            Display display = textArea2.getDisplay();
            FontData[] data = display.getFontList("courier", true); //$NON-NLS-1$
            if (data.length < 1) {
                data = textArea2.getFont().getFontData();
            }
            for (int i = 0; i < data.length; i++) {
                if (Platform.OS_MACOSX.equals(Platform.getOS())) {
                    data[i].setHeight(12);
                } else {
                    data[i].setHeight(10);
                }
            }
            textArea2.setFont(new Font(textArea2.getDisplay(), data));
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // do nothing
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.character == SWT.Selection) {
                go();
            } else if (e.character == SWT.ESC) {
                textArea.setText(getString(position));
            }

        }

        private void go() {
            Coordinate newpos = parse(textArea.getText(), getContext().getCRS());
            if (Math.abs(newpos.x - position.x) > ACCURACY
                    || Math.abs(newpos.y - position.y) > ACCURACY) {
                setPosition(newpos);
                Command c = new SetViewportCenterCommand(newpos);
                getContext().sendASyncCommand(c);
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
            int end = textArea.getText().length();
            textArea.setSelection(0, end);
        }

        @Override
        public void focusLost(FocusEvent e) {
            // do nothing
        }

    }

    /**
     * Transforms a string value to a Coordinate considering Locale setting and the supplied CRS.
     *
     * @param value
     * @param crs
     * @return
     */
    public static Coordinate parse(String value, CoordinateReferenceSystem crs) {

        char decimalSeparator = DecimalFormatSymbols.getInstance(Locale.getDefault())
                .getDecimalSeparator();

        String modifiedvalue = value.trim();
        boolean latlong = false;
        String upperCase = modifiedvalue.toUpperCase();
        String tmp = modifiedvalue;
        modifiedvalue = stripCode(modifiedvalue, upperCase);
        if (tmp.length() != modifiedvalue.length())
            latlong = true;

        modifiedvalue = StringUtils.removeStart(modifiedvalue.trim(), "("); //$NON-NLS-1$
        modifiedvalue = StringUtils.removeStart(modifiedvalue.trim(), "["); //$NON-NLS-1$
        modifiedvalue = StringUtils.removeEnd(modifiedvalue.trim(), ")"); //$NON-NLS-1$
        modifiedvalue = StringUtils.removeEnd(modifiedvalue.trim(), "]"); //$NON-NLS-1$

        String[] components = StringUtils.split(modifiedvalue, decimalSeparator == ',' ? " " : ","); //$NON-NLS-1$ //$NON-NLS-2$
        if (components.length == 1) {
            components = StringUtils.split(modifiedvalue, " "); //$NON-NLS-1$
        }
        if (components.length == 1) {
            components = StringUtils.split(modifiedvalue, ","); //$NON-NLS-1$
        }
        if (components.length <= 1) {
            return null;
        }

        try {
            components[0] = StringUtils.stripEnd(components[0].trim(), ", "); //$NON-NLS-1$
            double arg1 = components[0].contains(".") ? Double.parseDouble(components[0]) //$NON-NLS-1$
                    : NumberFormat.getInstance().parse(components[0]).doubleValue();

            components[1] = StringUtils.stripEnd(components[1].trim(), ", "); //$NON-NLS-1$
            double arg0 = components[1].contains(".") ? Double.parseDouble(components[1]) //$NON-NLS-1$
                    : NumberFormat.getInstance().parse(components[1]).doubleValue();
            Coordinate coord = new Coordinate(arg1, arg0);
            if (latlong && crs != null) {
                try {
                    JTS.transform(coord, coord,
                            CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs, true));
                } catch (Exception e) {
                    ToolsPlugin.log(Messages.CursorPosition_transformError, e);
                }
            }
            return coord;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Transforms coordinate to String
     *
     * @param coord
     * @return
     */
    public static String getString(Coordinate coord) {
        return formatCoordinate(true, coord.y) + " " + formatCoordinate(false, coord.x); //$NON-NLS-1$
    }

    /**
     *
     * @param modifiedvalue
     * @param upperCase
     * @return
     */
    private static String stripCode(String modifiedvalue, String upperCase) {
        String code = "LL"; //$NON-NLS-1$
        if (upperCase.endsWith(code)) {
            return modifiedvalue.substring(0, modifiedvalue.length() - code.length());
        }
        code = "L L"; //$NON-NLS-1$
        if (upperCase.endsWith(code)) {
            return modifiedvalue.substring(0, modifiedvalue.length() - code.length());
        }
        code = "LATLONG"; //$NON-NLS-1$
        if (upperCase.endsWith(code)) {
            return modifiedvalue.substring(0, modifiedvalue.length() - code.length());
        }
        code = "LAT LONG"; //$NON-NLS-1$
        if (upperCase.endsWith(code)) {
            return modifiedvalue.substring(0, modifiedvalue.length() - code.length());
        }
        code = "LAT LON"; //$NON-NLS-1$
        if (upperCase.endsWith(code)) {
            return modifiedvalue.substring(0, modifiedvalue.length() - code.length());
        }
        return modifiedvalue;
    }

    private static String formatCoordinate(boolean isLatitude, double value) {
        double finalvalue;
        if (isLatitude && Math.abs(value) > 90.0) {
            finalvalue = 90.0;
        } else {
            double vector = getVector(value);
            finalvalue = value + vector;
        }
        StringBuilder sb = new StringBuilder();
        if (isLatitude) {
            sb.append(LAT_ANGLE_FORMAT.format(Math.abs(finalvalue)));
            if (value > 0) {
                sb.append("N"); //$NON-NLS-1$
            } else {
                sb.append("S"); //$NON-NLS-1$
            }
        } else {
            sb.append(LON_ANGLE_FORMAT.format(Math.abs(finalvalue)));
            if (finalvalue > 0) {
                sb.append("E"); //$NON-NLS-1$
            } else {
                sb.append("W"); //$NON-NLS-1$
            }
        }
        return sb.toString();
    }

    /**
     * @param longValue Longitude-Value to get vector for
     * @return vector to move X-valid into valid WGS84 Bounds
     */
    public static double getVector(final double longValue) {
        return -1 * (longValue - (euclideanMod(longValue + 180., 360.) - 180.));
    }

    static double euclideanMod(final double x, final double y) {
        double r = Math.abs(x) % Math.abs(y);
        // apply the sign of dividend and make sure the remainder is positive number
        r *= Math.signum(x);
        r = (r + Math.abs(y)) % Math.abs(y);
        return r;
    }
}
