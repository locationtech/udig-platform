package net.refractions.udig.ui.graphics;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

/**
 * This class contains the settings for a table, including the minimum widths of
 * columns and maximum width (ratio) of each column with respect to the entire table.
 * <p>
 * 
 * </p>
 * 
 * @author chorner
 * @since 1.0.1
 * @see net.refractions.udig.ui.graphics.TableUtils
 */
public class TableSettings {
    private static final int defaultMinPixels = 60; //default minimum width of a column
    
    /** This is a miniature state machine for how we should behave */
    private int currentMode = 0; 
    
    /**
     * List containing the minimum width of each column (in pixels)
     */
    private ArrayList<Integer> minPixels;

    /**
     * List containing the maximum width of each column (in percent, where 1.0 = 100%)
     */
    private ArrayList<Double> maxPercent;
    
    private int numColumns;
    
    /**
     * Constructor for the TableSettings object
     * @param table
     */
    public TableSettings(Table table) {
        this(table.getColumnCount());
    }

    public TableSettings(Tree tree) {
        this(tree.getColumnCount());
    }
    
    public TableSettings(int columnCount) {
        numColumns = columnCount;
        minPixels = new ArrayList<Integer>();
        maxPercent = new ArrayList<Double>();
        double defaultMaxPercent = 1.0 / numColumns;
        //iterate through each column in the array, and set the values
        for (int i = 0; i < numColumns; i++) {
            minPixels.add(i, defaultMinPixels);
            maxPercent.add(i, defaultMaxPercent);
        }
    }

    
    
    /**
     * sets the minimum pixel width of the column
     *
     * @param columnIndex integer representing the column index (first column = 0, second = 1, ...) 
     * @param numPixels
     */
    public void setColumnMin(int columnIndex, int numPixels) {
        minPixels.set(columnIndex, numPixels);
    }
    
    /**
     * returns the minimum pixel width of the column
     *
     * @param columnIndex integer representing the column index (first column = 0, second = 1, ...) 
     */
    public int getColumnMin(int columnIndex) {
        return minPixels.get(columnIndex);
    }

    /**
     * sets the maximum width of the column, as a percentage ratio (0-->1)
     *
     * @param columnIndex integer representing the column index (first column = 0, second = 1, ...) 
     * @param widthPercent value between 0 and 1, where 1 is 100%
     */
    public void setColumnMax(int columnIndex, double widthPercent) {
        maxPercent.set(columnIndex, widthPercent);
    }
    
    /**
     * returns the maximum width of the column, as a percentage ratio (0-->1)
     *
     * @param columnIndex integer representing the column index (first column = 0, second = 1, ...) 
     */
    public double getColumnMax(int columnIndex) {
        return maxPercent.get(columnIndex);
    }

    /**
     * Returns the number of columns in the table
     *
     * @return the number of columns in the table that was initially passed to the constructor 
     */
    public int getColumnCount() {
        return numColumns;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(int currentMode) {
        this.currentMode = currentMode;
    }
}