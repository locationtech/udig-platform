/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.mapgraphic.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

/**
 * Controls the behavior of the Spinner and Combo for setting the horizontal or vertical
 * spacing of lines.
 * 
 * @author Jesse
 * @since 1.1.0
 */
class SpacerController {
    
    private final Spinner spinner;
    private final Combo unit;

    /** this information should be coming out of the java.units package */
    static final String PIXELS = "pixels"; //$NON-NLS-1$
    static final String DEGREES = "degrees"; //$NON-NLS-1$
    static final String MINUTES = "minutes"; //$NON-NLS-1$
    static final String SECONDS = "seconds"; //$NON-NLS-1$

    static final String[] UNITS = new String[]{PIXELS, DEGREES, MINUTES, SECONDS};
    
    public SpacerController( final Spinner spinner, final Combo unit ) {
        super();
        this.spinner = spinner;
        this.unit = unit;
        this.unit.setItems(UNITS);
        this.unit.select(0);

    }

    public Spinner getSpinner() {
        return spinner;
    }

    public Combo getUnit() {
        return unit;
    }

    public static double spinnerUnit( int digitsInSpinner ) {
        return Math.pow(10, digitsInSpinner);
    }

    private void setSpinner(int inc, int max, int digits, int pageinc) {
        spinner.setIncrement(inc);
        spinner.setMaximum(max);
        spinner.setDigits(digits);
        spinner.setPageIncrement(pageinc);
    }
    

    /**
     * Sets the spinner to the correct setting for the unit.  Sets the incrememnt,
     * maximum value, digits and pageincrement values on the spinner.
     */
    public void setSpinnerProperties( ) {
        if( GridStyleConfigurator.selectedString(unit).equals(SpacerController.PIXELS)){
            setSpinner(1,Integer.MAX_VALUE, 0,10);
        }else if( GridStyleConfigurator.selectedString(unit).equals(SpacerController.MINUTES)){
            setSpinner((int) (spinnerUnit(4)),(int) (360*60*spinnerUnit(4)), 4,60);
        }else if( GridStyleConfigurator.selectedString(unit).equals(SpacerController.SECONDS)){
            setSpinner((int) spinnerUnit(2),(int) (360*3600*spinnerUnit(2)), 2,3600);
        }else if( GridStyleConfigurator.selectedString(unit).equals(SpacerController.DEGREES)){
            setSpinner((int) spinnerUnit(6),(int) (360*spinnerUnit(6)), 6,10);
        }
    }

    /**
     * Updates otherSpacer to be in the same unit as this if it is in pixel space.  
     * If not it leave it alone.
     *
     * @return true if it updated the other spacer.
     */
    public boolean validateWorld(SpacerController otherSpacer) {
        if( otherSpacer.isPixelsSelected() ){
            otherSpacer.getUnit().select(unit.getSelectionIndex());
            return true;
        }else{
            return false;
        }
    }

    /**
     * Updates otherSpacer to be in the same unit as this if it is in world space.  If not it leave it alone.
     *
     * @param otherSpacer 
     * @return true if it updated the other spacer.
     */
    public boolean validatePixels(SpacerController otherSpacer) {
        if( !otherSpacer.isPixelsSelected() ){
            otherSpacer.getUnit().select(otherSpacer.getUnit().indexOf(PIXELS));
            return true;
        }
        return false;
    }

    /**
     * Returns true if this spacer is in Pixel Units.
     *
     * @return true if this spacer is in Pixel Units.
     */
    public boolean isPixelsSelected( ) {
        return GridStyleConfigurator.selectedString(getUnit()).equals(PIXELS);
    }

    /**
     * adds listener to unit and spinner as for Modify event listeners ;  
     * Adds modify listener to unit.
     *
     * @param modifyListener
     * @param listener
     */
    public void addListeners( ModifyListener modifyListener, Listener listener ) {
        unit.addModifyListener(modifyListener);
        unit.addListener(SWT.Modify, listener);
        spinner.addListener(SWT.Modify, listener);
        spinner.addListener(SWT.KeyUp, listener);

    }
    
    /**
     * Removes listeners from unit and spinner.
     *
     * @param modifyListener
     * @param listener
     */
    public void removeListeners( ModifyListener modifyListener, Listener listener ) {
        unit.removeModifyListener(modifyListener);
        unit.removeListener(SWT.Modify, listener);
        spinner.removeListener(SWT.Modify, listener);
        spinner.removeListener(SWT.KeyUp, listener);
    }
    
    /**
     * Sets the unit and spinner in this spacer to the correct value.  If the spacer is 
     * currently in pixel space then it will be changed to degrees.
     *
     * @param value value in DEGREES
     */
    public void setWorldSpacing( double value ) {
        double unitValue = spinnerUnit(spinner.getDigits());
        if (GridStyleConfigurator.selectedString(unit).equals(SpacerController.DEGREES)) {
            spinner.setSelection((int) (value * unitValue));
        } else if (GridStyleConfigurator.selectedString(unit).equals(SpacerController.MINUTES)) {
            spinner.setSelection((int) (value * 60 * unitValue));
        } else if (GridStyleConfigurator.selectedString(unit).equals(SpacerController.SECONDS)) {
            spinner.setSelection((int) (value * 3600 * unitValue));
        } else if (GridStyleConfigurator.selectedString(unit).equals(SpacerController.PIXELS)) {
            unit.select(unit.indexOf(SpacerController.DEGREES));
            setSpinnerProperties();
            spinner.setSelection((int) value);
        }
    }

    /**
     * Sets this spacer to be in pixel space and set the value of the spinner.
     * @param spacing the value to set on the spinner
     */
    public void setPixelSpacing( double spacing ) {
        unit.select(unit.indexOf(PIXELS));
        setSpinnerProperties();
        spinner.setSelection((int) spacing);
    }

    public double getSpacing( ) {
        double selection = (double) spinner.getSelection() / spinnerUnit(spinner.getDigits());
        if (GridStyleConfigurator.selectedString(unit).equals(SpacerController.MINUTES)) {
            return selection / 60;
        }
        if (GridStyleConfigurator.selectedString(unit).equals(SpacerController.SECONDS)) {
            return selection / 3600;
        }
        return selection;
    }


}
