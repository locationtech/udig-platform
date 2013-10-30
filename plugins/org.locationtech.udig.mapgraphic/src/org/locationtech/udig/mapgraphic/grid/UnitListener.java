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

import net.refractions.udig.mapgraphic.internal.Messages;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * Verifies that the unit chosen is compatible with the other Unit Combo. The idea is that is that
 * both units must be compatible. You can't have one be minutes and the other be pixels. This
 * listener will update the other combo and set the message to explain why.
 * 
 * @see GridStyleConfigurator
 * @author Jesse
 * @since 1.1.0
 */
public class UnitListener implements ModifyListener {

    private Label message;
    private SpacerController otherSpacer;
    private SpacerController thisSpacer;

    public UnitListener( SpacerController otherSpacer, SpacerController thisSpacer, Label message ) {
        this.otherSpacer = otherSpacer;
        this.message = message;
        this.thisSpacer = thisSpacer;
    }

    public void modifyText( ModifyEvent e ) {
        if( thisSpacer.isPixelsSelected() ){
            if( thisSpacer.validatePixels(otherSpacer) ){
                warningMessage();
            }else{
                message.setText(""); //$NON-NLS-1$
            }
        }else{
            if( thisSpacer.validateWorld(otherSpacer) ){
                warningMessage();
            }else{
                message.setText(""); //$NON-NLS-1$
            }
        }

        thisSpacer.setSpinnerProperties();
        converSpinnerValueToNewUnit();
    }

    private void warningMessage() {
        message.setText(Messages.UnitListener_MixedUnits);
    }

    /**
     * Updates the spinner value so that it is the equivalent value for the newly selected unit.
     */
    private void converSpinnerValueToNewUnit( ) {
        String oldValue = (String) thisSpacer.getUnit().getData();
        if( GridStyleConfigurator.selectedString(thisSpacer.getUnit()).equals(SpacerController.PIXELS)){
            convertSpinner(oldValue, 1, 1, 1, 0);
        }else if( GridStyleConfigurator.selectedString(thisSpacer.getUnit()).equals(SpacerController.MINUTES)){
            convertSpinner(oldValue, 60, 1, 1/(double)60, 4);
        }else if( GridStyleConfigurator.selectedString(thisSpacer.getUnit()).equals(SpacerController.SECONDS)){
            convertSpinner(oldValue, 3600, 60, 1, 2);
        }else if( GridStyleConfigurator.selectedString(thisSpacer.getUnit()).equals(SpacerController.DEGREES)){
            convertSpinner(oldValue, 1, 1/(double)60, 1/(double)3600, 6);
        }
        
        thisSpacer.getUnit().setData(GridStyleConfigurator.selectedString(thisSpacer.getUnit()));
    }

    /**
     * Converts the spinner value to a new value that is valid for the current unit.
     */
    void convertSpinner( String oldUnit, double fromDegree, double fromMinute, double fromSeconds, int digits ) {
        Spinner spinner = thisSpacer.getSpinner();
        double selection = spinner .getSelection();
        if(oldUnit==null )
            oldUnit=SpacerController.PIXELS;
        
        if ( oldUnit.equals(SpacerController.DEGREES )){
            double power = SpacerController.spinnerUnit(digits-6);
            int newValue = (int) (selection*power * fromDegree);
            spinner.setSelection(newValue);
        }else if (oldUnit.equals(SpacerController.SECONDS)){
            double power = SpacerController.spinnerUnit(digits-2);
            int newValue = (int) (selection*power*fromSeconds);
            spinner.setSelection(newValue);
        }else if (oldUnit.equals(SpacerController.MINUTES)){
            double power = SpacerController.spinnerUnit(digits-4);
            
            int newValue = (int) (selection*power*fromMinute);
            spinner.setSelection(newValue);
        }else if (oldUnit.equals(SpacerController.PIXELS)){
            int power = (int) SpacerController.spinnerUnit(digits);
            if( selection>360 ){
                spinner.setSelection(25*power);
            }else{
                spinner.setSelection((int) (selection*power));
            }
        }
    }
    
}
