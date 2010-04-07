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
package net.refractions.udig.ui.graphics;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Utility class for dealing with the resizing of table columns.
 * <p>
 * 
 * </p>
 * 
 * @author chorner
 * @since 1.0.1
 */
public class TableUtils {
	// Considerations for future modifications:
	// -- keep the table somewhat stable
	// -- keep content visible
	// -- don't annoy the user with chaotic resizing (which overrides what the
	// user does)

	/**
	 * The columns are left as is unless they exceed the bounds set by the
	 * TableSettings.
	 */
	public static final int MODE_LAZY = 1;

	/**
	 * Columns are automatically sized, and adjusted to fit into the
	 * TableSettings bounds.
	 */
	public static final int MODE_AUTO = 2;

	/**
	 * We start off with MODE_AUTO, but switch to MODE_LAZY when the user
	 * modifies a column size (listener required).
	 */
	public static final int MODE_JUMP = 3;

	/**
	 * Given a table, this method resizes the columns as specified. Although
	 * automatic column sizing works well under Linux , it
	 * does not work well under Windows and Mac OSX and hence this method is needed (in
	 * particular, an empty column on the right appears under Win).
	 * 
	 * @param table
	 * @param settings
	 *            the configured settings for the table (column min, max, etc)
	 * @param mode one of {@link #MODE_AUTO}, {@link #MODE_LAZY} , {@link #MODE_JUMP} 
	 */
	public static void resizeColumns(Table table, TableSettings settings,
			int mode) {
        int mode2=mode;
		// turn off redraw (reduces flicker)
		table.setRedraw(false);

		int tableWidth = table.getSize().x;
		int columnCount = table.getColumnCount();
		int columnSum = 0;

		// load/save the current calling mode
		if (!(mode2 > 0)) {
			mode2 = MODE_JUMP; // mode wasn't defined; set a default
		}

		// oldMode is only set:
		// a) when this method is called for the first time
		// b) when the listener detects a user column-resize <-- NOT IMPLEMENTED

		// MODES:
		// MODE_JUMP --> MODE_AUTO
		// until the user manually-resizes, then:
		// MODE_JUMP --> MODE_LAZY

		int oldMode = settings.getCurrentMode();
		if (oldMode == 0) {
			settings.setCurrentMode(mode2); // store the mode
		}
		if (mode2 == MODE_JUMP) { // hey! we'll actually use the oldMode
			if (oldMode != mode2) {
				mode2 = oldMode; // oldMode should be MODE_LAZY
			} else {
				mode2 = MODE_AUTO;
			}
		}

		// iterate through each column in the array, and set the size as
		// appropriate
		for (int i = 0; i < columnCount - 1; i++) {
			int minWidth = (int) settings.getColumnMin(i);
			int maxWidth = (int) (settings.getColumnMax(i) * tableWidth);
			TableColumn column = table.getColumn(i);
			if (mode2 == MODE_AUTO)
				column.pack(); // automatically resize the column
			int width = column.getWidth();
            // check the values
			if (width < minWidth) {
				// ensure the table isn't incredibly small
				if (!(columnCount * minWidth > tableWidth)) {
					// column is too small, and the table is a reasonable size,
					// so resize it
					column.setWidth(minWidth);
				}
			} else if (width > maxWidth) {
				// too big
				column.setWidth(maxWidth);
			}
			columnSum += width;
		}

		// treat the last column a little bit differently (just fill in the
		// remaining space)
		TableColumn column = table.getColumn(columnCount - 1);
		column.setWidth(tableWidth - columnSum - (2 * table.getBorderWidth()));

		// TODO: verify that 2*borderWidth is the correct adjustment for MacOS
		// and Linux (Looks good on Win)

		// turn redraw back on
		table.setRedraw(true);
	}

    public static void resizeColumns(Tree treeTable, TableSettings settings,
            int mode) {
        int mode2=mode;
        // turn off redraw (reduces flicker)
        treeTable.setRedraw(false);

        int tableWidth = treeTable.getSize().x;
        int columnCount = treeTable.getColumnCount();
        int columnSum = 0;

        // load/save the current calling mode
        if (!(mode2 > 0)) {
            mode2 = MODE_JUMP; // mode wasn't defined; set a default
        }

        // oldMode is only set:
        // a) when this method is called for the first time
        // b) when the listener detects a user column-resize <-- NOT IMPLEMENTED

        // MODES:
        // MODE_JUMP --> MODE_AUTO
        // until the user manually-resizes, then:
        // MODE_JUMP --> MODE_LAZY

        int oldMode = settings.getCurrentMode();
        if (oldMode == 0) {
            settings.setCurrentMode(mode2); // store the mode
        }
        if (mode2 == MODE_JUMP) { // hey! we'll actually use the oldMode
            if (oldMode != mode2) {
                mode2 = oldMode; // oldMode should be MODE_LAZY
            } else {
                mode2 = MODE_AUTO;
            }
        }

        // iterate through each column in the array, and set the size as
        // appropriate
        for (int i = 0; i < columnCount - 1; i++) {
            int minWidth = (int) settings.getColumnMin(i);
            int maxWidth = (int) (settings.getColumnMax(i) * tableWidth);
            TreeColumn column = treeTable.getColumn(i);
            if (mode2 == MODE_AUTO)
                column.pack(); // automatically resize the column
            // check the values
            if (column.getWidth() < minWidth) {
                // ensure the table isn't incredibly small
                if (!(columnCount * minWidth > tableWidth)) {
                    // column is too small, and the table is a reasonable size,
                    // so resize it
                    column.setWidth(minWidth);
                }
            } else if (column.getWidth() > maxWidth) {
                // too big
                column.setWidth(maxWidth);
            }
            columnSum += column.getWidth();
        }

        // treat the last column a little bit differently (just fill in the
        // remaining space)
        TreeColumn column = treeTable.getColumn(columnCount - 1);
        column.setWidth(tableWidth - columnSum - (2 * treeTable.getBorderWidth()) - 1);

        // TODO: verify that 2*borderWidth is the correct adjustment for MacOS
        // and Linux (Looks good on Win)

        // turn redraw back on
        treeTable.setRedraw(true);
    }

    
	// FIXME: Implement the listener, if we need it
	// public static void createListener(Table table) {
	// table.addControlListener(new ColumnResizeListener());
	// }

	// public class ColumnResizeListener implements ControlListener {
	// public void controlMoved(ControlEvent e) {
	// }
	//
	// public void controlResized(ControlEvent e) {
	// System.out.println("Resized!:"+e.getSource().toString());
	// }
	// }
}
