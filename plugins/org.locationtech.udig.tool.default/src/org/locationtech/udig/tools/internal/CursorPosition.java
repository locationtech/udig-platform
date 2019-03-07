/* uDig - User Friendly Desktop Internet GIS client
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.internal.command.navigation.SetViewportCenterCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.AbstractTool;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.ui.PlatformGIS;
import org.apache.commons.lang.StringUtils;
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
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A CursorPosition tool displays the current Cursor position in map coordinates on the Statusbar
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class CursorPosition extends AbstractTool {
    private static final String ID = "CURSOR_POSITION_LABEL"; //$NON-NLS-1$

    /**
     * Creates an new instance of CursorPosition
     */
    public CursorPosition() {
        super(MOTION);
    }

    @Override
    public void setContext(IToolContext tools) {
        super.setContext(tools);
        PlatformGIS.syncInDisplayThread(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                getLabel();
            }
        });
    }

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
            textArea.addKeyListener(this);
            textArea.addFocusListener(this);
            if (position != null)
                textArea.setText(getString(position));
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
                if (Platform.OS_MACOSX == Platform.getOS())
                    data[i].setHeight(12);
                else
                    data[i].setHeight(10);
            }
            textArea2.setFont(new Font(textArea2.getDisplay(), data));
        }

        public void keyPressed(KeyEvent e) {
            // do nothing
        }

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

        public void focusGained(FocusEvent e) {
            int end = textArea.getText().length();
            textArea.setSelection(0, end);
        }

        public void focusLost(FocusEvent e) {
            // do nada
        }

    }

    /**
     * transforms a String value to a Coordinate considering Locale setting and the supplied crs.
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

        modifiedvalue = StringUtils.removeStart(modifiedvalue.trim(), "(");
        modifiedvalue = StringUtils.removeStart(modifiedvalue.trim(), "[");
        modifiedvalue = StringUtils.removeEnd(modifiedvalue.trim(), ")");
        modifiedvalue = StringUtils.removeEnd(modifiedvalue.trim(), "]");

        String[] components = StringUtils.split(modifiedvalue, decimalSeparator == ',' ? " " : ","); //$NON-NLS-1$
        if (components.length == 1) {
            components = StringUtils.split(modifiedvalue, " "); //$NON-NLS-1$
        }
        if (components.length == 1) {
            components = StringUtils.split(modifiedvalue, ",");
        }
        if (components.length <= 1) {
            return null;
        }

        try {
            components[0] = StringUtils.stripEnd(components[0].trim(), ", ");
            double arg1 = components[0].contains(".") ? Double.parseDouble(components[0])
                    : NumberFormat.getInstance().parse(components[0]).doubleValue();

            components[1] = StringUtils.stripEnd(components[1].trim(), ", ");
            double arg0 = components[1].contains(".") ? Double.parseDouble(components[1])
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
        } catch (NumberFormatException e) {
            return null;
        } catch (ParseException e1) {
            return null;
        } catch (Exception e1) {
            return null;
        }
    }

    /**
     * transforms coordinate to String
     * 
     * @param coord
     * @return
     */
    public static String getString(Coordinate coord) {
        String value = getString(coord.x) + " " + getString(coord.y); //$NON-NLS-1$
        return value;
    }

    private static String getString(double value) {
        if (Double.isNaN(value)) {
            return Messages.CursorPosition_not_a_number;
        }

        if (Double.isInfinite(value)) {
            return Messages.CursorPosition_infinity;
        }

        DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        format.setMaximumFractionDigits(4);
        format.setMinimumIntegerDigits(1);
        format.setGroupingUsed(false);
        String string = format.format(value);

        String[] parts = string.split("\\.");
        if (parts.length > 3) {
            string = parts[0];
        }
        return string;
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

}
